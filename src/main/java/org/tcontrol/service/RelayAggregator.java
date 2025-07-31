package org.tcontrol.service;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tcontrol.config.props.RelayAggregatorConfig;
import org.tcontrol.handler.IRelayHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class RelayAggregator implements IRelayAggregator {

  private final RelayAggregatorConfig relayAggregatorConfig;
  private final IRelayHandler relayHandler;

  private final Map<String, List<Boolean>> currentState = new HashMap<>();

  @Scheduled(cron = "${relay-aggregator.schedule}")
  public void cleanCurrentState() {
    currentState.clear();
  }

  @Override
  public PinState turnOnRelay(Pin controlPin) {
    Integer n = relayAggregatorConfig.getPinAggregates().get(controlPin.getName());
    if (n == null) {
      relayHandler.turnOnRelay(controlPin);
      log.info("delegated turnOnRelay: {}", controlPin.getName());
    } else {
      List<Boolean> pinStates = currentState
          .computeIfAbsent(controlPin.getName(), k -> new ArrayList<>());
      pinStates.add(true);
      log.info("aggregating turnOnRelay: {}  size {}", controlPin.getName(), pinStates.size());
      if (pinStates.size() == n) {
        relayHandler.turnOnRelay(controlPin);
        log.info("aggregated delegation turnOnRelay: {}", controlPin.getName());
      }
    }
    return null;
  }

  @Override
  public PinState turnOffRelay(Pin controlPin) {
    Integer n = relayAggregatorConfig.getPinAggregates().get(controlPin.getName());
    if (n == null) {
      relayHandler.turnOffRelay(controlPin);
      log.info("delegated turnOffRelay: {}", controlPin.getName());
      return PinState.LOW;
    } else {
      List<Boolean> pinStates = currentState
          .computeIfAbsent(controlPin.getName(), k -> new ArrayList<>());
      pinStates.add(false);
      log.info("aggregating turnOffRelay: {}  size {}", controlPin.getName(), pinStates.size());
      if (pinStates.size() == n) {
        boolean or = pinStates.stream().reduce(false, (a, b) -> a || b);
        if (!or) {
          relayHandler.turnOffRelay(controlPin);
          log.info("aggregated delegation turnOffRelay: {}", controlPin.getName());
          return PinState.LOW;
        } else {
          relayHandler.turnOnRelay(controlPin);
          log.info("aggregated delegation turnOnRelay: {}", controlPin.getName());
          return PinState.HIGH;
        }
      }
    }
    return null;
  }

  @Override
  public PinState getPinState(Pin controlPin) {
    Integer n = relayAggregatorConfig.getPinAggregates().get(controlPin.getName());
    if (n == null) {
      log.info("delegated getPinState: {}", controlPin.getName());
      return relayHandler.getPinState(controlPin);
    } else {
      List<Boolean> pinStates = currentState.get(controlPin.getName());
      if (pinStates != null && pinStates.size() == n) {
        boolean or = pinStates.stream().reduce(false, (a, b) -> a || b);
        PinState pinState = or ? PinState.HIGH : PinState.LOW;
        log.info("aggregated delegation getPinState: {}, value: {}", controlPin.getName(),
            pinState);
        return pinState;
      }
    }
    log.info("not aggregated getPinState: {}", controlPin.getName());
    return null;
  }
}
