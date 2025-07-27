package org.tcontrol.service.impl;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.tcontrol.service.W1Reader;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class W1ReaderImpl implements W1Reader {

    @PostConstruct
    private void init() {
        log.info("Initializing W1Reader ...");
    }

    @Override
    @Scheduled(cron = "0/30 * * * * ?")
    public void read(){
        log.info("Reading w1 sensors...");
        W1Master master = new W1Master();
        List<W1Device> w1Devices = master.getDevices(TmpDS18B20DeviceType.FAMILY_CODE);
        for (W1Device device : w1Devices) {
            //this line is enought if you want to read the temperature
            log.info("Temperature: " + ((TemperatureSensor) device).getTemperature());
            //returns the temperature as double rounded to one decimal place after the point

            try {
                log.info("1-Wire ID: " + device.getId() +  " value: " + device.getValue());
                //returns the ID of the Sensor and the  full text of the virtual file
            } catch (IOException e) {
                log.error("", e);
            }
        }
        log.info("Reading w1 sensors COMPLETE");
    }
}
