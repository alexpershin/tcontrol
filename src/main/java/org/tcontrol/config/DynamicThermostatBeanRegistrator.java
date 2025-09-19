package org.tcontrol.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.tcontrol.config.props.ThermostatsConfig;
import org.tcontrol.handler.IRelayHandler;
import org.tcontrol.hardware.ITemperatureMonitor;
import org.tcontrol.service.IThermostat;
import org.tcontrol.service.ITimer;
import org.tcontrol.service.impl.Thermostat;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.support.GenericBeanDefinition;

@RequiredArgsConstructor
@Slf4j
@Primary
public class DynamicThermostatBeanRegistrator implements BeanDefinitionRegistryPostProcessor {
    //private ApplicationContext applicationContext;
    private final ThermostatsConfig thermostatsConfig;
    //    private final ITemperatureMonitor temperatureMonitor;
//    private final IRelayHandler relayAggregator;
//    private final ITimer timer;
    private final CommonAnnotationBeanPostProcessor commonAnnotationBeanPostProcessor;

    public static final String PROPERTIES_PREFIX = "thermostats-config";


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        //       Map<String, IThermostat> dynamicThermostats =
        thermostatsConfig.getThermostats().forEach(thermostatConfig -> {
            final String thermostatConfigName = thermostatConfig.getName();
            log.info("register my bean: {}", thermostatConfigName);
            Thermostat thermostat = new Thermostat(thermostatConfigName);

            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(Thermostat.class);
            beanDefinition.setInstanceSupplier(() -> thermostat);
            registry.registerBeanDefinition(thermostatConfigName, beanDefinition);

            //   applicationContext1.registerBean(thermostatConfigName, Thermostat.class, thermostat);
            //   return thermostat;
        });
        //).collect(Collectors.toMap(Thermostat::getName, t -> t));
        //  applicationContext1.registerBean("dynamicThermostats", Map.class, dynamicThermostats);
    }

//    @PostConstruct
//    public void postConstruct() {
//        if (applicationContext instanceof AnnotationConfigServletWebServerApplicationContext) {
//            AnnotationConfigServletWebServerApplicationContext applicationContext1 =
//                    (AnnotationConfigServletWebServerApplicationContext) applicationContext;
//
//            Map<String, IThermostat> dynamicThermostats =
//                    thermostatsConfig.getThermostats().stream().map(thermostatConfig -> {
//                        final String thermostatConfigName = thermostatConfig.getName();
//                        log.info("register my bean: {}", thermostatConfigName);
//                        Thermostat thermostat = new Thermostat(
//                                thermostatConfig,
//                                temperatureMonitor,
//                                relayAggregator,
//                                timer
//                        );
//                        applicationContext1.registerBean(thermostatConfigName, Thermostat.class, thermostat);
//                        return thermostat;
//                    }).collect(Collectors.toMap(Thermostat::getName, t -> t));
//            applicationContext1.registerBean("dynamicThermostats", Map.class, dynamicThermostats);
//        } else {
//            log.error("AnnotationConfigServletWebServerApplicationContext not found, couldn't create thermostats!");
//        }
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //   commonAnnotationBeanPostProcessor.postProcessBeforeInitialization(temperatureMonitor, "temperatureMonitor");

    }
}
