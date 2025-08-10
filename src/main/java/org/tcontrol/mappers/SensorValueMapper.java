package org.tcontrol.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.tcontrol.controller.dto.SensorValueDto;
import org.tcontrol.service.models.SensorValue;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SensorValueMapper {
    SensorValueDto map(SensorValue sc);
    default SensorValueDto.SensorValueState map(SensorValue.SensorState state){
        return SensorValueDto.SensorValueState.NORMAL;
    }
}
