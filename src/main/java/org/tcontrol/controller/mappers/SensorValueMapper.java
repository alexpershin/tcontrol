package org.tcontrol.controller.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.tcontrol.controller.dto.SensorValueDto;
import org.tcontrol.models.SensorValue;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SensorValueMapper {
    SensorValueDto map(SensorValue sc);

    SensorValue clone(SensorValue sc);

    List<SensorValueDto> map(List<SensorValue> valueList);

    default SensorValueDto.SensorValueState map(SensorValue.SensorState state) {
        if (state != null) {
            switch (state) {
                case ALARM:
                    return SensorValueDto.SensorValueState.ALERT;
                case NORMAL:
                    return SensorValueDto.SensorValueState.NORMAL;
                case WARNING:
                    return SensorValueDto.SensorValueState.WARNING;
            }
        }
        return null;
    }
}
