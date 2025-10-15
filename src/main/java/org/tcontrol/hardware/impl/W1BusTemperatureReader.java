package org.tcontrol.hardware.impl;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tcontrol.hardware.ITemperatureReader;
import org.tcontrol.service.models.SensorValue;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class W1BusTemperatureReader implements ITemperatureReader {

    private final Map<String, SensorValue> lastValues = new HashMap<>();

    @PostConstruct
    private void init() {
        log.info("Initializing W1Reader ...");
    }

    @Override
    public void load(){
        log.info("Reading w1 sensors...");

        lastValues.clear();

        loadValues();

        log.info("Reading w1 sensors COMPLETE");
    }

    private void loadValues() {
        W1Master master = new W1Master();
        List<W1Device> w1Devices = master.getDevices(TmpDS18B20DeviceType.FAMILY_CODE);

        for (W1Device device : w1Devices) {
            try {
                double temperature = ((TemperatureSensor) device).getTemperature();
                log.info("Temperature: {}, 1-Wire ID: {}  value: {}", temperature, device.getId(),device.getValue());
                final SensorValue sensorValue =
                        new SensorValue(device.getId(), System.currentTimeMillis(), temperature);
                lastValues.put(device.getId(), sensorValue);
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

    @Override
    public SensorValue getValue(String sensorCode) throws IOException {
        return lastValues.get(sensorCode);
    }
}
