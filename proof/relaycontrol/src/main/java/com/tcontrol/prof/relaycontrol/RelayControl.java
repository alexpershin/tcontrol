package com.tcontrol.prof.relaycontrol;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.util.Optional;

public class RelayControl {

    public static void main(String[] args) throws InterruptedException {
        //true - set on, false - set off, not present -status;
        Optional<Boolean> action = null;
        Pin controlPin = null;
        int timeout=60;
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("on")) {
                action = Optional.of(true);
            } else if (args[0].equalsIgnoreCase("off")) {
                action = Optional.of(false);
            } else if (args[0].equalsIgnoreCase("status")) {
                action = Optional.empty();
            }
            controlPin = RaspiPin.getPinByName(args[1]);
            try {
                timeout = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid value timeout: " + args[2]);
                System.exit(1);
            }
        } else {
            System.out.println("Parameters must be: <value> <pin_name> <timeout>");
            System.out.println("<value> - 'on', 'off' or 'status'");
            System.out.println("<pin_name> - 'GPIO <num>' for example 'GPIO 0'");
            System.out.println("<timeout> - seconds, for example '60'");
            System.exit(1);
        }

        if (action == null) {
            System.out.println("Invalid value argument! Argument must be 'on', 'off' or 'status'!");
            System.exit(1);
        }
        if (controlPin == null) {
            System.out.println("Invalid pin argument! Argument must be like 'GPIO 0'!");
            System.exit(1);
        }
        System.out.println("GPIO Relay Control started.");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput pin
                = gpio.provisionDigitalOutputPin(controlPin, "Heat Relay", PinState.LOW);

        if (action.isPresent()) {
            PinState pinState = action.get() ? PinState.HIGH : PinState.LOW;

            // set shutdown state for this pin
            pin.setShutdownOptions(true, pinState);

            System.out.println("--> GPIO state was: " + pin.getState());

            // turn off/on gpio pin #17
            if (pinState.isLow()) {
                pin.low();
            } else if (pinState.isHigh()) {
                pin.high();
            }

            Thread.sleep(timeout*1000);
        }

        System.out.println("--> GPIO state is: " + pin.getState());

        // turn on gpio pin  
        System.out.println("--> GPIO shutdown");

        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        gpio.shutdown();

        System.out.println("Exiting Relay Control");

        System.exit(0);
    }
}
