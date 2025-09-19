import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.tcontrol.service.IThermostat;
import org.tcontrol.sms.*;
import org.tcontrol.sms.commands.CommandResult;
import org.tcontrol.sms.commands.HeatingOffCommand;
import org.tcontrol.sms.commands.HeatingOnCommand;

import org.junit.jupiter.api.Test;
import org.tcontrol.handler.IRelayHandler;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = CommandConfiguration.class, loader = AnnotationConfigContextLoader.class)
@SpringBootTest
public class HeatingOnOffCommandsTest {
    @Autowired
    private Map<String,IThermostat> dynamicThermostats;

    @Autowired
    private IRelayHandler relayHandler;

    private IThermostat thermostat;

    @BeforeEach
    public void befoEach(){
        thermostat = dynamicThermostats.get("elect");
    }

    @Test
    public void thermostatWasOnTest() {
        HeatingOnCommand heatingOnCommand = new HeatingOnCommand(thermostat);
        ISMSCommand heatingOffCommand = new HeatingOffCommand(thermostat);

        assertTrue(thermostat.isOn());
        assertFalse(thermostat.isHeatingOn());

        CommandResult result = heatingOnCommand.run();
        assertNotNull(result);
        assertTrue(thermostat.isOn());
        assertFalse(thermostat.isHeatingOn());

        thermostat.checkTemperature();

        assertTrue(thermostat.isOn());
        assertTrue(thermostat.isHeatingOn());

        heatingOffCommand.run();
        assertFalse(thermostat.isOn());
    }

    @Test
    public void thermostatWasOffTest() {

        HeatingOnCommand heatingOnCommand = new HeatingOnCommand(thermostat);

        thermostat.off();
        thermostat.checkTemperature();

        assertFalse(thermostat.isOn());

        CommandResult result = heatingOnCommand.run();
        assertNotNull(result);

        assertTrue(thermostat.isOn());
    }
}
