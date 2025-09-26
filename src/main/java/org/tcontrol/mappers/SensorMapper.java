package org.tcontrol.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.tcontrol.service.models.Sensor;
import static org.tcontrol.config.props.SensorConfig.SensorConfiguration;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SensorMapper {
    Sensor map(SensorConfiguration sc);
}
