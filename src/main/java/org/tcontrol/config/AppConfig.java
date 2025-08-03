package org.tcontrol.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.tcontrol.handler.IOnOffHandler;
import org.tcontrol.handler.impl.OnOffHandler;
import org.tcontrol.hardware.ITemperatureMonitor;
import org.tcontrol.service.IThermostat;
import org.tcontrol.service.ITimer;
import org.tcontrol.service.impl.Thermostat;
import org.tcontrol.config.props.*;
import org.tcontrol.handler.IRelayHandler;
import org.tcontrol.service.models.Sensor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
    public RelayPinsConfig relayPinsConfigStub() {
        RelayPinsConfig relayPinsConfig = new RelayPinsConfig();
        relayPinsConfig.setPins(Arrays.asList(
                new RelayPinsConfig.RelayPinConfig(8,"GPIO 8", "heating", "отопление")
        ));
        return relayPinsConfig;
    }

    @Bean
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
    public ThermostatConfig thermostatElectroConfigStub() {
        return ThermostatConfig.builder().sensor("8").relayPins(Collections.singletonList("GPIO 8")).build();
    }

    @Bean
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
    public IThermostat thermostatElectroStub(
            final ThermostatConfig thermostatElectroConfigStub,
            final ITemperatureMonitor temperatureMonitor,
            final IRelayHandler relayHandler,
            final ITimer timer) {
        return new Thermostat(thermostatElectroConfigStub, temperatureMonitor, relayHandler,
                timer, "termo(electro)");
    }

    @Bean
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
    public SensorConfig sensorConfigStub() {
        List<SensorConfiguration> sensors = Stream.of(
                new SensorConfiguration(1, "1", "Indoor",
                        Sensor.SensorType.TEMPERATURE,
                        "Дом",
                        "Inside home sensor", 10, 31, 2),
                new SensorConfiguration(2, "2", "Outdoor",
                        Sensor.SensorType.TEMPERATURE,
                        "Улица",
                        "Outdor temp sensor", -20, 32, 2),
                new SensorConfiguration(3, "3", "Cellar",
                        Sensor.SensorType.TEMPERATURE,
                        "Кладовая",
                        "Cellar temp sensor", 0, 20, 3),
                new SensorConfiguration(4, "4", "Loft",
                        Sensor.SensorType.TEMPERATURE,
                        "Чердак",
                        "Loft temp sensor", -20, 40, 2),
                new SensorConfiguration(5, "5", "Bath Room",
                        Sensor.SensorType.TEMPERATURE,
                        "Ванная",
                        "Bath Room temp sensor", 5, 34, 2),
                new SensorConfiguration(6, "6", "Garage",
                        Sensor.SensorType.TEMPERATURE,
                        "Гараж",
                        "Garage temp sensor", -40, 60, 6),
                new SensorConfiguration(7, "7", "Power",
                        Sensor.SensorType.VOLTAGE,
                        "Напряжение сети",
                        "Power voltage", 190, 250, 10),
                new SensorConfiguration(8, "8", "Heating",
                        Sensor.SensorType.ON_OFF,
                        "Отопление",
                        "Heating switcher"),
                new SensorConfiguration(9, "9", "Doors&Windows",
                        Sensor.SensorType.ALARM,
                        "Двери",
                        "Doors and windows close circuit")
        ).collect(Collectors.toList());
        SensorConfig sensorConfig = new SensorConfig();
        sensorConfig.setSensors(sensors);
        return sensorConfig;
    }

    @Bean
    @ConfigurationProperties(prefix = "sensor-config")
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "false")
    public SensorConfig sensorConfig() {
        return new SensorConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "sms-config")
    public SMSConfig smsConfig() {
        return new SMSConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "relay-pins")
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "false")
    public RelayPinsConfig relayPinsConfig() {
        return new RelayPinsConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "thermostat-electro")
    public ThermostatConfig thermostatElectroConfig() {
        return ThermostatConfig.builder().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "thermostat-gas")
    public ThermostatConfig thermostatGasConfig() {
        return ThermostatConfig.builder().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "thermostat-veranda")
    public ThermostatConfig thermostatVerandaConfig() {
        return ThermostatConfig.builder().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "thermostat-veranda-em")
    public ThermostatConfig thermostatVerandaEmConfig() {
        return ThermostatConfig.builder().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "thermostat-my-room")
    public ThermostatConfig thermostatMyRoomConfig() {
        return ThermostatConfig.builder().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "thermostat-hot-watter")
    public ThermostatConfig thermostatHotWatterConfig() {
        return ThermostatConfig.builder().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "voltage-monitor")
    public VoltageMonitorConfig voltageMonitorConfig() {
        return new VoltageMonitorConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "button-handler")
    public ButtonHandlerConfig buttonConfig() {
        return new ButtonHandlerConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "relay-aggregator")
    public RelayAggregatorConfig relayAggregatorConfig() {
        return new RelayAggregatorConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "main-config")
    public MainConfig mainConfig() {
        return new MainConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "voltage-handler")
    public VoltageHandlerConfig voltageControllerConfig() {
        return new VoltageHandlerConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "electric-heater-handler")
    public OnOfHandlerConfig onOfHandlerConfig() {
        return new OnOfHandlerConfig();
    }

    @Bean
    public IThermostat thermostatElectro(
            final ThermostatConfig thermostatElectroConfig,
            final ITemperatureMonitor temperatureMonitor,
            final IRelayHandler relayHandler,
            final ITimer timer) {
        return new Thermostat(thermostatElectroConfig, temperatureMonitor, relayHandler,
                timer, "termo1(electro)");
    }

    @Bean
    public IThermostat thermostatGas(
            final ThermostatConfig thermostatGasConfig,
            final ITemperatureMonitor temperatureMonitor,
            final IRelayHandler relayAggregator,
            final ITimer timer) {
        return new Thermostat(thermostatGasConfig, temperatureMonitor, relayAggregator,
                timer, "termo2(gas)");
    }

    @Bean
    public IThermostat thermostatVeranda(
            final ThermostatConfig thermostatVerandaConfig,
            final ITemperatureMonitor temperatureMonitor,
            final IRelayHandler relayHandler,
            final ITimer timer) {
        return new Thermostat(thermostatVerandaConfig, temperatureMonitor, relayHandler,
                timer, "termo3(veranda)");
    }

    @Bean
    public IThermostat thermostatVerandaEm(
            final ThermostatConfig thermostatVerandaEmConfig,
            final ITemperatureMonitor temperatureMonitor,
            final IRelayHandler relayAggregator,
            final ITimer timer) {
        return new Thermostat(thermostatVerandaEmConfig, temperatureMonitor, relayAggregator,
                timer, "termo5(veranda em)");
    }

    @Bean
    public IThermostat thermostatMyRoom(
            final ThermostatConfig thermostatMyRoomConfig,
            final ITemperatureMonitor temperatureMonitor,
            final IRelayHandler relayAggregator,
            final ITimer timer) {
        return new Thermostat(thermostatMyRoomConfig, temperatureMonitor, relayAggregator,
                timer, "termo4(my room)");
    }

    @Bean
    public IThermostat thermostatHotWatter(
            final ThermostatConfig thermostatHotWatterConfig,
            final ITemperatureMonitor temperatureMonitor,
            final IRelayHandler relayHandler,
            final ITimer timer) {
        return new Thermostat(thermostatHotWatterConfig, temperatureMonitor, relayHandler,
                timer, "termo5(hot watter)");
    }

    @Bean
    public IOnOffHandler electricHeatingOnOfHandler(
            final OnOfHandlerConfig onOfHandlerConfig,
            final RelayPinsConfig relayPinsConfig,
            final IRelayHandler relayHandler,
            final IThermostat thermostatElectro
    ){
        return new OnOffHandler(onOfHandlerConfig, relayPinsConfig, relayHandler, thermostatElectro);
    }

}
