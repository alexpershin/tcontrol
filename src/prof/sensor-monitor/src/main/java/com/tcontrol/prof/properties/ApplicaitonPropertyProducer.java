package com.tcontrol.prof.properties;

import java.util.StringTokenizer;
import javax.ejb.ScheduleExpression;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

public class ApplicaitonPropertyProducer {

    @Inject
    private PropertyFileResolver fileResolver;

    @Produces
    @ApplicationProperty(name = "")
    public String getPropertyAsString(InjectionPoint injectionPoint) {

        String propertyName = injectionPoint.getAnnotated().getAnnotation(ApplicationProperty.class).name();
        String value = fileResolver.getProperty(propertyName);

        if (value == null || propertyName.trim().length() == 0) {
            throw new IllegalArgumentException("No property found with name '" + propertyName + "'");
        }
        return value;
    }

    @Produces
    @ApplicationProperty(name = "")
    public Integer getPropertyAsInteger(InjectionPoint injectionPoint) {
        String value = getPropertyAsString(injectionPoint);
        return value == null ? null : Integer.valueOf(value);
    }

    @Produces
    @ApplicationProperty(name = "")
    public ScheduleExpression getPropertyAsCronExpression(InjectionPoint injectionPoint) {
        String value = getPropertyAsString(injectionPoint);

        ScheduleExpression scheduleExp;
        try {
            scheduleExp = buildScheduleExpression(value);
        } catch (Exception ex) {
            final String message = String.format("Couldont parse schedule string '%1$s'", value);
            throw new IllegalArgumentException(message, ex);
        }
        return scheduleExp;
    }

    private ScheduleExpression buildScheduleExpression(String expression) {
        ScheduleExpression scheduleExp = new ScheduleExpression();
        StringTokenizer cronTokenizer = new StringTokenizer(expression);
        if (cronTokenizer.countTokens() != 7) {
            throw new IllegalArgumentException(
                    "Invalid schedule expression '" + expression + "', must contain 7 parts.");
        }
        scheduleExp.second(cronTokenizer.nextToken())
                .minute(cronTokenizer.nextToken())
                .hour(cronTokenizer.nextToken())
                .dayOfMonth(cronTokenizer.nextToken())
                .month(cronTokenizer.nextToken())
                .dayOfWeek(cronTokenizer.nextToken())
                .year(cronTokenizer.nextToken());
        return scheduleExp;
    }

}
