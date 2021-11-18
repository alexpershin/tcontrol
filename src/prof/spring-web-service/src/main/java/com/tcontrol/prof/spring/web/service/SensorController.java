package com.tcontrol.prof.spring.web.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Web Service.
 */
@RestController
public class SensorController {

  //  private final static Logger LOG = LogManager.getLogger(SensorController.class);
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SensorController.class);

    private AtomicInteger total;
    /**
     * Creates a new instance of GenericResource.
     */
    public SensorController() {
        LOG.debug("Initialisation sensor controller");
        System.out.println("init sensor controller");
    }

    @RequestMapping(value = "/sensors", method = RequestMethod.POST)
    public List<Sensor> sensors() {
        LOG.debug("sensors()");
        return Arrays.asList(new Sensor("indor", 1), new Sensor("outdor", 2));
    }

    @RequestMapping("/logtest/{loops}/{threads}")
    public String logtest(@PathVariable("loops") long loops, @PathVariable("threads") int threads) throws InterruptedException {
        long t1 = System.currentTimeMillis();

        ThreadPoolExecutor executor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
        total = new AtomicInteger(0);
        for (int j = 0; j < threads; j++) {
            final int pid = j;
            executor.submit(() -> {
                for (int i = 0; i < loops; i++) {
                    total.incrementAndGet();
                    LOG.debug("logtest(" + pid + ")");
                }
                return null;
            });
        }
        do {
            Thread.sleep(10);
        } while (executor.getCompletedTaskCount() != threads);
        executor.shutdown();
        long t2 = System.currentTimeMillis();
        return "TIME: " + (t2 - t1) + " ATTEMPTS: " + loops +" total: " + total;
    }
}
