import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import org.junit.jupiter.api.BeforeEach;
import org.tcontrol.sms.IRelayController;
import org.tcontrol.sms.IVoltageController;
import org.tcontrol.sms.IVoltageMonitor;
import org.tcontrol.sms.VoltageController;
import org.tcontrol.sms.config.props.VoltageControllerConfig;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VoltageControllerTest {

    private static final String GPIO_00 = "GPIO_00";
    private static final Integer SENSOR = 0;
    private IVoltageMonitor voltageMonitor;
    private VoltageControllerConfig voltageControllerConfig;
    private IRelayController relayController;

    private IVoltageController voltageController;

    public VoltageControllerTest() {
    }

    @BeforeEach
    public void before() {
        voltageControllerConfig = new VoltageControllerConfig();
        voltageControllerConfig.setValueId(SENSOR);
        voltageControllerConfig.setVoltageMin(11.5f);
        voltageControllerConfig.setVoltageMax(14.4f);
        voltageControllerConfig.getRelayPins().add(GPIO_00);

        voltageMonitor = mock(IVoltageMonitor.class);

        relayController = new IRelayController() {
            PinState currentPinState = PinState.LOW;

            @Override
            public PinState turnOnRelay(Pin controlPin) {
                currentPinState = PinState.HIGH;
                return currentPinState;
            }

            @Override
            public PinState turnOffRelay(Pin controlPin) {
                currentPinState = PinState.LOW;
                return currentPinState;
            }

            @Override
            public PinState getPinState(Pin controlPin) {
                return currentPinState;
            }
        };


        voltageController = new VoltageController(
                voltageControllerConfig,
                voltageMonitor,
                relayController
       );

    }

    private static List<IVoltageMonitor.VoltageResult> vresult(double v){
        List<IVoltageMonitor.VoltageResult> list = new ArrayList<>();
        IVoltageMonitor.VoltageResult value = new IVoltageMonitor.VoltageResult("battery", v, "V");
        list.add(value);
        return list;
    }

    @Test
    public void testMidDay() {
        Pin pin = RaspiPin.getPinByName(GPIO_00);

        assertFalse(voltageController.isOn());
        when(voltageMonitor.getVoltageResults()).thenReturn(vresult(13.0));
        voltageController.checkVoltage();
        assertFalse(voltageController.isOn());
        assertTrue(relayController.getPinState(pin).isLow());
        when(voltageMonitor.getVoltageResults()).thenReturn(vresult(14.3));
        voltageController.checkVoltage();
        assertFalse(voltageController.isOn());
        when(voltageMonitor.getVoltageResults()).thenReturn(vresult(14.4));
        voltageController.checkVoltage();
        assertTrue(voltageController.isOn());
        assertTrue(relayController.getPinState(pin).isHigh());
        when(voltageMonitor.getVoltageResults()).thenReturn(vresult(14.5));
        voltageController.checkVoltage();
        assertTrue(voltageController.isOn());
        when(voltageMonitor.getVoltageResults()).thenReturn(vresult(14.5));
        voltageController.checkVoltage();
        assertTrue(voltageController.isOn());
        when(voltageMonitor.getVoltageResults()).thenReturn(vresult(14.4));
        voltageController.checkVoltage();
        assertTrue(voltageController.isOn());
        when(voltageMonitor.getVoltageResults()).thenReturn(vresult(14.0));
        voltageController.checkVoltage();
        assertTrue(voltageController.isOn());
        when(voltageMonitor.getVoltageResults()).thenReturn(vresult(13.0));
        voltageController.checkVoltage();
        assertTrue(voltageController.isOn());
        when(voltageMonitor.getVoltageResults()).thenReturn(vresult(11.6));
        voltageController.checkVoltage();
        assertTrue(voltageController.isOn());
        when(voltageMonitor.getVoltageResults()).thenReturn(vresult(11.5));
        voltageController.checkVoltage();
        assertFalse(voltageController.isOn());
        assertTrue(relayController.getPinState(pin).isLow());
        when(voltageMonitor.getVoltageResults()).thenReturn(vresult(11.3));
        voltageController.checkVoltage();
        assertFalse(voltageController.isOn());
        when(voltageMonitor.getVoltageResults()).thenReturn(vresult(11.6));
        voltageController.checkVoltage();
        assertFalse(voltageController.isOn());
        when(voltageMonitor.getVoltageResults()).thenReturn(vresult(14.4));
        voltageController.checkVoltage();
        assertTrue(voltageController.isOn());
    }
}
