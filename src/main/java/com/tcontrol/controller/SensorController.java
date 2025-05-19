package com.tcontrol.controller;

import com.tcontrol.controller.dto.SensorWeb;
import com.tcontrol.controller.dto.SensorsResult;
import com.tcontrol.dao.DaoException;
import com.tcontrol.dao.DaoInterface;
import com.tcontrol.dao.Sensor;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequiredArgsConstructor
public class SensorController {
    private final static Logger LOGGER = Logger.getLogger(SensorController.class.getName());

    private final DaoInterface dao;

    //Temp user
    private final int CURRENT_USER_ID = 1;

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


}
