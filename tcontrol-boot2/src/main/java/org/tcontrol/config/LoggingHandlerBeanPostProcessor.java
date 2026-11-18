package org.tcontrol.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoggingHandlerBeanPostProcessor implements BeanPostProcessor {
    private final Map<String, Class> map = new HashMap<>();
    private final LoggingEnabler enabler;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
//        if (beanClass.isAnnotationPresent(Logging.class)) {
//            map.put(beanName, beanClass);
//        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClass = map.get(beanName);
        if (beanClass != null) {
            return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), (proxy, method, args) -> {
                if (enabler.isEnabled()) {
                    List<String> listArgs = args == null ?
                            Collections.singletonList("no args") :
                            Arrays.stream(args).map(Object::toString).collect(Collectors.toList());

                    System.out.println("Start method: " + beanClass.getTypeName() + "." + method.getName()
                            + " args: " + listArgs);
                    long before = System.nanoTime();
                    Object retVal = method.invoke(bean, args);
                    long after = System.nanoTime();
                    System.out.println("Finish method: " + beanClass.getTypeName() + "." + method.getName()
                            + " time: " + (after - before));
                    return retVal;
                } else {
                    return method.invoke(bean, args);
                }
            });
        }
        return bean;
    }
}
