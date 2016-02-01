package com.tcontrol.sensormonitor.properties;

import com.tcontrol.sensormonitor.properties.ApplicaitonPropertyProducer;
import javax.ejb.ScheduleExpression;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 */
public class ApplicationPropertyProducerTest {

    public ApplicationPropertyProducerTest() {
    }

    @Test(expected = IllegalArgumentException.class)
    public void scheduleExpressionEmptyTest() {
        ApplicaitonPropertyProducer.buildScheduleExpression("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void scheduleExpressionInvalidTest() {
        ApplicaitonPropertyProducer.buildScheduleExpression("abc");
    }

    @Test
    public void scheduleExpressionICorrectTest() {
        ScheduleExpression scExp
                = ApplicaitonPropertyProducer.buildScheduleExpression("1 * * * * * *");
        assertThat(scExp.getSecond(), is("1"));
    }

}
