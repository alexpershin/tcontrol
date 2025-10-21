package org.tcontrol.sms.commands;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.hardware.ITemperatureMonitor;
import org.tcontrol.service.IThermostat;
import org.tcontrol.sms.*;
import org.tcontrol.hardware.IVoltageMonitor;
import org.tcontrol.hardware.IVoltageMonitor.VoltageResult;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.handler.IRelayHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.tcontrol.config.props.SensorConfig.SensorConfiguration;

@Slf4j
@RequiredArgsConstructor
public class StatusCommand implements ISMSCommand {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
            "yyyy.MM.dd HH:mm:ss");

    private final ITemperatureMonitor temperatureMonitor;

    private final SensorConfig sensorConfig;

    private final IRelayHandler relayHandler;

    private final IThermostat thermostatElectro;

    private final IThermostat thermostatGas;

    private final IThermostat thermostatVeranda;

    private final IThermostat thermostatMyRoom;

    private final IVoltageMonitor voltageMonitor;

    @Override
    public CommandResult run() {
        Optional<String> res = temperatureMonitor.getSensorValueMap().values().stream().map(value -> {
            Optional<SensorConfiguration> sc = sensorConfig.sensorConfigByCode(value.getSensorCode());
            return sc.map(sensorConfiguration -> sensorConfiguration.getName() + ": " + value.getValue())
                    .orElse("");
        }).reduce((a, b) -> a + "\n" + b);

        res = thermostatStatus(res, thermostatElectro, relayHandler);
        res = thermostatStatus(res, thermostatGas, relayHandler);
        res = thermostatStatus(res, thermostatVeranda, relayHandler);
        res = thermostatStatus(res, thermostatMyRoom, relayHandler);
        res = voltageStatus(res, voltageMonitor);

        res = Optional.of(res.orElse("") + "\n" + simpleDateFormat.format(new Date()));

        String text = res.orElse("status not ready");
        log.info("Executed");
        return new CommandResult(res.isPresent() ? STATUS.OK : STATUS.FAILURE, text);
    }

    private Optional<String> voltageStatus(Optional<String> res, IVoltageMonitor voltageMonitor) {
        for (VoltageResult vr : voltageMonitor.getVoltageResults()) {
            res = Optional.of(
                    res.orElse("")
                            + "\n" + vr.getName() + ": " + vr.getValue() + vr.getUnit());
        }
        return res;
    }

    private static Optional<String> thermostatStatus(Optional<String> res,
                                                     IThermostat thermostat, IRelayHandler relayHandler) {

        StringBuilder resultString = new StringBuilder(" ");
        for (String heatingPin : thermostat.getHeatingPins()) {
            Pin pin = RaspiPin.getPinByName(heatingPin);
            PinState heatingState = relayHandler.getPinState(pin);
            resultString.append(heatingState == null ? "UNDEFINED" : (heatingState.isHigh() ? "ON" : "OFF"));
        }

        res = Optional.of(
                res.orElse("")
                        + "\n" + thermostat.getName() + "(" + thermostat.getMediumT() + "): "
                        + (thermostat.isOn() ? "ON" : "OFF") + ", heating:" + resultString);
        return res;
    }
}
