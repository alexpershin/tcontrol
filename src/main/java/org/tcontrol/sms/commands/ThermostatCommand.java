package org.tcontrol.sms.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.config.props.CommandConfig;
import org.tcontrol.service.impl.Thermostat;
import org.tcontrol.sms.ISMSCommand;
import org.tcontrol.service.IThermostat;

import java.text.MessageFormat;
import java.util.Map;
import java.util.function.BiFunction;

import static java.lang.Float.parseFloat;

@Slf4j
@AllArgsConstructor
public class ThermostatCommand implements ISMSCommand {
    public static final float MIN_TEMPERATURE = 4.0f;
    public static final float MAX_TEMPERATURE = 30.0f;

    private final Map<String, IThermostat> dynamicThermostats;

    @Getter
    private final CommandConfig commandConfig;

    private final BiFunction<Float, IThermostat, IThermostat> action;

    @Override
    public CommandResult run(String param) {
        float newTemperature;
        try {
            newTemperature = parseFloat(param);
            if (newTemperature < MIN_TEMPERATURE || newTemperature > MAX_TEMPERATURE) {
                return new CommandResult(STATUS.FAILURE,
                        MessageFormat.format("invalid argument: {0} has to be [{1},{2}]",
                                param, MIN_TEMPERATURE, MAX_TEMPERATURE));
            }
        } catch (NumberFormatException e) {
            return new CommandResult(STATUS.FAILURE, MessageFormat.format("invalid argument: {0}", param));
        }

        log.info("Start command");
        return dynamicThermostats.values().stream()
                .filter(t -> commandConfig.getThermostats().contains(t.getCode()))
                .map(t -> action.apply(newTemperature, t))
                .map(t -> MessageFormat.format("thermostat: {0} status: {1}",
                        t.getName(), t.checkTemperature() ? "on" : "off"))
                .reduce((a, b) -> a + "\n" + b)
                .map(r -> new CommandResult(STATUS.OK, r))
                .map(r -> {
                    log.info("Executed");
                    return r;
                })
                .orElse(new CommandResult(STATUS.FAILURE, "internal error"));
    }
}
