package org.tcontrol.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.stream.Collectors;;

@Aspect
@Component
@Slf4j
@AllArgsConstructor
public class LoggerAspect {

    private final LoggingEnabler enabler;

    @Pointcut("within(@org.tcontrol.config.Logging *)")
    public void loggableMethod() {
    }

    @Around("loggableMethod()")
    public Object logExecutionTime(ProceedingJoinPoint jp) throws Throwable {
        if (enabler.isEnabled()) {
            MethodSignature methodSignature = (MethodSignature) jp.getSignature();
            String className = methodSignature.getDeclaringType().getSimpleName();
            String methodName = methodSignature.getName();

            String args = Arrays.stream(jp.getArgs())
                    .map(Object::toString)
                    .collect(Collectors.joining(","));

            log.info("Start method: {}.{} args: [{}]", className, methodName, args);
            final StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Object retVal = jp.proceed();
            stopWatch.stop();
            log.info("Finish method: {}.{} return:{} time: {}ms",
                    className, methodName, retVal, stopWatch.getTotalTimeMillis());
            return retVal;
        } else {
            return jp.proceed();
        }
    }
//
//    @Before("loggableMethod()")
//    public void beforeCallAtMethod1(JoinPoint jp) {
//        String args = Arrays.stream(jp.getArgs())
//                .map(a -> a.toString())
//                .collect(Collectors.joining(","));
//        log.info("before " + jp.toString() + ", args=[" + args + "]");
//    }
//
//    @After("loggableMethod()")
//    public void afterCallAt(JoinPoint jp) {
//        log.info("after " + jp.toString());
//    }
}
