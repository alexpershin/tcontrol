package com.tcontrol.sensormonitor;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.tcontrol.dao.Sensor;
import com.tcontrol.dao.SensorValue;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 */
@Stateless
@LocalBean
public class AlarmMonitor implements MonitorInterface {

    private static final Logger LOGGER = Logger.getLogger(MonitorInterface.class.getName());

    public void addAlarmListener(final Sensor sensor, final AlarmListener listener) {
        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();
        final Pin pin = RaspiPin.getPinByName(sensor.getSerialNumber());
        GpioPinDigitalInput inputPin = (GpioPinDigitalInput) gpio.getProvisionedPin(pin);
        if (inputPin == null) {
            inputPin = gpio.provisionDigitalInputPin(pin, PinPullResistance.PULL_UP);
        }
        LOGGER.log(Level.INFO, String.format("Add listener for sensor: %s , current state: %s", inputPin.getPin().getName(), inputPin.getState()));
        inputPin.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                listener.alarm(toSensorValue((GpioPinDigitalInput) event.getPin(), sensor));
            }

        });
    }

    @Override
    public SensorValue loadValue(String sensorUUID) throws IOException {
        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();
        try {
            final Pin pin = RaspiPin.getPinByName(sensorUUID);
            GpioPinDigitalInput inputPin = (GpioPinDigitalInput) gpio.getProvisionedPin(pin);
            if (inputPin == null) {
                inputPin = gpio.provisionDigitalInputPin(pin, PinPullResistance.PULL_DOWN);
            }
            SensorValue sensorValue = toSensorValue(inputPin, null);
            return sensorValue;
        } finally {
            //gpio.shutdown();
        }
    }

    private SensorValue toSensorValue(GpioPinDigitalInput inputPin, final Sensor sensor) {
        Double value = -1.0;//undefined
        if (inputPin.getState().isHigh()) {
            value = 0.0;
        } else if (inputPin.getState().isLow()) {
            value = 1.0;
        }
        final SensorValue sensorValue = new SensorValue(-1, System.currentTimeMillis(), value);
        if (sensor != null) {
            sensorValue.setSensorId(sensor.getId());
        }
        return sensorValue;
    }
}
