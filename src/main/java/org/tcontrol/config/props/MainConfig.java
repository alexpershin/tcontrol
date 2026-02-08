package org.tcontrol.config.props;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MainConfig {
    private Integer sensorValuesBufferSize;
    private Boolean sensorsStub;
    private Boolean historyDataMultiThread;
}
