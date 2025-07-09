import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.tcontrol.sms.*;
import org.tcontrol.sms.commands.CommandResult;
import org.tcontrol.sms.commands.HeatingOffCommand;
import org.tcontrol.sms.commands.HeatingOnCommand;
import org.tcontrol.sms.config.props.SMSConfig;
import org.tcontrol.sms.config.props.SensorConfig;
import org.tcontrol.sms.config.props.ThermostatConfig;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes=CommandConfiguration.class, loader= AnnotationConfigContextLoader.class)
@SpringBootTest
public class HeatingOnOffCommandsTest {
    @Autowired
    @Qualifier("thermostatElectro")
    private IThermostat thermostat;
    @Autowired
    private ThermostatConfig thermostatConfig;
    @Autowired
    private ITemperatureMonitor temperatureMonitor;
    @Autowired
    private IRelayController relayController;
    @Autowired
    private ITimer timer;
    @Autowired
    private SensorConfig sensorConfig;

    @Test
    public void thermostatWasOnTest(){
        SMSConfig smsConfig = new SMSConfig();
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
        SMSConfig smsConfig = new SMSConfig();

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
