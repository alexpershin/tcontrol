package org.tcontrol.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.tcontrol.config.props.ThermostatsConfig.ThermostatConfig;
import org.tcontrol.handler.IOnOffHandler;
import org.tcontrol.handler.impl.ButtonGasOnOffHandler;
import org.tcontrol.handler.impl.OnOffHandler;
import org.tcontrol.handler.impl.RelayHandler;
import org.tcontrol.hardware.IPinFactory;
import org.tcontrol.hardware.ITemperatureMonitor;
import org.tcontrol.hardware.ITemperatureReader;
import org.tcontrol.hardware.IVoltageMonitor;
import org.tcontrol.hardware.impl.TemperatureMonitor;
import org.tcontrol.hardware.impl.TemperatureMonitorStub;
import org.tcontrol.mappers.SensorMapper;
import org.tcontrol.service.*;
import org.tcontrol.service.impl.CSVStatisticSaver;
import org.tcontrol.service.impl.RelayAggregator;
import org.tcontrol.service.impl.SensorServiceImpl;
import org.tcontrol.config.props.*;
import org.tcontrol.handler.IRelayHandler;
import org.tcontrol.service.models.Sensor;
import org.tcontrol.sms.ISMSCommand;
import org.tcontrol.sms.commands.*;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.tcontrol.config.props.SensorConfig.SensorConfiguration;


