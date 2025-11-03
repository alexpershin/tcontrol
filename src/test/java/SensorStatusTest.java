import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.service.impl.SensorLastDataSaver;
import org.tcontrol.service.models.Sensor;
import org.tcontrol.service.models.SensorValue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.tcontrol.service.models.SensorValue.SensorState.NORMAL;
import static org.tcontrol.service.models.SensorValue.SensorState.WARNING;
import static org.tcontrol.service.models.SensorValue.SensorState.ALARM;

import static org.tcontrol.config.props.SensorConfig.SensorConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.tcontrol.service.models.Sensor.SensorType.*;

public class SensorStatusTest {

    private SensorValue value;
    private SensorConfiguration sensorConfiguration;
    private SensorValue.SensorState state;//result

    public static Stream<Arguments> data() {
        int id = 1;
        String code = "1";
        long time = System.currentTimeMillis();

        return Stream.of(
                Arguments.of(
                        new SensorValue(id, time, 20.0),
                        new SensorConfiguration(id, code, "Indoor", TEMPERATURE, "", "", 10, 30, 2),
                        NORMAL
                ),
                Arguments.of(
                        new SensorValue(id, time, 12.0),
                        new SensorConfiguration(id, code, "Indoor", TEMPERATURE, "", "", 10, 30, 2),
                        WARNING
                ),
                Arguments.of(
                        new SensorValue(id, time, 11.0),
                        new SensorConfiguration(id, code, "Indoor", TEMPERATURE, "", "", 10, 30, 2),
                        WARNING
                ),
                Arguments.of(
                        new SensorValue(id, time, 10.0),
                        new SensorConfiguration(id, code, "Indoor", TEMPERATURE, "", "", 10, 30, 2),
                        ALARM
                ),
                Arguments.of(
                        new SensorValue(id, time, 9.0),
                        new SensorConfiguration(id, code, "Indoor", TEMPERATURE, "", "", 10, 30, 2),
                        ALARM
                ),
                Arguments.of(
                        new SensorValue(id, time, 8.0),
                        new SensorConfiguration(id, code, "Indoor", TEMPERATURE, "", "", 10, 30, 2),
                        ALARM
                ),
                Arguments.of(
                        new SensorValue(id, time, 27.0),
                        new SensorConfiguration(id, code, "Indoor", TEMPERATURE, "", "", 10, 30, 2),
                        NORMAL
                ),
                Arguments.of(
                        new SensorValue(id, time, 28.0),
                        new SensorConfiguration(id, code, "Indoor", TEMPERATURE, "", "", 10, 30, 2),
                        WARNING
                ),
                Arguments.of(
                        new SensorValue(id, time, 29.0),
                        new SensorConfiguration(id, code, "Indoor", TEMPERATURE, "", "", 10, 30, 2),
                        WARNING
                ),
                Arguments.of(
                        new SensorValue(id, time, 30.0),
                        new SensorConfiguration(id, code, "Indoor", TEMPERATURE, "", "", 10, 30, 2),
                        ALARM
                ),
                Arguments.of(
                        new SensorValue(id, time, 31.0),
                        new SensorConfiguration(id, code, "Indoor", TEMPERATURE, "", "", 10, 30, 2),
                        ALARM
                ),
                Arguments.of(
                        new SensorValue(id, time, 230.0),
                        new SensorConfiguration(id, code, "Temperatue", TEMPERATURE, "", "", 210, 240, 2),
                        NORMAL
                ),
                Arguments.of(
                        new SensorValue(id, time, 1),
                        new SensorConfiguration(id, code, "Close", Sensor.SensorType.ALARM, "", ""),
                        ALARM
                ),
                Arguments.of(
                        new SensorValue(id, time, 0),
                        new SensorConfiguration(id, code, "Close", Sensor.SensorType.ALARM, "", ""),
                        NORMAL
                ),
                Arguments.of(
                        new SensorValue(id, time, 0.6),
                        new SensorConfiguration(id, code, "Close", Sensor.SensorType.ALARM, "", ""),
                        ALARM
                ),
                Arguments.of(
                        new SensorValue(id, time, 0.0),
                        new SensorConfiguration(id, code, "OnOff", Sensor.SensorType.ALARM, "", ""),
                        NORMAL
                ),
                Arguments.of(
                        new SensorValue(id, time, 1),
                        new SensorConfiguration(id, code, "OnOff", ON_OFF, "", ""),
                        ALARM
                ),
                Arguments.of(
                        new SensorValue(id, time, 0),
                        new SensorConfiguration(id, code, "OnOff", ON_OFF, "", ""),
                        NORMAL
                ),
                Arguments.of(
                        new SensorValue(id, time, 0.6),
                        new SensorConfiguration(id, code, "OnOff", ON_OFF, "", ""),
                        ALARM
                ),
                Arguments.of(
                        new SensorValue(id, time, 0.0),
                        new SensorConfiguration(id, code, "OnOff", ON_OFF, "", ""),
                        NORMAL
                )
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void calculateStateTest(SensorValue value,
                                   SensorConfig.SensorConfiguration sensorConfiguration,
                                   SensorValue.SensorState state) {

        assertEquals(SensorLastDataSaver.calcSate(value, new SensorConfig(singletonList(sensorConfiguration))), state);
    }
}
