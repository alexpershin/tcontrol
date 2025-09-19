package org.tcontrol.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.stereotype.Component;
import org.tcontrol.config.props.ThermostatsConfig;
import org.tcontrol.hardware.ITemperatureMonitor;
import org.tcontrol.service.IThermostat;
import org.tcontrol.service.impl.Thermostat;

import java.util.Arrays;

@Slf4j
@AllArgsConstructor
@Component
public class ThermostatBeanPostProcessor implements BeanPostProcessor {

    private final ThermostatsConfig thermostatsConfig;
    private final ConfigurableListableBeanFactory beanFactory;
    private final CommonAnnotationBeanPostProcessor commonAnnotationBeanPostProcessor;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (Arrays.stream(bean.getClass().getInterfaces()).anyMatch(c -> c == ITemperatureMonitor.class)) {
//             log.info("postProcessBeforeInitialization");
//            return commonAnnotationBeanPostProcessor.postProcessBeforeInitialization(bean, beanName);

//            thermostatsConfig.getThermostats().forEach(thermostatConfig -> {
//                log.info("register my bean: {}", thermostatConfig.getName());
//                Thermostat thermostat = new Thermostat(thermostatConfig, null, null, null, null);
//                beanFactory.registerSingleton(thermostatConfig.getName(), thermostat);
//            });
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (Arrays.stream(bean.getClass().getInterfaces()).anyMatch(c -> c == ITemperatureMonitor.class)) {
//            log.info("postProcessAfterInitialization");
//            return commonAnnotationBeanPostProcessor.postProcessAfterInitialization(bean, beanName);
        }
        return bean;
    }
}
