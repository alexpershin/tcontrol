package com.tcontrol.controller;

import com.tcontrol.controller.dto.SensorValueWeb;
import com.tcontrol.controller.dto.SensorValuesResult;
import com.tcontrol.controller.dto.SensorWeb;
import com.tcontrol.controller.dto.SensorsResult;
import com.tcontrol.dao.DaoException;
import com.tcontrol.dao.DaoInterface;
import com.tcontrol.dao.Sensor;
import com.tcontrol.dao.SensorValue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.tcontrol.controller.dto.SensorValueWeb.SensorValueState.*;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequiredArgsConstructor
public class SensorController {
    private final static Logger LOGGER = Logger.getLogger(SensorController.class.getName());

    private final DaoInterface dao;

    //Temp user
    private static final int CURRENT_USER_ID = 1;

    @PostConstruct
    public void AfterBegin(){
        LOGGER.log(Level.INFO,"after begin");
    }

    /**
     * Returns sensors.
     *
     * @return sensors list;
     */
    @GetMapping("/sensors")
    @Operation(summary = "Получение списка сенсоров",
            description = "Получение списка сенсоров",
            responses = {@ApiResponse(responseCode = ""+HTTP_OK,
                    description = "Список сенсоров",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SensorsResult.class))
                    )),
                    @ApiResponse(responseCode = ""+HTTP_BAD_REQUEST,
                            content = @Content(mediaType = "application/json"))})

    public SensorsResult getSensors() {
        LOGGER.log(Level.INFO,"get sensors");
        SensorsResult result = new SensorsResult();
        Collection<Sensor> sensors;
        try {
            sensors = dao.getUserSensors(CURRENT_USER_ID).values();
        } catch (DaoException ex) {
            throw buildWebApplicationException(ex);
        }
        List<SensorWeb> webSensors = convertSensorToWeb(sensors);
        result.setSensors(webSensors.toArray(new SensorWeb[]{}));
        return result;
    }

    /**
     * Current values.
     *
     * @return list of values;
     * @throws com.tcontrol.dao.DaoException
     */
    @GetMapping("/sensor_values")
    public SensorValuesResult getSensorValues() throws DaoException {
        SensorValuesResult result = new SensorValuesResult();
        List<SensorValue> values = dao.getCurrentValues(CURRENT_USER_ID);
        List<SensorValueWeb> webValues = convertSensorValuesToWeb(values);
        result.setValues(webValues.toArray(new SensorValueWeb[]{}));
        return result;
    }

    private ResponseStatusException buildWebApplicationException(DaoException ex) {
        Logger.getLogger(SensorController.class.getName()).log(Level.SEVERE, null, ex);
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage() + ":\n" + ex.getCause().getMessage()
        );
    }

    List<SensorWeb> convertSensorToWeb(Collection<Sensor> sensors) {
        List<SensorWeb> webSensors = new ArrayList<>();
        for (Sensor sensor : sensors) {
            final SensorWeb sensorWeb = new SensorWeb(
                    sensor.getName(),
                    sensor.getId(),
                    sensor.getType()
            );
            sensorWeb.setDescription(sensor.getDescription());

            webSensors.add(sensorWeb);
        }
        return webSensors;
    }

    List<SensorValueWeb> convertSensorValuesToWeb(List<SensorValue> values) throws DaoException {

        List<SensorValueWeb> webValues = new ArrayList<>();

        final Map<Integer, Sensor> allSensors = dao.getAllSensors();

        for (SensorValue value : values) {

            Sensor sensor = allSensors.get(value.getSensorId());

            SensorValueWeb webValue = new SensorValueWeb(
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

    public static SensorValueWeb.SensorValueState calculateSate(SensorValue value,
                                                         Sensor sensor) {

        SensorValueWeb.SensorValueState result = NORMAL;

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
