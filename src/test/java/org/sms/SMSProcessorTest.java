package org.sms;

import org.junit.jupiter.api.BeforeEach;
import org.tcontrol.config.props.CommandConfig;
import org.tcontrol.sms.ISMSCommand;
import org.tcontrol.sms.SMSProcessor;
import org.tcontrol.sms.commands.CommandExecutor;
import org.tcontrol.config.props.SMSConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.tcontrol.sms.commands.CommandResult;

import static org.junit.jupiter.api.Assertions.*;

public class SMSProcessorTest {

    public static final String FORWARDING_PHONE = "79221111111";
    private SMSProcessor smsProcessor;
    private SMSConfig smsConfig;

    @BeforeEach
    public void before() throws IOException {
        smsConfig = new SMSConfig();
        smsConfig.setForwardingPhone(FORWARDING_PHONE);
        smsConfig.setInputFolder("src/test/resources/messages/inbox");
        smsConfig.getNotForwardList().add("RSCHS");

        Path inPath = new File(smsConfig.getInputFolder()).toPath();
        if (!Files.exists(inPath)) {
            Files.createDirectory(inPath);
        }
        smsConfig.setProcessedFolder("src/test/resources/messages/processed");
        smsConfig.setOutputFolder("src/test/resources/messages/outbox");
        Path outPath = new File(smsConfig.getOutputFolder()).toPath();
        if (!Files.exists(outPath)) {
            Files.createDirectory(outPath);
        }

        CommandExecutor commandExecutor = new CommandExecutor(
                Collections.singletonList(new ISMSCommand() {
                    @Override
                    public CommandConfig getCommandConfig() {
                        return null;
                    }

                    @Override
                    public CommandResult run(String param) {
                        return null;
                    }
                })
        );

        smsProcessor = new SMSProcessor(smsConfig, commandExecutor);
    }

    @Test
    public void emptyTest() {
        smsProcessor.inputSmsScan();
    }

    @Test
    public void forwardingEmptyFileTest() throws IOException {
        String inputFileName = "abc.txt";
        Path inputFilePath = Paths.get(smsConfig.getInputFolder(), inputFileName);
        Files.write(inputFilePath, "abc".getBytes());
        smsProcessor.inputSmsScan();
        try (Stream<Path> stream = Files.list(Paths.get(smsConfig.getInputFolder()))) {
            List<Path> files = stream.collect(Collectors.toList());
            assertEquals(0, files.size());
        }
        final String expectedOutfileName = "OUT+" + FORWARDING_PHONE + "..txt";
        try (Stream<Path> stream = Files.list(Paths.get(smsConfig.getOutputFolder()))) {
            Optional<Path> fileOptional = stream.filter(path -> !Files.isDirectory(path))
                    .filter(path -> path.endsWith(expectedOutfileName)).findFirst();
            assertTrue(fileOptional.isPresent());
        } finally {
            Files.delete(Paths.get(smsConfig.getOutputFolder(), expectedOutfileName));
            Files.delete(Paths.get(smsConfig.getProcessedFolder(), inputFileName));
        }
    }

    @Test
    public void forwarding1Test() throws IOException {
        String inputFileName = "IN20190101_135634_00_Megafon_00.txt";
        Path inputFilePath = Paths.get(smsConfig.getInputFolder(), inputFileName);
        Files.write(inputFilePath, "abc".getBytes());
        smsProcessor.inputSmsScan();
        try (Stream<Path> stream = Files.list(Paths.get(smsConfig.getInputFolder()))) {
            List<Path> files = stream.collect(Collectors.toList());
            assertEquals(0, files.size());
        }
        final String expectedOutfileName = "OUT+" + FORWARDING_PHONE + ".2019010113563400.txt";
        try (Stream<Path> stream = Files.list(Paths.get(smsConfig.getOutputFolder()))) {
            Optional<Path> fileOptional = stream.filter(path -> !Files.isDirectory(path))
                    .filter(path -> path.endsWith(expectedOutfileName)).findFirst();
            assertTrue(fileOptional.isPresent());
        } finally {
            Files.delete(Paths.get(smsConfig.getOutputFolder(), expectedOutfileName));
            Files.delete(Paths.get(smsConfig.getProcessedFolder(), inputFileName));
        }
    }

    @Test
    public void forwarding2Test() throws IOException {
        String inputFileName = "IN20190101_135634_00_Megafon_01.txt";
        Path inputFilePath = Paths.get(smsConfig.getInputFolder(), inputFileName);
        Files.write(inputFilePath, "abc".getBytes());
        smsProcessor.inputSmsScan();
        try (Stream<Path> stream = Files.list(Paths.get(smsConfig.getInputFolder()))) {
            List<Path> files = stream.collect(Collectors.toList());
            assertEquals(0, files.size());
        }
        final String expectedOutfileName = "OUT+" + FORWARDING_PHONE + ".2019010113563401.txt";
        try (Stream<Path> stream = Files.list(Paths.get(smsConfig.getOutputFolder()))) {
            Optional<Path> fileOptional = stream.filter(path -> !Files.isDirectory(path))
                    .filter(path -> path.endsWith(expectedOutfileName)).findFirst();
            assertTrue(fileOptional.isPresent());
            assertTrue(Files.readString(fileOptional.get()).contains("(Megafon)"));
        } finally {
            Files.delete(Paths.get(smsConfig.getOutputFolder(), expectedOutfileName));
            Files.delete(Paths.get(smsConfig.getProcessedFolder(), inputFileName));
        }
    }

    @Test
    public void forwardingBanTest() throws IOException {
        String inputFileName = "IN20190101_135634_00_RSCHS_00.txt";
        Path inputFilePath = Paths.get(smsConfig.getInputFolder(), inputFileName);
        Files.write(inputFilePath, "abc".getBytes());
        smsProcessor.inputSmsScan();
        try (Stream<Path> stream = Files.list(Paths.get(smsConfig.getInputFolder()))) {
            List<Path> files = stream.collect(Collectors.toList());
            assertEquals(0, files.size());
        }

        final String expectedOutfileName = "OUT+" + FORWARDING_PHONE + ".2019010113563401.txt";
        try (
                Stream<Path> stream = Files.list(Paths.get(smsConfig.getOutputFolder()));
                Stream<Path> stream2 = Files.list(Paths.get(smsConfig.getProcessedFolder()))
        ) {
            Optional<Path> fileOptional = stream.filter(path -> !Files.isDirectory(path))
                    .filter(path -> path.endsWith(expectedOutfileName)).findFirst();
            assertFalse(fileOptional.isPresent());

            Path processedFilePath = Paths.get(smsConfig.getProcessedFolder(), inputFileName);
            Optional<Path> fileOptional2 = stream2.filter(path -> !Files.isDirectory(processedFilePath))
                    .filter(path -> path.endsWith(inputFileName)).findFirst();
            assertTrue(fileOptional2.isPresent());
        } finally {
            Files.delete(Paths.get(smsConfig.getProcessedFolder(), inputFileName));
        }
    }
}
