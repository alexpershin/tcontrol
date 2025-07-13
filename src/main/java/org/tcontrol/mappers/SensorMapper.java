package org.tcontrol.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.tcontrol.service.models.Sensor;
import org.tcontrol.sms.config.props.SensorConfiguration;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SensorMapper {
    Sensor map(SensorConfiguration sc);
}
