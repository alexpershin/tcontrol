package org.sms;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.scheduling.TaskScheduler;
import org.tcontrol.config.props.CommandConfig;
import org.tcontrol.config.props.RelayPinsConfig;
import org.tcontrol.config.props.ThermostatsConfig;
import org.tcontrol.hardware.ITemperatureMonitor;
import org.tcontrol.service.IThermostat;
import org.tcontrol.service.ITimer;
import org.tcontrol.service.impl.Thermostat;
import org.tcontrol.models.SensorValue;
import org.tcontrol.hardware.IVoltageMonitor;
import org.tcontrol.hardware.IVoltageMonitor.VoltageResult;
import org.tcontrol.sms.commands.CommandResult;
import org.tcontrol.sms.commands.StatusCommand;
import org.tcontrol.config.props.SensorConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.tcontrol.handler.IRelayHandler;

import static org.tcontrol.config.props.ThermostatsConfig.ThermostatConfig;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatusCommandTest {

    private static final String GPIO_00 = "GPIO_00";
    private static final String GPIO_01 = "GPIO_01";
    private static final String GPIO_02 = "GPIO_02";
    private static final String GPIO_03 = "GPIO_03";
    private static final String SENSOR_CODE = "abc";
    private Thermostat thermostat1;
    private Thermostat thermostat2;
    private Thermostat thermostat3;
    private Thermostat thermostat4;
    private ThermostatsConfig.ThermostatConfig thermostatConfig;
    private ThermostatConfig thermostatConfig2;
    private ThermostatConfig thermostatConfig3;
    private ThermostatConfig thermostatConfig4;
    private ThermostatsConfig thermostatsConfig;
    private ITemperatureMonitor temperatureMonitor;
    private IRelayHandler relayHandler;
    private ITimer timer;
    private StatusCommand statusCommand;
    private SensorConfig sensorConfig;

    @BeforeEach
    public void before() {
        thermostatConfig = new ThermostatConfig();
        thermostatConfig.setId(1);
        thermostatConfig.setDelta(0.5f);
        thermostatConfig.setNightBegin(60 * 60 * 23);
        thermostatConfig.setNightEnd(60 * 60 * 7);
        thermostatConfig.setSensor(SENSOR_CODE);
        thermostatConfig.setTDay(12.0f);
        thermostatConfig.setTNight(16.0f);
        thermostatConfig.setRelayPins(new ArrayList<>());
        thermostatConfig.getRelayPins().add(GPIO_00);
        thermostatConfig.setName("termo1");
        thermostatConfig.setCode("t1");
        thermostatConfig.setSchedule("10 0/10  * * * ?");
        thermostatConfig.init();

        thermostatConfig2 = new ThermostatConfig();
        thermostatConfig2.setId(2);
        thermostatConfig2.setDelta(0.5f);
        thermostatConfig2.setNightBegin(60 * 60 * 23);
        thermostatConfig2.setNightEnd(60 * 60 * 7);
        thermostatConfig2.setSensor(SENSOR_CODE);
        thermostatConfig2.setTDay(18.0f);
        thermostatConfig2.setTNight(20.0f);
        thermostatConfig2.setRelayPins(new ArrayList<>());
        thermostatConfig2.getRelayPins().add(GPIO_01);
        thermostatConfig2.setName("termo2");
        thermostatConfig2.setCode("t2");
        thermostatConfig2.setSchedule("10 0/10  * * * ?");
        thermostatConfig2.init();

        thermostatConfig3 = new ThermostatConfig();
        thermostatConfig3.setId(3);
        thermostatConfig3.setDelta(0.5f);
        thermostatConfig3.setNightBegin(60 * 60 * 23);
        thermostatConfig3.setNightEnd(60 * 60 * 7);
        thermostatConfig3.setSensor(SENSOR_CODE);
        thermostatConfig3.setTDay(17.0f);
        thermostatConfig3.setTNight(18.0f);
        thermostatConfig3.setRelayPins(new ArrayList<>());
        thermostatConfig3.getRelayPins().add(GPIO_02);
        thermostatConfig3.setName("termo3");
        thermostatConfig3.setCode("t3");
        thermostatConfig3.setSchedule("10 0/10  * * * ?");
        thermostatConfig3.init();

        thermostatConfig4 = new ThermostatConfig();
        thermostatConfig4.setId(4);
        thermostatConfig4.setDelta(0.5f);
        thermostatConfig4.setNightBegin(60 * 60 * 23);
        thermostatConfig4.setNightEnd(60 * 60 * 7);
        thermostatConfig4.setSensor(SENSOR_CODE);
        thermostatConfig4.setTDay(17.0f);
        thermostatConfig4.setTNight(18.0f);
        thermostatConfig4.setRelayPins(new ArrayList<>());
        thermostatConfig4.getRelayPins().add(GPIO_03);
        thermostatConfig4.setName("termo4");
        thermostatConfig4.setCode("t4");
        thermostatConfig4.setSchedule("10 0/10  * * * ?");
        thermostatConfig4.init();

        thermostatsConfig = new ThermostatsConfig();
        thermostatsConfig.setThermostats(Arrays.asList(thermostatConfig, thermostatConfig2,
                thermostatConfig3, thermostatConfig4));

        temperatureMonitor = new ITemperatureMonitor() {

            final Map<String, SensorValue> map = new HashMap<>();

            {
                SensorValue v = new SensorValue();
                v.setSensorCode(SENSOR_CODE);
                v.setValue(13.0);
                map.put(SENSOR_CODE, v);

            }

            @Override
            public Map<String, SensorValue> getSensorValueMap() {
                return map;
            }
        };

        relayHandler = new IRelayHandler() {
            final Map<Pin, PinState> currentPinStates = new HashMap<>();

            {
                currentPinStates.put(thermostatConfig.relayPins().getFirst(), PinState.LOW);
                currentPinStates.put(thermostatConfig2.relayPins().getFirst(), PinState.LOW);
                currentPinStates.put(thermostatConfig3.relayPins().getFirst(), PinState.LOW);
                currentPinStates.put(thermostatConfig4.relayPins().getFirst(), PinState.LOW);
            }

            @Override
            public PinState turnOnRelay(Pin controlPin) {
                PinState currentPinState = PinState.HIGH;
                currentPinStates.put(thermostatConfig.relayPins().getFirst(), PinState.HIGH);
                return currentPinState;
            }

            @Override
            public PinState turnOffRelay(Pin controlPin) {
                PinState currentPinState = PinState.LOW;
                currentPinStates.put(controlPin, currentPinState);
                return currentPinState;
            }

            @Override
            public PinState getPinState(Pin controlPin) {
                return currentPinStates.get(controlPin);
            }
        };

        timer = mock(ITimer.class);
        when(timer.getCurrentTime()).thenReturn(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0))
        );

        TaskScheduler taskScheduler = mock(TaskScheduler.class);

        thermostat1 = new Thermostat(1);
        thermostat1.setThermostatsConfig(thermostatsConfig);
        thermostat1.setTemperatureMonitor(temperatureMonitor);
        thermostat1.setRelayAggregator(relayHandler);
        thermostat1.setTimer(timer);
        thermostat1.setTaskScheduler(taskScheduler);
        thermostat1.init();

        thermostat2 = new Thermostat(2);
        thermostat2.setThermostatsConfig(thermostatsConfig);
        thermostat2.setTemperatureMonitor(temperatureMonitor);
        thermostat2.setRelayAggregator(relayHandler);
        thermostat2.setTimer(timer);
        thermostat2.setTaskScheduler(taskScheduler);
        thermostat2.init();

        thermostat3 = new Thermostat(3);
        thermostat3.setThermostatsConfig(thermostatsConfig);
        thermostat3.setTemperatureMonitor(temperatureMonitor);
        thermostat3.setRelayAggregator(relayHandler);
        thermostat3.setTimer(timer);
        thermostat3.setTaskScheduler(taskScheduler);
        thermostat3.init();

        thermostat4 = new Thermostat(4);
        thermostat4.setThermostatsConfig(thermostatsConfig);
        thermostat4.setTemperatureMonitor(temperatureMonitor);
        thermostat4.setRelayAggregator(relayHandler);
        thermostat4.setTimer(timer);
        thermostat4.setTaskScheduler(taskScheduler);
        thermostat4.init();

        sensorConfig = new SensorConfig();

        SensorConfig.SensorConfiguration sensorConfiguration = new SensorConfig.SensorConfiguration();
        sensorConfiguration.setName("дом");
        sensorConfiguration.setCode(SENSOR_CODE);

        sensorConfig.setSensors(Collections.singletonList(sensorConfiguration));

        final IVoltageMonitor voltageMonitor = () -> Collections.singletonList(new VoltageResult("socket", 220.1, "V"));

        CommandConfig statusCommandConfig = new CommandConfig();
        statusCommandConfig.setThermostats(Arrays.asList("t1", "t2", "t3", "t4"));

        Map<String, IThermostat> dynamicThermostats = new HashMap<>();
        dynamicThermostats.put("t1", thermostat1);
        dynamicThermostats.put("t2", thermostat2);
        dynamicThermostats.put("t3", thermostat3);
        dynamicThermostats.put("t4", thermostat4);

        RelayPinsConfig relayPinsConfig = new RelayPinsConfig();
        relayPinsConfig.setPins(Arrays.asList(
                new RelayPinsConfig.RelayPinConfiguration(0, GPIO_00, GPIO_00, "pin0")
                , new RelayPinsConfig.RelayPinConfiguration(1, GPIO_01, GPIO_01, "pin1")
                , new RelayPinsConfig.RelayPinConfiguration(2, GPIO_02, GPIO_02, "pin2")
                , new RelayPinsConfig.RelayPinConfiguration(3, GPIO_03, GPIO_03, "pin3")
        ));


        statusCommand = new StatusCommand(
                relayHandler,
                dynamicThermostats,
                voltageMonitor,
                statusCommandConfig,
                relayPinsConfig
        );

    }

    @Test
    public void testStatus() {
        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)));

        thermostat1.checkTemperature();
        thermostat2.checkTemperature();
        thermostat3.checkTemperature();

        CommandResult result = statusCommand.run("");
        assertTrue (result.getMessage().contains("termo4: ?(17.0): Default, heating: pin3(ON)"));
        assertTrue (result.getMessage().contains("termo1: 13.0(12.0): Default, heating: pin0(ON)"));
        assertTrue(result.getMessage().contains("termo2: 13.0(18.0): Default, heating: pin1(ON)"));
        assertTrue(result.getMessage().contains("socket: 220.1V"));
    }
}
