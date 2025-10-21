package org.tcontrol.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.tcontrol.config.props.ThermostatsConfig;
import org.tcontrol.service.impl.Thermostat;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.support.GenericBeanDefinition;

import static java.lang.String.valueOf;
import static org.tcontrol.config.props.ThermostatsConfig.ThermostatConfig;

@Slf4j
public class DynamicThermostatBeanCreator implements BeanDefinitionRegistryPostProcessor {

    private final Set<String> thermostatNames;

    public DynamicThermostatBeanCreator(final Environment environment) {
        Binder binder = Binder.get(environment);
        final List<HashMap> properties =
                binder.bind("thermostats-config." + ThermostatsConfig.Fields.thermostats,
                        Bindable.listOf(HashMap.class)
                ).get();

        thermostatNames = properties.stream()
                .map(thermostatProps -> valueOf(thermostatProps.get(ThermostatConfig.Fields.name)))
                .collect(Collectors.toSet());
    }

    /**
     * For test mode!
     * @param thermostatsConfig test configuration of thermostats;
     */
    public DynamicThermostatBeanCreator(final ThermostatsConfig thermostatsConfig) {
        thermostatNames = thermostatsConfig.thermostatNames();
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        thermostatNames.forEach(thermostatName -> {
            log.info("register my bean: {}", thermostatName);
            Thermostat thermostat = new Thermostat(thermostatName);

            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(Thermostat.class);
            beanDefinition.setInstanceSupplier(() -> thermostat);
            registry.registerBeanDefinition(thermostatName, beanDefinition);
        });
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
