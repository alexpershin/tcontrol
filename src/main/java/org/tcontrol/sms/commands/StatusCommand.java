package org.tcontrol.sms.commands;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.config.props.CommandConfig;
import org.tcontrol.service.IThermostat;
import org.tcontrol.sms.*;
import org.tcontrol.hardware.IVoltageMonitor;
import org.tcontrol.hardware.IVoltageMonitor.VoltageResult;
import org.tcontrol.handler.IRelayHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class StatusCommand implements ISMSCommand {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
            "yyyy.MM.dd HH:mm:ss");
    public static final String DEFAULT = "Default";
    public static final String MODIFIED = "Modified";

    private final IRelayHandler relayHandler;

    private final Map<String, IThermostat> dynamicThermostats;

    private final IVoltageMonitor voltageMonitor;

    @Getter
    private final CommandConfig commandConfig;

    @Override
    public CommandResult run(String par) {

        String res = dynamicThermostats.values().stream()
                .filter(t -> commandConfig.getThermostats().contains(t.getCode()))
                .map(t -> thermostatStatus(t, relayHandler))
                .reduce((a, b) -> a + "\n" + b).orElse("");


        res += (res.isEmpty() ? "": "\n" ) + voltageStatus(voltageMonitor);

        res +=  "\n" + simpleDateFormat.format(new Date());

        log.info("Executed");
        return new CommandResult( STATUS.OK , res);
    }

    private String voltageStatus(IVoltageMonitor voltageMonitor) {
        String res="";
        for (VoltageResult vr : voltageMonitor.getVoltageResults()) {
            res = vr.getName() + ": " + vr.getValue() + vr.getUnit();
        }
        return res;
    }

    private static String thermostatStatus(IThermostat thermostat, IRelayHandler relayHandler) {

        StringBuilder resultString = new StringBuilder(" ");
        for (String heatingPin : thermostat.getHeatingPins()) {
            Pin pin = RaspiPin.getPinByName(heatingPin);
            PinState heatingState = relayHandler.getPinState(pin);
            resultString.append(heatingState == null ? "UNDEFINED" : (heatingState.isHigh() ? "ON" : "OFF"));
        }

        return thermostat.getName() + "(" + thermostat.getMediumT() + "): "
                        + (thermostat.isChanged() ? MODIFIED : DEFAULT) + ", heating:" + resultString;
    }
}
