package org.tcontrol.sms.commands;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.tcontrol.sms.handler.IRelayHandler;
import org.tcontrol.sms.ISMSCommand;
import org.tcontrol.sms.IThermostat;

@Component
@Slf4j
@Qualifier("heatingOffCommand")
@RequiredArgsConstructor
public class HeatingOffCommand implements ISMSCommand {
    private final IRelayHandler relayHandler;
    private final IThermostat thermostatElectro;
    private final HeatingOnCommand heatingOnCommand;

    @Override
    public CommandResult run() {
        String resultString = " ";
        for(String heatingPin: thermostatElectro.getHeatingPins()){
            Pin pin = RaspiPin.getPinByName(heatingPin);
            PinState result = relayHandler.turnOffRelay(pin);
            resultString+=(result.isHigh()?"ON":"OFF");
        }

        Boolean prevThermostatState = heatingOnCommand.getPrevThermostatState();
        if(prevThermostatState !=null){
            thermostatElectro.changeOn(prevThermostatState);
        }

        log.info("Executed");
        return new CommandResult(STATUS.OK,"heating is" + resultString);
    }
}
