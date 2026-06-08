package org.tcontrol.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
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
import org.tcontrol.controller.mappers.SensorMapper;
import org.tcontrol.controller.mappers.SensorValueMapper;
import org.tcontrol.models.SensorValueShort;
import org.tcontrol.service.*;
import org.tcontrol.service.impl.*;
import org.tcontrol.config.props.*;
import org.tcontrol.handler.IRelayHandler;
import org.tcontrol.models.Sensor;
import org.tcontrol.sms.ISMSCommand;
import org.tcontrol.sms.commands.*;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.tcontrol.config.props.SensorConfig.SensorConfiguration;
import static org.tcontrol.service.impl.SensorServiceImpl.SYSTEM_ALERTS;


@Slf4j
@Configuration
@EnableScheduling
@EnableAspectJAutoProxy
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

        thermostatElectroConfigStub.setId(1);
        thermostatElectroConfigStub.setCode("termo1-electro");
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
                        "Doors and windows are closed"),
                new SensorConfiguration(10, "10", SYSTEM_ALERTS,
                        Sensor.SensorType.ALARM,
                        "Состояние системы",
                        "Показывает, если ошибки")
        ).collect(Collectors.toList());
        SensorConfig sensorConfig = new SensorConfig();
        sensorConfig.setSensors(sensors);
        return sensorConfig;
    }

    @Bean
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
    public SystemAlertsAggregator systemAlertsAggregatorStub(
            SensorConfig sensorConfig,
            IStatisticsManager statisticsManager
    ) {
        SystemAlertsAggregatorImpl systemAlertsAggregator = new SystemAlertsAggregatorImpl(sensorConfig);
        for (int i = 0; i < 20; i++) {
            systemAlertsAggregator.addAlert(System.currentTimeMillis() - 1000 * 3600 + i,
                    "Error while getting temperature via W1 bus for sensor 28-0317017a6aff");
            systemAlertsAggregator.addAlert(System.currentTimeMillis() - 1000 * 3600 + i + 100, "Error while getting temperature via W1 bus for sensor 28-000002c7c3ee");
        }
        return systemAlertsAggregator;
    }

    @Bean
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
    public ITemperatureMonitor temperatureMonitorStub(
            SensorConfig sensorConfig,
            ICSVDataSaver csvWriter,
            IStatisticsManager statisticsManager) {
        return new TemperatureMonitorStub(sensorConfig, statisticsManager);
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
    @ConfigurationProperties(prefix = "gas-on-command")
    public CommandConfig gasOnCommandConfig() {
        return new CommandConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "gas-off-command")
    public CommandConfig gasOffCommandConfig() {
        return new CommandConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "electro-on-command")
    public CommandConfig electroOnCommandConfig() {
        return new CommandConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "electro-off-command")
    public CommandConfig electroOffCommandConfig() {
        return new CommandConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "status-command")
    public CommandConfig statusCommandConfig() {
        return new CommandConfig();
    }

    @Bean
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "false")
    public ITemperatureMonitor temperatureMonitor(
            ITemperatureReader temperatureReader,
            SensorConfig sensorConfig,
            IStatisticsManager statisticsManager) {
        return new TemperatureMonitor(temperatureReader, sensorConfig, statisticsManager);
    }

    @Bean
    @ConditionalOnExpression("${main-config.sensorsStub} == false && ${main-config.historyDataMultiThread} == false")
    public IStatisticsLoader statisticsLoader(ITimer timer,
                                              SystemAlertsAggregator systemAlertsAggregator) {
        return new CSVStatisticLoaderSingleThread(timer, systemAlertsAggregator);
    }

    @Bean
    @ConditionalOnExpression("${main-config.sensorsStub} == false && ${main-config.historyDataMultiThread} == true")
    public IStatisticsLoader statisticsLoaderMultiThread(ITimer timer,
                                                         SystemAlertsAggregator systemAlertsAggregator) {
        return new CSVStatisticLoaderMultiThread(timer, systemAlertsAggregator);
    }

    @Bean
    public IStatisticsManager statisticsManager(SensorConfig sensorConfig,
                                                       IStatisticsLoader statisticsLoader,
                                                       ICSVDataSaver csvWriter,
                                                       ITimer timer,
                                                       SystemAlertsAggregator systemAlertsAggregator){
        return new SensorLastDataManager(sensorConfig, statisticsLoader, csvWriter, timer, systemAlertsAggregator);
    }

    @Bean
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "false")
    public SystemAlertsAggregator systemAlertsAggregator(SensorConfig sensorConfig) {
        return new SystemAlertsAggregatorImpl(sensorConfig);
    }

    @Bean
    @ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
    public IStatisticsLoader statisticsLoaderStub(SensorConfig sensorConfig, ITimer timer) {
        return new IStatisticsLoader() {

            @Override
            public List<SensorValueShort> loadLast() throws FileNotFoundException {
                return Collections.emptyList();
            }

            @Override
            public List<SensorValueShort> load(long from, long to, Integer sensorId) throws FileNotFoundException {
                return Collections.emptyList();
            }
        };
    }

    @Bean
    public IRelayAggregator relayAggregator(RelayAggregatorConfig relayAggregatorConfig,
                                            IRelayHandler relayHandler) {
        return new RelayAggregator(relayAggregatorConfig, relayHandler);
    }

    @Bean
    public IRelayHandler relayHandler(CSVStatisticSaver csvStatisticSaver,
                                      IStatisticsManager statisticsManager,
                                      IPinFactory pinFactory,
                                      RelayPinsConfig relayPinsConfig,
                                      SensorConfig sensorConfig) {
        return new RelayHandler(csvStatisticSaver, statisticsManager, pinFactory, relayPinsConfig, sensorConfig);
    }

    @Bean
    public ISensorService sensorService(
            final ThermostatsConfig thermostatsConfig,
            final SensorConfig sensorConfig,
            final RelayPinsConfig relayPinsConfig,
            final IStatisticsManager statisticsManager,
            final SystemAlertsAggregator systemAlertsAggregator,
            final HistoricDataCache historicDataCache,
            final SensorMapper sensorMapper,
            final Map<String, IThermostat> dynamicThermostats,
            final SensorValueMapper sensorValueMapper
    ) {
        return new SensorServiceImpl(
                thermostatsConfig,
                sensorConfig,
                relayPinsConfig,
                historicDataCache,
                systemAlertsAggregator,
                statisticsManager,
                sensorMapper,
                dynamicThermostats,
                sensorValueMapper);
    }

    @Bean
    public IOnOffHandler electricHeatingOnOfHandler(
            final OnOfHandlerConfig onOfHandlerConfig,
            final RelayPinsConfig relayPinsConfig,
            final IRelayHandler relayHandler,
            final Map<String, IThermostat> dynamicThermostats
    ) {
        IThermostat thermostat = dynamicThermostats.get(onOfHandlerConfig.getThermostatCode());
        return new OnOffHandler(onOfHandlerConfig, relayPinsConfig, relayHandler, thermostat);
    }

    @Bean
    public ButtonGasOnOffHandler buttonGasOnOffHandler(ButtonHandlerConfig buttonHandlerConfig,
                                                       final Map<String, IThermostat> dynamicThermostats) {
        IThermostat thermostat = dynamicThermostats.get(buttonHandlerConfig.getThermostatCode());
        return new ButtonGasOnOffHandler(buttonHandlerConfig, thermostat);
    }

    @Bean
    @Primary
    @Qualifier("heatingOnCommand")
    public ISMSCommand heatingOnCommand(
            final Map<String, IThermostat> dynamicThermostats,
            CommandConfig electroOnCommandConfig) {
        return new ThermostatCommand(
                dynamicThermostats,
                electroOnCommandConfig,
                (newTemperature, thermostat) -> thermostat.temperature(newTemperature),
                1);
    }

    @Bean
    @Primary
    @Qualifier("heatingOnCommand")
    public ISMSCommand heatingOffCommand(
            final Map<String, IThermostat> dynamicThermostats,
            CommandConfig electroOffCommandConfig) {
        return new ThermostatCommand(
                dynamicThermostats,
                electroOffCommandConfig,
                (newTemperature, thermostat) -> thermostat.rollback(),
                0
        );
    }

    @Bean
    @Qualifier("gasOnCommand")
    public ISMSCommand gasOnCommand(
            final Map<String, IThermostat> dynamicThermostats,
            final CommandConfig gasOnCommandConfig) {
        return new ThermostatCommand(
                dynamicThermostats,
                gasOnCommandConfig,
                (newTemperature, thermostat) -> thermostat.temperature(newTemperature),
                1
        );
    }

    @Bean
    @Qualifier("gasOffCommand")
    public ISMSCommand gasOffCommand(
            final Map<String, IThermostat> dynamicThermostats,
            CommandConfig gasOffCommandConfig) {
        return new ThermostatCommand(
                dynamicThermostats,
                gasOffCommandConfig,
                (newTemperature, thermostat) -> thermostat.rollback(),
                0
        );
    }

    @Bean
    public ISMSCommand statusCommand(
            final IRelayHandler relayHandler,
            final Map<String, IThermostat> dynamicThermostats,
            final IVoltageMonitor voltageMonitor,
            final CommandConfig statusCommandConfig,
            final RelayPinsConfig relayPinsConfig) {

        return new StatusCommand(relayHandler, dynamicThermostats, voltageMonitor, statusCommandConfig, relayPinsConfig);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10); // Allows multiple concurrent tasks
        scheduler.setThreadNamePrefix("dynamic-sched-");
        scheduler.initialize();
        return scheduler;
    }
}