@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class AppConfig implements BeanFactoryPostProcessor {

    @Bean
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
    public RelayPinsConfig relayPinsConfigStub() {
        RelayPinsConfig relayPinsConfig = new RelayPinsConfig();
        relayPinsConfig.setPins(Collections.singletonList(
                new RelayPinsConfig.RelayPinConfiguration(8, "GPIO 8", "heating", "отопление")
        ));
        return relayPinsConfig;
    }

    @Bean
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
    public ThermostatsConfig thermostatsStub() {

        ThermostatConfig thermostatElectroConfigStub =
                new ThermostatConfig();

        thermostatElectroConfigStub.setName("termo1(electro)");
        thermostatElectroConfigStub.setSensor("8");
        thermostatElectroConfigStub.setRelayPins(Collections.singletonList("GPIO 8"));

        ThermostatsConfig thermostatsConfig = new ThermostatsConfig();
        thermostatsConfig.setThermostats(Collections.singletonList(thermostatElectroConfigStub));
        return thermostatsConfig;
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
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
    public ITemperatureMonitor temperatureMonitorStub(
            SensorConfig sensorConfig,
            CSVStatisticSaver csvWriter,
            IStatisticsSaver sensorLastDataSaver) {
        return new TemperatureMonitorStub(sensorConfig, csvWriter, sensorLastDataSaver);
    }

    @Bean
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
    public DynamicThermostatBeanCreator dynamicThermostatBeanCreatorStub(
            ThermostatsConfig thermostatsConfig
    ) {
        return new DynamicThermostatBeanCreator(thermostatsConfig);
    }

    @Bean
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "false")
    public DynamicThermostatBeanCreator dynamicThermostatBeanCreator(
            Environment environment
    ) {
        return new DynamicThermostatBeanCreator(environment);
    }

    @Bean
    @ConfigurationProperties(prefix = "sensor-config")
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "false")
    public SensorConfig sensorConfig() {
        return new SensorConfig();
    }


    @Bean
    @ConfigurationProperties(prefix = "relay-pins")
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "false")
    public RelayPinsConfig relayPinsConfig() {
        return new RelayPinsConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "sms-config")
    public SMSConfig smsConfig() {
        return new SMSConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "thermostats-config")
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "false")
    public ThermostatsConfig thermostatsConfig() {
        return new ThermostatsConfig();
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
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "false")
    public ITemperatureMonitor temperatureMonitor(
            ITemperatureReader temperatureReader,
            SensorConfig sensorConfig,
            CSVStatisticSaver csvWriter,
            IStatisticsSaver sensorLastDataSaver) {
        return new TemperatureMonitor(temperatureReader, sensorConfig, csvWriter, sensorLastDataSaver);
    }


    @Bean
    public IRelayAggregator relayAggregator(RelayAggregatorConfig relayAggregatorConfig,
                                            IRelayHandler relayHandler) {
        return new RelayAggregator(relayAggregatorConfig, relayHandler);
    }

    @Bean
    public IRelayHandler relayHandler(CSVStatisticSaver csvStatisticSaver,
                                      IStatisticsSaver sensorLastDataSaver,
                                      IPinFactory pinFactory,
                                      RelayPinsConfig relayPinsConfig,
                                      SensorConfig sensorConfig) {
        return new RelayHandler(csvStatisticSaver, sensorLastDataSaver, pinFactory, relayPinsConfig, sensorConfig);
    }

    @Bean
    public SensorService sensorService(
            final ThermostatsConfig thermostatsConfig,
            final SensorConfig sensorConfig,
            final RelayPinsConfig relayPinsConfig,
            final IStatisticsSaver sensorLastDataSaver,
            final SensorMapper sensorMapper,
            final Map<String, IThermostat> dynamicThermostats
    ) {
        return new SensorServiceImpl(
                thermostatsConfig,
                sensorConfig,
                relayPinsConfig,
                sensorLastDataSaver,
                sensorMapper,
                dynamicThermostats);
    }


    @Bean
    public IOnOffHandler electricHeatingOnOfHandler(
            final OnOfHandlerConfig onOfHandlerConfig,
            final RelayPinsConfig relayPinsConfig,
            final IRelayHandler relayHandler,
            final Map<String, IThermostat> dynamicThermostats
    ) {
        IThermostat thermostat = dynamicThermostats.get(onOfHandlerConfig.getThermostat());
        return new OnOffHandler(onOfHandlerConfig, relayPinsConfig, relayHandler, thermostat);
    }

    @Bean
    public ButtonGasOnOffHandler buttonGasOnOffHandler(ButtonHandlerConfig buttonHandlerConfig,
                                                       final Map<String, IThermostat> dynamicThermostats) {
        IThermostat thermostat = dynamicThermostats.get(buttonHandlerConfig.getThermostat());
        return new ButtonGasOnOffHandler(buttonHandlerConfig, thermostat);
    }

    @Bean
    @Primary
    @Qualifier("heatingOnCommand")
    public ISMSCommand heatingOnCommand(
            final Map<String, IThermostat> dynamicThermostats) {
        IThermostat thermostat = dynamicThermostats.get("thermostat-electro");
        return new HeatingOnCommand(thermostat);
    }

    @Bean
    @Qualifier("thermostatOnCommand")
    public ISMSCommand thermostatOnCommand(
            final Map<String, IThermostat> dynamicThermostats) {
        IThermostat thermostat = dynamicThermostats.get("thermostat-electro");
        return new ThermostatOnCommand(dynamicThermostats);
    }

    @Bean
    @Qualifier("thermostatOffCommand")
    public ISMSCommand thermostatOffCommand(
            final Map<String, IThermostat> dynamicThermostats) {
        IThermostat thermostat = dynamicThermostats.get("thermostat-electro");
        return new ThermostatOffCommand(thermostat);
    }

    @Bean
    @Qualifier("gasOnCommand")
    public ISMSCommand gasOnCommand(
            final Map<String, IThermostat> dynamicThermostats) {
        IThermostat thermostat = dynamicThermostats.get("thermostat-gas");
        return new GasOnCommand(thermostat);
    }

    @Bean
    @Qualifier("gasOffCommand")
    public GasOffCommand gasOffCommand(
            final Map<String, IThermostat> dynamicThermostats) {
        IThermostat thermostat = dynamicThermostats.get("thermostat-gas");
        return new GasOffCommand(thermostat);
    }

    @Bean
    public ISMSCommand statusCommand(
            final ITemperatureMonitor temperatureMonitor,
            final SensorConfig sensorConfig,
            final IRelayHandler relayHandler,
            final Map<String, IThermostat> dynamicThermostats,
            final IVoltageMonitor voltageMonitor) {

        final IThermostat thermostatElectro = dynamicThermostats.get("thermostat-electro");

        final IThermostat thermostatGas = dynamicThermostats.get("thermostat-gas");

        final IThermostat thermostatVeranda = dynamicThermostats.get("thermostat-veranda");

        final IThermostat thermostatMyRoom = dynamicThermostats.get("thermostat-my-room");

        return new StatusCommand(temperatureMonitor, sensorConfig, relayHandler,
                thermostatElectro, thermostatGas, thermostatVeranda, thermostatMyRoom, voltageMonitor);
    }

    private static ThermostatConfig thermostatConfig(ThermostatsConfig thermostatsConfig, String name) {
        return thermostatsConfig.getThermostats().stream()
                .filter(tc -> tc.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new SensorServiceException(MessageFormat.format("Thermostat config not found by name: {0}", name)));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
