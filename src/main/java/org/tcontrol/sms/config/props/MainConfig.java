package org.tcontrol.sms.config.props;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MainConfig {
    private Integer sensorValuesBufferSize;
    private Boolean sensorsStub;
}
