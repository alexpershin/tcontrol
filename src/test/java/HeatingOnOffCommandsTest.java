import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.tcontrol.sms.*;
import org.tcontrol.sms.commands.CommandResult;
import org.tcontrol.sms.commands.HeatingOffCommand;
import org.tcontrol.sms.commands.HeatingOnCommand;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes=CommandConfiguration.class, loader= AnnotationConfigContextLoader.class)
@SpringBootTest
public class HeatingOnOffCommandsTest {
    @Autowired
    @Qualifier("thermostatElectro")
    private IThermostat thermostat;
    @Autowired
    private IRelayController relayController;

    @Test
    public void thermostatWasOnTest(){
        HeatingOnCommand heatingOnCommand =
                new HeatingOnCommand(relayController, thermostat);
        ISMSCommand heatingOffCommand =
                new HeatingOffCommand(relayController, thermostat, heatingOnCommand);

        thermostat.changeOn(true);
        assertTrue(thermostat.isOn());

        CommandResult result = heatingOnCommand.run();
        assertNotNull(result);

        assertFalse(thermostat.isOn());

        result = heatingOffCommand.run();
        assertNotNull(result);

        assertTrue(thermostat.isOn());

        thermostat.changeOn(false);
    }

    @Test
    public void thermostatWasOffTest(){

        HeatingOnCommand heatingOnCommand =
                new HeatingOnCommand(relayController, thermostat);

        ISMSCommand heatingOffCommand =
                new HeatingOffCommand(relayController, thermostat, heatingOnCommand);

        thermostat.changeOn(false);
        assertFalse(thermostat.isOn());

        CommandResult result = heatingOnCommand.run();
        assertNotNull(result);

        assertFalse(thermostat.isOn());

        result = heatingOffCommand.run();
        assertNotNull(result);

        assertFalse(thermostat.isOn());
    }
}
