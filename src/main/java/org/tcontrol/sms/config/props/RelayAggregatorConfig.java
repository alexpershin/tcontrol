package org.tcontrol.sms.config.props;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class RelayAggregatorConfig {
    private Map<String,Integer> pinAggregates = new HashMap<>();
}
