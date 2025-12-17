package org.tcontrol.sms.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tcontrol.sms.ISMSCommand;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandExecutor {
    private final List<ISMSCommand> commands;

    public CommandResult run(String name, String param) {

        try {
            return commands.stream()
                    .filter(c -> c.getCommandConfig().getName().equalsIgnoreCase(name))
                    .map(c -> c.run(param))
                    .findAny()
                    .orElseGet(() -> new CommandResult(STATUS.FAILURE, "Unknown command: " + name));
        } catch (Exception e) {
            log.error("Error in command: {}", name, e);
            return new CommandResult(STATUS.FAILURE, "Error in command, see logs: " + name);

        }
    }
}
