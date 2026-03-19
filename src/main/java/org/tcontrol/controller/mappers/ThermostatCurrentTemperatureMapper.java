package org.tcontrol.controller.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.tcontrol.controller.dto.ThermostatTemperatureDto;
import org.tcontrol.models.ThermostatTemperature;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ThermostatCurrentTemperatureMapper {
    @Mapping(target = "nightTime", expression = "java(convertUTCTimeToLocalTime(dto.getNightTime()))")
    @Mapping(target = "dayTime", expression = "java(convertUTCTimeToLocalTime(dto.getDayTime()))")
    ThermostatTemperature map(ThermostatTemperatureDto dto)  throws ParseException;

    @Mapping(target = "nightTime", expression = "java(convertLocalTimeToUTC(model.getNightTime()))")
    @Mapping(target = "dayTime", expression = "java(convertLocalTimeToUTC(model.getDayTime()))")
    ThermostatTemperatureDto map(ThermostatTemperature model);

    List<ThermostatTemperatureDto> mapToDto(List<ThermostatTemperature> valueList);
    List<ThermostatTemperature> mapToModel(List<ThermostatTemperatureDto> valueList);

    default LocalTime convertUTCTimeToLocalTime(String timeInUtcString) throws ParseException {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // 2. Parse the time string to a LocalTime object
        LocalTime utcLocalTime = LocalTime.parse(timeInUtcString, timeFormatter);

        // 3. Get today's date in the UTC time zone.
        // This provides the necessary date context for the time of day.
        LocalDate todayUtc = LocalDate.now(ZoneId.of("UTC"));

        // 4. Combine the UTC date and time into a LocalDateTime object.
        LocalDateTime utcDateTime = LocalDateTime.of(todayUtc, utcLocalTime);

        // 5. Convert the UTC LocalDateTime to an Instant (a point in time).
        Instant instant = utcDateTime.toInstant(ZoneId.of("UTC").getRules().getOffset(utcDateTime));

        // 6. Convert the Instant to a LocalDateTime in the system's default time zone.
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        return localDateTime.toLocalTime();
    }

    default String convertLocalTimeToUTC(LocalTime localTime){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        OffsetTime offsetTime = localTime.atOffset(OffsetDateTime.now().getOffset());

        String formattedTime = offsetTime.withOffsetSameInstant( ZoneOffset.UTC).format(formatter);

        return formattedTime;//zdtInUTC.format(formatter);
    }
}
