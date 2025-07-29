package org.tcontrol.sms.config.props;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class SensorConfig {
    private List<SensorConfiguration> sensors = new ArrayList<>();
}
