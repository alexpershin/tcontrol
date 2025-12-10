package org.tcontrol.sms.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tcontrol.sms.ISMSCommand;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandExecutor {
    private final List<ISMSCommand> commands;

    public CommandResult run(String name, String param) {

        return commands.stream()
                .filter(c->c.getCommandConfig().getName().equals(name))
                .map(c->c.run(param))
                .findFirst()
                .orElseGet(()->new CommandResult(STATUS.FAILURE, "unknown command: " + name));
    }
}
