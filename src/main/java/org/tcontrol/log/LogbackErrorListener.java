package org.tcontrol.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tcontrol.service.SystemAlertsAggregator;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class LogbackErrorListener extends AppenderBase<ILoggingEvent> {

    private final SystemAlertsAggregator systemAlertsAggregator;

    @PostConstruct
    public void init(){
        Logger rootLogger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        setContext(rootLogger.getLoggerContext());
        start();
        rootLogger.addAppender(this);
    }

    @Override
    protected void append(ILoggingEvent event) {
        // Filter specifically for ERROR events
        if (event.getLevel().equals(Level.ERROR)) {
            String message = event.getFormattedMessage();
            Throwable throwable = (event.getThrowableProxy() != null)
                    ? ((ch.qos.logback.classic.spi.ThrowableProxy) event.getThrowableProxy()).getThrowable()
                    : null;

            systemAlertsAggregator.addAlert(message);
        }
    }

}