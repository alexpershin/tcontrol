package org.tcontrol.hardware;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

public interface IVoltageMonitor {

  List<VoltageResult> getVoltageResults();

  @Data
  @AllArgsConstructor
  final class VoltageResult {
    String name;
    Double value;
    String unit;
  }
}
