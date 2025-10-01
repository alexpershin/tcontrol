import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tcontrol.config.props.RelayAggregatorConfig;
import org.tcontrol.config.props.ThermostatsConfig;
import org.tcontrol.handler.IRelayHandler;
import org.tcontrol.hardware.ITemperatureMonitor;
import org.tcontrol.service.ITimer;
import org.tcontrol.service.impl.RelayAggregator;
import org.tcontrol.service.impl.Thermostat;
import org.tcontrol.service.models.SensorValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.tcontrol.config.props.ThermostatsConfig.ThermostatConfig;

public class ThermostatAggregatorTest {

    private static final String GPIO_00 = "GPIO 0";
    private static final String GPIO_01 = "GPIO 1";
    private static final String SENSOR_CODE = "abc";
    private Thermostat thermostat1;
    private Thermostat thermostat2;
    private Thermostat thermostat3;
    private ThermostatsConfig.ThermostatConfig thermostatConfig1;
    private ThermostatsConfig.ThermostatConfig thermostatConfig2;
    private ThermostatsConfig.ThermostatConfig thermostatConfig3;
    private ITemperatureMonitor temperatureMonitor;
    private IRelayHandler relayHandler;
    private ITimer timer;

    @BeforeEach
    public void before() {
        thermostatConfig1 = new ThermostatConfig();
        thermostatConfig1.setDelta(0.5f);
        thermostatConfig1.setNightBegin(60 * 60 * 23);
        thermostatConfig1.setNightEnd(60 * 60 * 7);
        thermostatConfig1.setSensor(SENSOR_CODE);
        thermostatConfig1.setTDay(12.0f);
        thermostatConfig1.setTNight(16.0f);
        thermostatConfig1.setRelayPins(new ArrayList<>());
        thermostatConfig1.getRelayPins().add(GPIO_00);
        thermostatConfig1.setName("termo1");

        thermostatConfig2 = new ThermostatConfig();
        thermostatConfig2.setDelta(0.5f);
        thermostatConfig2.setNightBegin(60 * 60 * 23);
        thermostatConfig2.setNightEnd(60 * 60 * 7);
        thermostatConfig2.setSensor(SENSOR_CODE);
        thermostatConfig2.setTDay(12.0f);
        thermostatConfig2.setTNight(16.0f);
        thermostatConfig2.setRelayPins(new ArrayList<>());
        thermostatConfig2.getRelayPins().add(GPIO_00);
        thermostatConfig2.setName("termo2");

        thermostatConfig3 = new ThermostatConfig();
        thermostatConfig3.setDelta(0.5f);
        thermostatConfig3.setNightBegin(60 * 60 * 23);
        thermostatConfig3.setNightEnd(60 * 60 * 7);
        thermostatConfig3.setSensor(SENSOR_CODE);
        thermostatConfig3.setTDay(12.0f);
        thermostatConfig3.setTNight(16.0f);
        thermostatConfig3.setRelayPins(new ArrayList<>());
        thermostatConfig3.getRelayPins().add(GPIO_01);
        thermostatConfig3.setName("termo3");

        ThermostatsConfig thermostatsConfig = new ThermostatsConfig();
        thermostatsConfig.setThermostats(Arrays.asList(thermostatConfig1, thermostatConfig2, thermostatConfig3));

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
            PinState currentPinState = PinState.LOW;

            @Override
            public PinState turnOnRelay(Pin controlPin) {
                currentPinState = PinState.HIGH;
                return currentPinState;
            }

            @Override
            public PinState turnOffRelay(Pin controlPin) {
                currentPinState = PinState.LOW;
                return currentPinState;
            }

            @Override
            public PinState getPinState(Pin controlPin) {
                return currentPinState;
            }
        };

        RelayAggregatorConfig relayAggregatorConfig = new RelayAggregatorConfig();
        Map<String, Integer> stringIntegerMap = new HashMap<>();
        stringIntegerMap.put("GPIO 0", 2);
        relayAggregatorConfig.setPinAggregates(stringIntegerMap);
        IRelayHandler relayAggregator = new RelayAggregator(relayAggregatorConfig, relayHandler);

        timer = mock(ITimer.class);
        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)));

        thermostat1 = new Thermostat("termo1");
        thermostat1.setThermostatsConfig(thermostatsConfig);
        thermostat1.setTemperatureMonitor(temperatureMonitor);
        thermostat1.setRelayAggregator(relayAggregator);
        thermostat1.setTimer(timer);
        thermostat1.init();

        thermostat2 = new Thermostat("termo2");
        thermostat2.setThermostatsConfig(thermostatsConfig);
        thermostat2.setTemperatureMonitor(temperatureMonitor);
        thermostat2.setRelayAggregator(relayAggregator);
        thermostat2.setTimer(timer);
        thermostat2.init();

        thermostat3 = new Thermostat("termo3");
        thermostat3.setThermostatsConfig(thermostatsConfig);
        thermostat3.setTemperatureMonitor(temperatureMonitor);
        thermostat3.setRelayAggregator(relayAggregator);
        thermostat3.setTimer(timer);
        thermostat3.init();
    }

    @Test
    public void testMidDaytSingleThermostat() {
        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)));

        thermostat1.checkTemperature();

        assertFalse(thermostat1.isHeatingOn());

        Pin heatingPin = RaspiPin.getPinByName(thermostatConfig1.getRelayPins().get(0));
        assertTrue(relayHandler.getPinState(heatingPin) == PinState.LOW);
    }

    @Test
    public void testMidNightSingleThermostat(){
        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(3, 0)));

        thermostat1.checkTemperature();

        assertTrue(thermostat1.isHeatingOn());

        Pin heatingPin = RaspiPin.getPinByName(thermostatConfig1.getRelayPins().get(0));
        assertTrue(relayHandler.getPinState(heatingPin) == PinState.LOW);
    }


    @Test
    public void testMidNighttTwoThermostatHigh() {
        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(3, 0)));

        thermostat1.checkTemperature();
        thermostat2.checkTemperature();

        assertTrue(thermostat1.isHeatingOn());
        assertTrue(thermostat2.isHeatingOn());

        Pin heatingPin = RaspiPin.getPinByName(thermostatConfig1.getRelayPins().get(0));
        assertTrue(relayHandler.getPinState(heatingPin) == PinState.HIGH);
    }

    @Test
    public void testMidNightSingleThermostatHigh() {
        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(3, 0)));

        thermostat3.checkTemperature();

        assertTrue(thermostat3.isHeatingOn());

        Pin heatingPin = RaspiPin.getPinByName(thermostatConfig3.getRelayPins().get(0));
        assertTrue(relayHandler.getPinState(heatingPin) == PinState.HIGH);
    }

}