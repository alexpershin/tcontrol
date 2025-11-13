package org.tcontrol.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.tcontrol.config.Logging;
import org.tcontrol.controller.dto.*;
import org.tcontrol.controller.mappers.SensorValueMapper;
import org.tcontrol.controller.mappers.ThermostatCurrentTemperatureMapper;
import org.tcontrol.service.SensorServiceException;
import org.tcontrol.service.ISensorService;
import org.tcontrol.models.Sensor;
import org.tcontrol.models.SensorValue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("tcontrol/api")
@Logging
public class SensorController {
    private final ISensorService ISensorService;
    private final SensorValueMapper sensorValueMapper;
    private final ThermostatCurrentTemperatureMapper thermostatCurrentTemperatureMapper;

    //Temp user
    private static final int CURRENT_USER_ID = 1;

    @PostConstruct
    public void init() {
        log.debug("init");
    }

    /**
     * Returns sensors.
     *
     * @return sensors list;
     */
    @PostMapping("/sensors")
    @CrossOrigin(origins = {"http://localhost:8090", "http://localhost"})
    @Operation(summary = "Получение списка сенсоров",
            description = "Получение списка сенсоров",
            responses = {@ApiResponse(responseCode = "" + HTTP_OK,
                    description = "Список сенсоров",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SensorValueDto.SensorsResult.class))
                    )),
                    @ApiResponse(responseCode = "" + HTTP_BAD_REQUEST,
                            content = @Content(mediaType = "application/json"))})

    public SensorValueDto.SensorsResult getSensors() throws InterruptedException {
        log.info("get sensors");
        Thread.sleep(1000);
        SensorValueDto.SensorsResult result = new SensorValueDto.SensorsResult();
        Collection<Sensor> sensors;
        try {
            sensors = ISensorService.getUserSensors(CURRENT_USER_ID).values();
        } catch (SensorServiceException ex) {
            throw buildWebApplicationException(ex);
        }
        List<SensorDto> webSensors = convertSensorToWeb(sensors);
        result.setSensors(webSensors.toArray(new SensorDto[]{}));
        return result;
    }

    /**
     * Current values.
     *
     * @return list of values;
     * @throws SensorServiceException if some exeption;
     */
    @PostMapping("/sensor_values")
    @CrossOrigin(origins = {"http://localhost:8090", "http://localhost"})
    @Operation(summary = "Получение значений сенсоров",
            description = "Получение значений сенсоров",
            responses = {@ApiResponse(responseCode = "" + HTTP_OK,
                    description = "Список значений сенсоров",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SensorValuesResult.class))
                    )),
                    @ApiResponse(responseCode = "" + HTTP_BAD_REQUEST,
                            content = @Content(mediaType = "application/json"))})
    public SensorValuesResult getSensorValues() throws SensorServiceException {
        log.info("get values");
        final List<SensorValue> values = ISensorService.getCurrentValues(CURRENT_USER_ID, null);

        final SensorValuesResult result = new SensorValuesResult();
        result.setTime(System.currentTimeMillis());
        result.setValues(sensorValueMapper.map(values).toArray(new SensorValueDto[]{}));
        return result;
    }

    /**
     * Current values.
     *
     * @return list of values;
     * @throws SensorServiceException if some exeption;
     */
    @PostMapping("/sensor_previous_values")
    @CrossOrigin(origins = {"http://localhost:8090", "http://localhost"})
    @Operation(summary = "Получение предыдущих значений сенсора",
            description = "Получение предыдущих значений сенсора",
            responses = {@ApiResponse(responseCode = "" + HTTP_OK,
                    description = "Список значений сенсоров",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SensorValuesResult.class))
                    )),
                    @ApiResponse(responseCode = "" + HTTP_BAD_REQUEST,
                            content = @Content(mediaType = "application/json"))
            },
            parameters = {@Parameter(name = "sensorId", description = "Идентификатор сенсора",
                    in = ParameterIn.QUERY,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(maxLength = 4, example = "1"))
            )}
    )
    public SensorValuesResult getSensorPrevValues(@RequestParam(name = "sensorId") Integer sensorId) throws SensorServiceException {
        SensorValuesResult result = new SensorValuesResult();
        List<SensorValue> values = ISensorService.getPrevSensorValues(CURRENT_USER_ID, null, sensorId);
        List<SensorValueDto> webValues = sensorValueMapper.map(values);
        result.setValues(webValues.toArray(new SensorValueDto[]{}));
        return result;
    }

    @PostMapping("/start_process")
    @CrossOrigin(origins = {"http://localhost:8090", "http://localhost"})
    @Operation(summary = "Старт отопления",
            description = "Старт отопления",
            responses = {@ApiResponse(responseCode = "" + HTTP_BAD_REQUEST, content = @Content(mediaType = "application/json"))
            },
            parameters = {@Parameter(name = "sensorId", description = "Идентификатор сенсора",
                    in = ParameterIn.QUERY,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(maxLength = 4, example = "1"))
            )}
    )
    public ResponseEntity<StartHeatingResult> startProcess(@RequestBody final StartHeatingRequest processChangeRequest)
            throws InterruptedException {
        Thread.sleep(1000);
        int result = ISensorService.changeThermostatOptions(thermostatCurrentTemperatureMapper.mapToModel(processChangeRequest.getNewTemperatures()));
        return new ResponseEntity<>(new StartHeatingResult(result, "ok", System.currentTimeMillis()),
                HttpStatus.OK);
    }

    @PostMapping("/thermostat_current_temperature")
    @CrossOrigin(origins = {"http://localhost:8090", "http://localhost"})
    @Operation(summary = "Текущая температура",
            description = "Текущая температура",
            responses = {@ApiResponse(responseCode = "" + HTTP_BAD_REQUEST, content = @Content(mediaType = "application/json"))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Тело запроса для старта отопления", required = true)
    )
    public ResponseEntity<List<ThermostatTemperatureDto>> thermostatCurrentTemperature(@RequestParam(name = "sensorId") Integer sensorId) {
        log.info("current temperature for sensor: {}", sensorId);
        if (sensorId == null || sensorId < 0) {
            throw new IllegalArgumentException("sensorId has to be not null and positive number");
        }
        List<ThermostatTemperatureDto> result = thermostatCurrentTemperatureMapper.mapToDto(ISensorService.thermostatCurrentTemperature(sensorId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private ResponseStatusException buildWebApplicationException(SensorServiceException ex) {
        Logger.getLogger(SensorController.class.getName()).log(Level.SEVERE, null, ex);
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage() + ":\n" + ex.getCause().getMessage()
        );
    }

    List<SensorDto> convertSensorToWeb(Collection<Sensor> sensors) {
        List<SensorDto> webSensors = new ArrayList<>();
        for (Sensor sensor : sensors) {
            final SensorDto sensorWeb = new SensorDto(
                    sensor.getUserName(),
                    sensor.getId(),
                    sensor.getType()
            );
            sensorWeb.setDescription(sensor.getDescription());

            webSensors.add(sensorWeb);
        }
        return webSensors;
    }
}
