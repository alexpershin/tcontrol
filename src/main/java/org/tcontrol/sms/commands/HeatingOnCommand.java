package org.tcontrol.sms.commands;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.tcontrol.sms.handler.IRelayHandler;
import org.tcontrol.sms.ISMSCommand;
import org.tcontrol.sms.IThermostat;

@Component
@Slf4j
@Qualifier("heatingOnCommand")
public class HeatingOnCommand implements ISMSCommand {

    private final IRelayHandler relayHandler;

    private final IThermostat thermostat;

    @Getter
    private Boolean prevThermostatState;

    public HeatingOnCommand(IRelayHandler relayHandler,
                            IThermostat thermostatElectro) {
        this.relayHandler = relayHandler;
        this.thermostat = thermostatElectro;
    }

    @Override
    public CommandResult run() {
        prevThermostatState = thermostat.isOn();

        thermostat.changeOn(false);

        StringBuilder resultString = new StringBuilder(" ");
        for(String heatingPin: thermostat.getHeatingPins()){
            Pin pin = RaspiPin.getPinByName(heatingPin);
            PinState result = relayHandler.turnOnRelay(pin);
            resultString.append(result.isHigh() ? "ON" : "OFF");
        }

        log.info("Executed");
        return new CommandResult(STATUS.OK, "heating is" + resultString);
    }
}
