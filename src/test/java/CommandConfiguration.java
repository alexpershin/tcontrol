import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tcontrol.config.props.ThermostatsConfig;
import org.tcontrol.hardware.ITemperatureMonitor;
import org.tcontrol.service.IThermostat;
import org.tcontrol.service.ITimer;
import org.tcontrol.service.impl.Thermostat;
import org.tcontrol.models.SensorValue;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.handler.IRelayHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.tcontrol.config.props.ThermostatsConfig.ThermostatConfig;

@Configuration
public class CommandConfiguration {

    private static final String GPIO_00 = "GPIO_00";
    private static final String SENSOR_CODE = "abc";

    @Bean
    public ThermostatConfig thermostatConfig() {
        ThermostatConfig thermostatConfig = new ThermostatConfig();
        thermostatConfig.setId(1);
        thermostatConfig.setDelta(0.5f);
        thermostatConfig.setNightBegin(60 * 60 * 23);
        thermostatConfig.setNightEnd(60 * 60 * 7);
        thermostatConfig.setSensor(SENSOR_CODE);
        thermostatConfig.setTDay(12.0f);
        thermostatConfig.setTNight(16.0f);
        thermostatConfig.setRelayPins(new ArrayList<>());
        thermostatConfig.getRelayPins().add(GPIO_00);
        thermostatConfig.setName("elect");
        thermostatConfig.setCode("elect");
        return thermostatConfig;
    }

    @Bean
    public ThermostatConfig thermostatConfig2() {
        ThermostatConfig thermostatConfig = new ThermostatConfig();
        thermostatConfig.setId(2);
        thermostatConfig.setDelta(0.5f);
        thermostatConfig.setNightBegin(60 * 60 * 23);
        thermostatConfig.setNightEnd(60 * 60 * 7);
        thermostatConfig.setSensor(SENSOR_CODE);
        thermostatConfig.setTDay(18.0f);
        thermostatConfig.setTNight(20.0f);
        thermostatConfig.setRelayPins(new ArrayList<>());
        thermostatConfig.getRelayPins().add(GPIO_00);
        thermostatConfig.setName("gast");
        thermostatConfig.setCode("gast");
        return thermostatConfig;
    }

    @Bean
    public ThermostatsConfig thermostatsConfig(ThermostatConfig thermostatConfig, ThermostatConfig thermostatConfig2){
        ThermostatsConfig thermostatsConfig = new ThermostatsConfig();
        thermostatsConfig.setThermostats(Arrays.asList(thermostatConfig, thermostatConfig2));
        return  thermostatsConfig;
    }

    @Bean
    public ITemperatureMonitor temperatureMonitor() {
        return new ITemperatureMonitor() {

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
    }

    @Bean
    public IRelayHandler relayHandler() {
        return new IRelayHandler() {
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
    }

    @Bean
    public ITimer timer() {
        return new ITimer() {
            @Override
            public LocalDateTime getCurrentTime() {
                return LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0));
            }

            @Override
            public long getCurrentTimeMillis() {
                return 1000;
            }
        };
    }

    @Bean
    public Map<String, IThermostat> dynamicThermostats(ThermostatsConfig thermostatsConfig,
                                                       ITemperatureMonitor temperatureMonitor,
                                                       IRelayHandler relayHandler,
                                                       ITimer timer) {
        Map<String, IThermostat> thermostatMap = new HashMap<>();

        Thermostat t1 = new Thermostat(1);
        t1.setThermostatsConfig(thermostatsConfig);
        t1.setTemperatureMonitor(temperatureMonitor);
        t1.setRelayAggregator(relayHandler);
        t1.setTimer(timer);
        t1.init();

        Thermostat t2 = new Thermostat(1);
        t2.setThermostatsConfig(thermostatsConfig);
        t2.setTemperatureMonitor(temperatureMonitor);
        t2.setRelayAggregator(relayHandler);
        t2.setTimer(timer);
        t2.init();

        thermostatMap.put("elect", t1);
        thermostatMap.put("gast", t2);

        return thermostatMap;
    }

    @Bean
    public SensorConfig sensorConfig() {
        SensorConfig sensorConfig = new SensorConfig();

        SensorConfig.SensorConfiguration sensorConfiguration = new SensorConfig.SensorConfiguration();
        sensorConfiguration.setName("дом");
        sensorConfiguration.setCode(SENSOR_CODE);

        sensorConfig.setSensors(Collections.singletonList(sensorConfiguration));
        return sensorConfig;
    }
}
