package org.tcontrol.controller.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.tcontrol.controller.dto.SensorValueShortDto;
import org.tcontrol.models.SensorValueShort;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SensorValueShortMapper {
    SensorValueShortDto map(SensorValueShort sc);
    List<SensorValueShortDto> map(List<SensorValueShort> valueList);
}
