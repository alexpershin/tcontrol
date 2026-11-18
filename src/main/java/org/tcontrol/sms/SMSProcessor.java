package org.tcontrol.sms;

import com.pi4j.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tcontrol.sms.commands.CommandExecutor;
import org.tcontrol.sms.commands.CommandResult;
import org.tcontrol.sms.commands.STATUS;
import org.tcontrol.config.props.SMSConfig;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
@AllArgsConstructor
public class SMSProcessor {

    final public static Pattern PATTERN_PHONE = Pattern.compile(".*_00_\\+([0-9]*)_[0-9]{2}.txt");
    final public static Pattern PATTERN_NOT_PHONE = Pattern.compile(".*_00_(.*)_[0-9]{2}.txt");
    final public static Pattern PATTERN_TIME = Pattern.compile("IN([0-9]*)_([0-9]*)_00_.*_(0[0-9]).txt");

    private SMSConfig smsConfig;

    private CommandExecutor commandExecutor;

    @PostConstruct
    private void init() {
        log.info("init");
    }

    @Scheduled(cron = "${sms-config.schedule}")
    public void inputSmsScan() {
        log.info("Scanning input SMS to process..");
        processInputFolderSMS();
        log.info("Input SMS processing completed");
    }

    private void processInputFolderSMS() {
        final String inputFolderName = smsConfig.getInputFolder();
        Path path = Paths.get(inputFolderName);

        try (Stream<Path> stream = Files.list(path)) {
            List<Path> files = stream.collect(Collectors.toList());
            for (Path incomingFile : files) {
                if (!Files.isDirectory(incomingFile)) {
                    processIncomingSmsFile(incomingFile);
                }
            }
        } catch (IOException e) {
            log.error("reading file exception: {}", e.getMessage());
        }
    }

    private void processIncomingSmsFile(Path inconingSmsFilePath) throws IOException {
        final String fileName = inconingSmsFilePath.getFileName().toString();
        final Matcher matcher = PATTERN_PHONE.matcher(fileName);
        boolean inList = false;
        String incomingPhoneNumber = "?";

        //try to recognize sender's phone number
        if (matcher.find()) {
            incomingPhoneNumber = matcher.group(1);
            final String incomingPhoneNumberMatch = incomingPhoneNumber;
            inList = smsConfig.getPhones().stream()
                    .anyMatch(phone -> phone.getPhone().equals(incomingPhoneNumberMatch));

            if (inList) {
                processingCommand(inconingSmsFilePath, fileName, incomingPhoneNumber);
            } else {//redirect to first number
                List<String> lines = Files.readAllLines(inconingSmsFilePath, Charset.defaultCharset());
                writeCommandAnswer(fileName, smsConfig.getPhones().get(0).getPhone(), new CommandResult(
                        STATUS.REDIRECT,
                        lines.stream().reduce((a, b) -> a + '\n' + b).orElse("")
                ), "not parsed");
            }

        }

        if (!inList) {
            if (incomingPhoneNumber.equals("?")) {
                //parse if number has not parsed on previous step
                Matcher notPhoneMatcher = PATTERN_NOT_PHONE.matcher(fileName);
                if (notPhoneMatcher.find()) {
                    String phone = notPhoneMatcher.group(1);
                    if (StringUtils.isNotBlank(phone)) {
                        incomingPhoneNumber = phone;
                    }
                }
            }
            if (!smsConfig.getNotForwardList().contains(incomingPhoneNumber)) {
                //forwarding sms from unknown senders
                String text = "Forwarding from (" + incomingPhoneNumber + "):"
                        + "\n" + readMessageTextFromFile(inconingSmsFilePath);
                forward(fileName, incomingPhoneNumber, smsConfig.getForwardingPhone(), text);
            } else {
                //forwarding is banned
                log.info("SMS from {} was not forwarded", incomingPhoneNumber);
            }
        }

        //moving to result folder
        Path resultPath = Paths.get(smsConfig.getProcessedFolder(), inconingSmsFilePath.getFileName().toString());
        Files.move(inconingSmsFilePath, resultPath);
    }

    private void processingCommand(Path inconingSmsFilePath, String fileName, String incomingPhoneNumber) throws IOException {
        Triple<String, String, String> commandParams = readCommandFromFile(inconingSmsFilePath);
        if (commandParams != null) {
            String commandName = commandParams.getLeft();
            String commandParam = commandParams.getMiddle();
            log.info("Command: {}", commandName);
            CommandResult result = commandExecutor.run(commandName, commandParam);
            writeCommandAnswer(fileName, incomingPhoneNumber, result, commandName);
        } else {
            CommandResult result = new CommandResult(
                    STATUS.FAILURE,
                    MessageFormat.format("invalid argument: {0}", commandParams.getRight())
            );
            writeCommandAnswer(fileName, incomingPhoneNumber, result, commandParams.getRight());
        }
    }

    private void writeCommandAnswer(String fileName, String phoneNumber, CommandResult result, String command)
            throws IOException {

        String resultText =
                (result.getStatus() != STATUS.OK ?
                        "Command " + command + " result: " + result.getStatus().name() + "\n"
                        : "")
                        + result.getMessage();

        writeOutSmsFile(fileName, phoneNumber, resultText);
        log.info("SMS answer ready (status:{}, phone:+{})", result.getStatus(), phoneNumber);
    }

    private void forward(String fileName, String incomingNumber, String forwardingPhoneNumber, String resultText)
            throws IOException {
        writeOutSmsFile(fileName, forwardingPhoneNumber, resultText);
        log.info("SMS message forwarded (from:{}, to:+{})", incomingNumber, forwardingPhoneNumber);
    }

    private void writeOutSmsFile(String fileName, String phoneNumber, String resultText) throws IOException {

        final Matcher matcher = PATTERN_TIME.matcher(fileName);
        String date = "";
        String time = "";
        String suffix = "";

        //processing command
        if (matcher.find()) {
            date = matcher.group(1);
            time = matcher.group(2);
            suffix = matcher.group(3);
        }

        String outFileName = "OUT+" + phoneNumber + "." + date + time + suffix + ".txt";
        Path path = Paths.get(smsConfig.getOutputFolder(), outFileName);
        Files.deleteIfExists(path);
        Files.createFile(path);

        Files.write(path, resultText.getBytes());
    }

    private Triple<String, String, String> readCommandFromFile(Path filePath) {
        try {
            String command = null;
            String[] args = new String[]{};
            String commandText = null;
            List<String> lines = Files.readAllLines(filePath, Charset.defaultCharset());
            if (lines.size() == 1) {
                commandText = lines.get(0);
                args = StringUtil.trim(commandText).split(" ");
                if (args.length > 2) {
                    log.info("Error parsing argument of command: {}", commandText);
                }
                command = args[0].toUpperCase();
            } else {
                log.info("Error while found command: {}", lines);
            }
            return Triple.of(command, args.length > 1 ? args[1] : null, commandText);
        } catch (IOException ex) {
            log.info("Error while reading file {}, {}", filePath, ex.getMessage());
        }
        return null;
    }

    private String readMessageTextFromFile(Path filePath) {
        try {
            List<String> lines = Files.readAllLines(filePath, Charset.defaultCharset());
            return lines.stream().reduce((s1, s2) -> s1 + "\n" + s2).orElse("");
        } catch (IOException ex) {
            log.info("Error while reading file: {}, {}", filePath, ex.getMessage());
        }
        return null;
    }

}
