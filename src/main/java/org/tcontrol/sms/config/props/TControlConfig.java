package org.tcontrol.sms.config.props;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TControlConfig {
    private Integer sensorValuesBufferSize;
    private Boolean sensorsStub;
}
