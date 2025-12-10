package org.tcontrol.sms;

import org.tcontrol.config.props.CommandConfig;
import org.tcontrol.sms.commands.CommandResult;

public interface ISMSCommand {
    CommandConfig getCommandConfig();
    CommandResult run (String param);
}
