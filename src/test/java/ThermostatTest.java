import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.BeforeEach;
import org.tcontrol.config.props.ThermostatsConfig;
import org.tcontrol.service.models.SensorValue;
import org.tcontrol.handler.IRelayHandler;
import org.tcontrol.hardware.ITemperatureMonitor;
import org.tcontrol.service.ITimer;
import org.tcontrol.service.impl.Thermostat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThermostatTest {

    private static final String GPIO_00 = "GPIO_00";
    private static final String SENSOR_CODE = "abc";
    private Thermostat thermostat;
    private ThermostatsConfig.ThermostatConfig thermostatConfig;
    private ITemperatureMonitor temperatureMonitor;
    private IRelayHandler relayHandler;
    private ITimer timer;

    @BeforeEach
    public void before() {
        thermostatConfig = new ThermostatsConfig.ThermostatConfig();
        thermostatConfig.setDelta(0.5f);
        thermostatConfig.setNightBegin(60 * 60 * 23);
        thermostatConfig.setNightEnd(60 * 60 * 7);
        thermostatConfig.setSensor(SENSOR_CODE);
        thermostatConfig.setTDay(12.0f);
        thermostatConfig.setTNight(16.0f);
        thermostatConfig.setRelayPins(new ArrayList<>());
        thermostatConfig.getRelayPins().add(GPIO_00);
        thermostatConfig.setName("termo");

        ThermostatsConfig thermostatsConfig = new ThermostatsConfig();
        thermostatsConfig.setThermostats(Collections.singletonList(thermostatConfig));

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

        timer = mock(ITimer.class);
        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)));

        thermostat = new Thermostat("termo");
        thermostat.setThermostatsConfig(thermostatsConfig);
        thermostat.setTemperatureMonitor(temperatureMonitor);
        thermostat.setRelayAggregator(relayHandler);
        thermostat.setTimer(timer);
        thermostat.init();
    }

    @Test
    public void testMidDay() {
        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)));

        thermostat.checkTemperature();

        assertFalse(thermostat.isHeatingOn());
    }

    @Test
    public void testMidNight() {
        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(3, 0)));

        thermostat.checkTemperature();

        assertTrue(thermostat.isHeatingOn());
    }

    @Test
    public void testSwitch() {
        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(3, 0)));

        Pin heatingPin = RaspiPin.getPinByName(thermostatConfig.getRelayPins().get(0));

        thermostat.checkTemperature();
        assertTrue(thermostat.isHeatingOn());
        assertTrue(relayHandler.getPinState(heatingPin).isHigh());

        SensorValue v = temperatureMonitor.getSensorValueMap().get(SENSOR_CODE);
        v.setSensorCode(SENSOR_CODE);
        v.setValue(15.0);

        thermostat.checkTemperature();
        assertTrue(thermostat.isHeatingOn());
        assertTrue(relayHandler.getPinState(heatingPin).isHigh());

        v.setValue(16.6);

        thermostat.checkTemperature();
        assertFalse(thermostat.isHeatingOn());
        assertFalse(relayHandler.getPinState(heatingPin).isHigh());

        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(4, 0)));
        v.setValue(16.2);
        thermostat.checkTemperature();
        assertFalse(thermostat.isHeatingOn());
        assertFalse(relayHandler.getPinState(heatingPin).isHigh());

        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 0)));
        v.setValue(15.6);
        thermostat.checkTemperature();
        assertFalse(thermostat.isHeatingOn());
        assertFalse(relayHandler.getPinState(heatingPin).isHigh());

        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 30)));
        v.setValue(15.5);
        thermostat.checkTemperature();
        assertTrue(thermostat.isHeatingOn());
        assertTrue(relayHandler.getPinState(heatingPin).isHigh());

        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 0)));
        v.setValue(15.5);
        thermostat.checkTemperature();
        assertFalse(thermostat.isHeatingOn());

        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 0)));
        v.setValue(11.5);
        thermostat.checkTemperature();
        assertTrue(thermostat.isHeatingOn());
        assertTrue(relayHandler.getPinState(heatingPin).isHigh());

        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)));
        v.setValue(12.8);
        thermostat.checkTemperature();
        assertFalse(thermostat.isHeatingOn());
        assertFalse(relayHandler.getPinState(heatingPin).isHigh());

        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 0)));
        v.setValue(12.1);
        thermostat.checkTemperature();
        assertFalse(thermostat.isHeatingOn());
        assertFalse(relayHandler.getPinState(heatingPin).isHigh());

        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 1)));
        v.setValue(12.12);
        thermostat.checkTemperature();
        assertTrue(thermostat.isHeatingOn());
        assertTrue(relayHandler.getPinState(heatingPin).isHigh());

        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 10)));
        v.setValue(12.1);
        thermostat.checkTemperature();
        assertEquals(16.0, thermostat.getMediumT(), 0.01);
        assertTrue(thermostat.isHeatingOn());
        assertTrue(relayHandler.getPinState(heatingPin).isHigh());

        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(6, 50)));
        v.setValue(15.2);
        thermostat.checkTemperature();
        assertEquals(16.0, thermostat.getMediumT(), 0.01);
        assertTrue(thermostat.isHeatingOn());
        assertTrue(relayHandler.getPinState(heatingPin).isHigh());

        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 0)));
        v.setValue(15.3);
        thermostat.checkTemperature();
        assertEquals(12.0, thermostat.getMediumT(), 0.01);
        assertFalse(thermostat.isHeatingOn());
        assertFalse(relayHandler.getPinState(heatingPin).isHigh());

        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 10)));
        v.setValue(15.3);
        thermostat.checkTemperature();
        assertEquals(12.0, thermostat.getMediumT(), 0.01);
        assertFalse(thermostat.isHeatingOn());
        assertFalse(relayHandler.getPinState(heatingPin).isHigh());
    }

    @Test
    public void testOnOff(){
        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(3, 0)));

        Pin heatingPin = RaspiPin.getPinByName(thermostatConfig.getRelayPins().get(0));

        thermostat.checkTemperature();
        assertEquals(16.0, thermostat.getMediumT());
        assertTrue(thermostat.isHeatingOn());
        assertTrue(relayHandler.getPinState(heatingPin).isHigh());

        thermostat.on(40.0f);
        thermostat.checkTemperature();
        assertTrue(relayHandler.getPinState(heatingPin).isHigh());
        assertEquals(40.0, thermostat.getMediumT());

        thermostat.on(10.0f);
        thermostat.checkTemperature();
        assertFalse(relayHandler.getPinState(heatingPin).isHigh());

        thermostat.off();
        thermostat.checkTemperature();
        assertTrue(relayHandler.getPinState(heatingPin).isHigh());
        assertEquals(16.0, thermostat.getMediumT());

        thermostat.on(40.0f);
        thermostat.checkTemperature();
        assertTrue(relayHandler.getPinState(heatingPin).isHigh());
        assertEquals(40.0, thermostat.getMediumT());

        thermostat.off();
        thermostat.checkTemperature();
        assertTrue(relayHandler.getPinState(heatingPin).isHigh());
        assertEquals(16.0, thermostat.getMediumT());

    }
}
