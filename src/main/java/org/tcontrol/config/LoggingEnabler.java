package org.tcontrol.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.management.*;
import javax.management.modelmbean.DescriptorSupport;
import java.lang.management.ManagementFactory;
import java.util.Collections;

@Getter
@Setter
@Service
@FieldNameConstants
public class LoggingEnabler implements LoggingEnablerMBean, DynamicMBean {
    private boolean enabled;

    private MBeanInfo mBeanInfo = null;

    @PostConstruct
    public void init() throws MalformedObjectNameException, NotCompliantMBeanException,
            InstanceAlreadyExistsException, MBeanRegistrationException {
        buildDynamicMBeanInfo();
        MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        platformMBeanServer.registerMBean(this,
                new ObjectName(this.getClass().getPackage().getName(), "type", "logging"));
    }

    @Override
    public Object getAttribute(String attribute) {
        return Fields.enabled.equals(attribute) ? enabled : null;
    }

    @Override
    public void setAttribute(Attribute attribute) {
        if (attribute.getName().equals(Fields.enabled)) {
            enabled = (Boolean) attribute.getValue();
        }
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        return new AttributeList(Collections.singletonList(new Attribute(Fields.enabled, enabled)));
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        return null;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        return null;
    }

    private void buildDynamicMBeanInfo() {
        MBeanAttributeInfo[] dAttributes = new MBeanAttributeInfo[]{
                new MBeanAttributeInfo(Fields.enabled, boolean.class.getSimpleName(),
                        "Indicates if logging is enable", true, true, true
                )
        };

        mBeanInfo = new MBeanInfo(this.getClass().getName(), null, dAttributes, null, null,
                new MBeanNotificationInfo[0]);
    }
}
