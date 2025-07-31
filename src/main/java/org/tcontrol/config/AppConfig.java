package org.tcontrol.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.tcontrol.hardware.ITemperatureMonitor;
import org.tcontrol.service.IThermostat;
import org.tcontrol.service.ITimer;
import org.tcontrol.service.impl.Thermostat;
import org.tcontrol.config.props.*;
import org.tcontrol.handler.IRelayHandler;

@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    @ConfigurationProperties(prefix = "sensor-config")
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
    public RelayPinsConfig relayPinsConfig() {
        return new RelayPinsConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "thermostat-electro")
    public ThermostatConfig thermostatElectroConfig() {
        return new ThermostatConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "thermostat-gas")
    public ThermostatConfig thermostatGasConfig() {
        return new ThermostatConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "thermostat-veranda")
    public ThermostatConfig thermostatVerandaConfig() {
        return new ThermostatConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "thermostat-veranda-em")
    public ThermostatConfig thermostatVerandaEmConfig() {
        return new ThermostatConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "thermostat-my-room")
    public ThermostatConfig thermostatMyRoomConfig() {
        return new ThermostatConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "thermostat-hot-watter")
    public ThermostatConfig thermostatHotWatterConfig() {
        return new ThermostatConfig();
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

}
