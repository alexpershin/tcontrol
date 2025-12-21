package org.sms;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.tcontrol.config.AppConfig;
import org.tcontrol.config.props.CommandConfig;
import org.tcontrol.service.IThermostat;
import org.tcontrol.sms.*;
import org.tcontrol.sms.commands.CommandResult;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.tcontrol.sms.commands.STATUS.FAILURE;

@ContextConfiguration(classes = CommandConfiguration.class, loader = AnnotationConfigContextLoader.class)
@SpringBootTest
public class ThermostatCommandTest {
    @Autowired
    private Map<String,IThermostat> dynamicThermostats;

    private IThermostat thermostat;

    @BeforeEach
    public void befoEach(){
        thermostat = dynamicThermostats.get("elect");
        thermostat.rollback();
    }

    @Test
    public void thermostatOnTest() {
        AppConfig appConfig= new AppConfig();
        ISMSCommand heatingOnCommand = appConfig.heatingOnCommand(
                dynamicThermostats,
                new CommandConfig("turn on", Collections.singletonList("elect"))
        );
        ISMSCommand heatingOffCommand = appConfig.heatingOffCommand(
                dynamicThermostats,
                new CommandConfig("turn off", Collections.singletonList("elect"))
        );

        assertFalse(thermostat.isChanged());
        assertFalse(thermostat.isHeatingOn());

        CommandResult result = heatingOnCommand.run("22");
        assertNotNull(result);

        assertTrue(thermostat.isChanged());
        assertTrue(thermostat.isHeatingOn());

        heatingOffCommand.run("5");
        assertFalse(thermostat.isChanged());
    }

    @Test
    public void thermostatOffTest() {

        AppConfig appConfig= new AppConfig();
        ISMSCommand heatingOffCommand = appConfig.heatingOffCommand(
                dynamicThermostats,
                new CommandConfig("turn off", Collections.singletonList("elect"))
        );

        assertFalse(thermostat.isChanged());

        CommandResult result = heatingOffCommand.run("10");
        assertNotNull(result);

        assertFalse(thermostat.isChanged());
    }

    @Test
    public void thermostatInvalidArgumentTest() {

        AppConfig appConfig= new AppConfig();

        ISMSCommand heatingOnCommand = appConfig.heatingOnCommand(
                dynamicThermostats,
                new CommandConfig("turn oo", Collections.singletonList("elect"))
        );

        assertFalse(thermostat.isChanged());

        CommandResult result = heatingOnCommand.run("35");
        assertNotNull(result);
        assertEquals(FAILURE, result.getStatus());
        assertTrue(result.getMessage().contains("invalid argument: 35"));

        result = heatingOnCommand.run("0");
        assertNotNull(result);
        assertEquals(FAILURE, result.getStatus());
        assertTrue(result.getMessage().contains("invalid argument: 0"));

        result = heatingOnCommand.run("abc");
        assertNotNull(result);
        assertEquals(FAILURE, result.getStatus());
        assertTrue(result.getMessage().contains("invalid argument: abc"));

        result = heatingOnCommand.run(null);
        assertNotNull(result);
        assertEquals(FAILURE, result.getStatus());
        assertTrue(result.getMessage().contains("parameter can't be null"));

        assertFalse(thermostat.isChanged());
    }
}
