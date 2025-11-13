package org.tcontrol.controller.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.tcontrol.controller.dto.ThermostatTemperatureDto;
import org.tcontrol.models.ThermostatTemperature;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ThermostatCurrentTemperatureMapper {
    ThermostatTemperature map(ThermostatTemperatureDto dto);
    ThermostatTemperatureDto map(ThermostatTemperature model);
    List<ThermostatTemperatureDto> mapToDto(List<ThermostatTemperature> valueList);
    List<ThermostatTemperature> mapToModel(List<ThermostatTemperatureDto> valueList);
}
