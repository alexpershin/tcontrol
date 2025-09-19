import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.tcontrol.handler.IRelayHandler;
import org.tcontrol.service.impl.RelayAggregator;
import org.tcontrol.config.props.RelayAggregatorConfig;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RelayAggregatorTest {

  public static final String GPIO_0 = "GPIO 0";
  public static final String GPIO_1 = "GPIO 1";
  private RelayAggregator relayAggregator;
  private IRelayHandler relayHandler;

  @BeforeEach
  public void before() {
    RelayAggregatorConfig relayAggregatorConfig = new RelayAggregatorConfig();
    relayAggregatorConfig.getPinAggregates().put(GPIO_0, 3);
    relayHandler = mock(IRelayHandler.class);
    relayAggregator = new RelayAggregator(relayAggregatorConfig, relayHandler);
  }

  @Test
  public void test1() {
    Pin pin = RaspiPin.getPinByName(GPIO_0);
    relayAggregator.turnOnRelay(pin);
    Mockito.verify(relayHandler, never()).turnOnRelay(pin);
  }

  @Test
  public void test2() {
    Pin pin = RaspiPin.getPinByName(GPIO_0);
    relayAggregator.turnOnRelay(pin);
    relayAggregator.turnOnRelay(pin);
    Mockito.verify(relayHandler, never()).turnOnRelay(pin);
    assertNull(relayAggregator.getPinState(pin));
  }

  @Test
  public void test3() {
    Pin pin = RaspiPin.getPinByName(GPIO_0);
    relayAggregator.turnOnRelay(pin);
    relayAggregator.turnOnRelay(pin);
    relayAggregator.turnOnRelay(pin);
    Mockito.verify(relayHandler).turnOnRelay(pin);
    Mockito.verify(relayHandler, times(1)).turnOnRelay(pin);
  }

  @Test
  public void testAllOff() {
    Pin pin = RaspiPin.getPinByName(GPIO_0);
    relayAggregator.turnOffRelay(pin);
    relayAggregator.turnOffRelay(pin);
    relayAggregator.turnOffRelay(pin);
    Mockito.verify(relayHandler, times(0)).turnOnRelay(pin);
    Mockito.verify(relayHandler, times(1)).turnOffRelay(pin);
  }

  @Test
  public void testOneOn() {
    Pin pin = RaspiPin.getPinByName(GPIO_0);
    relayAggregator.turnOffRelay(pin);
    relayAggregator.turnOnRelay(pin);
    relayAggregator.turnOffRelay(pin);
    Mockito.verify(relayHandler).turnOnRelay(pin);
    Mockito.verify(relayHandler, times(0)).turnOffRelay(pin);
  }

  @Test
  public void test4() {
    Pin pin = RaspiPin.getPinByName(GPIO_0);
    for (int i = 0; i < 4; i++) {
      relayAggregator.turnOnRelay(pin);
    }
    Mockito.verify(relayHandler, times(1)).turnOnRelay(pin);
  }

  @Test
  public void test5() {
    Pin pin = RaspiPin.getPinByName(GPIO_0);
    for (int i = 0; i < 3; i++) {
      relayAggregator.turnOnRelay(pin);
    }
    relayAggregator.cleanCurrentState();
    for (int i = 0; i < 3; i++) {
      relayAggregator.turnOnRelay(pin);
    }
    Mockito.verify(relayHandler, times(2)).turnOnRelay(pin);

    assertTrue(relayAggregator.getPinState(pin).isHigh());
  }

  @Test
  public void testOff() {
    Pin pin = RaspiPin.getPinByName(GPIO_0);
    for (int i = 0; i < 3; i++) {
      relayAggregator.turnOffRelay(pin);
    }
    relayAggregator.cleanCurrentState();
    for (int i = 0; i < 3; i++) {
      relayAggregator.turnOffRelay(pin);
    }
    Mockito.verify(relayHandler, times(2)).turnOffRelay(pin);

    assertTrue(relayAggregator.getPinState(pin).isLow());
  }

  @Test
  public void testOnNotAggregated() {
    Pin pin = RaspiPin.getPinByName(GPIO_1);
    relayAggregator.turnOnRelay(pin);
    Mockito.verify(relayHandler, times(1)).turnOnRelay(pin);
  }
  @Test
  public void testOffNotAggregated() {
    Pin pin = RaspiPin.getPinByName(GPIO_1);
    relayAggregator.turnOffRelay(pin);
    Mockito.verify(relayHandler, times(1)).turnOffRelay(pin);
  }
  @Test
  public void testStatusNotAggregated() {
    Pin pin = RaspiPin.getPinByName(GPIO_1);
    relayAggregator.getPinState(pin);
    Mockito.verify(relayHandler, times(1)).getPinState(pin);
  }
}
