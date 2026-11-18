package org.tcontrol.controller.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.tcontrol.controller.dto.SystemAlertDto;
import org.tcontrol.models.SystemAlert;

import java.util.Collection;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SystemAlertMapper {
    SystemAlertDto map(SystemAlert sa);
    Collection<SystemAlertDto> map(Collection<SystemAlert> alerts);
}
