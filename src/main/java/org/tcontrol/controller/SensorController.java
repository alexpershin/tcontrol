package org.tcontrol.controller;

import org.springframework.web.bind.annotation.RequestParam;
import org.tcontrol.controller.dto.*;
import org.tcontrol.service.SensorServiceException;
import org.tcontrol.service.SensorService;
import org.tcontrol.service.models.Sensor;
import org.tcontrol.service.models.SensorValue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.tcontrol.controller.dto.SensorValueDto.SensorValueState.*;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequiredArgsConstructor
public class SensorController {
    private final static Logger LOGGER = Logger.getLogger(SensorController.class.getName());

    private final SensorService sensorService;

    //Temp user
    private static final int CURRENT_USER_ID = 1;

    @PostConstruct
    public void AfterBegin() {
        LOGGER.log(Level.INFO, "after begin");
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

    public SensorValueDto.SensorsResult getSensors() {
        LOGGER.log(Level.INFO, "get sensors");
        SensorValueDto.SensorsResult result = new SensorValueDto.SensorsResult();
        Collection<Sensor> sensors;
        try {
            sensors = sensorService.getUserSensors(CURRENT_USER_ID).values();
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
        SensorValuesResult result = new SensorValuesResult();
        List<SensorValue> values = sensorService.getCurrentValues(CURRENT_USER_ID, null);
        List<SensorValueDto> webValues = convertSensorValuesToWeb(values);
        result.setValues(webValues.toArray(new SensorValueDto[]{}));
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
            parameters = {@Parameter(name = "campaignName", description = "Параметр для поиска кампаний по шаблону наименования",
                    in = ParameterIn.QUERY,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(maxLength = 255, example = "Кредит"))
            )}
    )
    public SensorValuesResult getSensorPrevValues(@RequestParam(name = "sensorId")Integer sensorId)  throws SensorServiceException {
        SensorValuesResult result = new SensorValuesResult();
        List<SensorValue> values = sensorService.getPrevSensorValues(CURRENT_USER_ID, null, sensorId);
        List<SensorValueDto> webValues = convertSensorValuesToWeb(values);
        result.setValues(webValues.toArray(new SensorValueDto[]{}));
        return result;
    }


    @PostMapping("/start_process")
    @CrossOrigin(origins = {"http://localhost:8090", "http://localhost"})
    public ResponseEntity<StartProcessResult> startProcess(StartProcessRequest processChangeRequest) throws InterruptedException {
        Thread.sleep(5000);
        int result = sensorService.startProcess(processChangeRequest.getSensorId());
        return new ResponseEntity<>(new StartProcessResult(result == -1 ? null : result, "ok"),
                result == -1 ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK);
    }

    @PostMapping("/stop_process")
    @CrossOrigin(origins = {"http://localhost:8090", "http://localhost"})
    public ResponseEntity<StopProcessResult> stopProcess(StartProcessRequest processChangeRequest) throws InterruptedException {
        Thread.sleep(5000);
        int result = sensorService.stopProcess(processChangeRequest.getSensorId());
        return new ResponseEntity<>(new StopProcessResult(result == -1 ? null : result, "ok"),
                result == -1 ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK);
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
                    sensor.getName(),
                    sensor.getId(),
                    sensor.getType()
            );
            sensorWeb.setDescription(sensor.getDescription());

            webSensors.add(sensorWeb);
        }
        return webSensors;
    }

    List<SensorValueDto> convertSensorValuesToWeb(List<SensorValue> values) throws SensorServiceException {

        List<SensorValueDto> webValues = new ArrayList<>();

        final Map<Integer, Sensor> allSensors = sensorService.getAllSensors();

        for (SensorValue value : values) {

            Sensor sensor = allSensors.get(value.getSensorId());

            SensorValueDto webValue = new SensorValueDto(
                    value.getSensorId(),
                    value.getTimestamp().getTime(),
                    value.getValue(),
                    calculateSate(value, sensor),
                    value.getGradient()
            );

            webValues.add(webValue);
        }
        return webValues;
    }

    public static SensorValueDto.SensorValueState calculateSate(SensorValue value,
                                                                Sensor sensor) {

        SensorValueDto.SensorValueState result = NORMAL;

        double v = value.getValue();
        if (Sensor.SensorType.ALARM == sensor.getType()
                || Sensor.SensorType.ON_OFF == sensor.getType()) {
            result = value.getValue() == 0 ? NORMAL : ALERT;

        } else //calculate state according to threshold
            //low thressHold
            if (value.getValue() <= sensor.getLowThreshold()) {
                result = ALERT;
            } else if (v <= sensor.getLowThreshold() + sensor.getThresholdLag()
                    && v > sensor.getLowThreshold()) {
                result = WARNING;
            } //high thressHold
            else if (v >= sensor.getHighThreshold()) {
                result = ALERT;
            } else if (v >= sensor.getHighThreshold() - sensor.getThresholdLag()
                    && v < sensor.getHighThreshold()) {
                result = WARNING;
            }
        return result;
    }


}
