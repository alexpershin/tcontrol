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

import static org.tcontrol.config.props.ThermostatsConfig.ThermostatConfig;

@Slf4j
public class DynamicThermostatBeanCreator implements BeanDefinitionRegistryPostProcessor {

    private final Set<Integer> thermostatIds;

    public DynamicThermostatBeanCreator(final Environment environment) {
        Binder binder = Binder.get(environment);
        final List<HashMap> properties =
                binder.bind("thermostats-config." + ThermostatsConfig.Fields.thermostats,
                        Bindable.listOf(HashMap.class)
                ).get();

        thermostatIds = properties.stream()
                .map(thermostatProps -> (int)(thermostatProps.get(ThermostatConfig.Fields.id)))
                .collect(Collectors.toSet());
    }

    /**
     * For test mode!
     *
     * @param thermostatsConfig test configuration of thermostats;
     */
    public DynamicThermostatBeanCreator(final ThermostatsConfig thermostatsConfig) {
        thermostatIds = thermostatsConfig.thermostatIds();
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        thermostatIds.forEach(thermostatId -> {
            log.info("register my bean: {}", thermostatId);
            Thermostat thermostat = new Thermostat(thermostatId);

            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(Thermostat.class);
            beanDefinition.setInstanceSupplier(() -> thermostat);
            registry.registerBeanDefinition("tcontrol-thermostat-" + thermostatId, beanDefinition);
        });
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
