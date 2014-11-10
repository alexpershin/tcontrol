function convertSensorsJsonToMap(sensorsJsonData) {
    result = new Map();
    $(sensorsJsonData).each(function(key, value) {
        result.set(value.id, value);
    });
    return result;
}

function layoutSensors(sensorsJsonData) {
    $(sensorsJsonData).each(function(key, value) {
        clone = $('#sensor_element').clone();
        clone.appendTo('.sensor_items');

        sensorElementId = clone.attr('id') + value.id;
        clone.attr("id", sensorElementId);

        sensorTitle =
                $('#' + sensorElementId + ' #sensor_title');
        sensorTitle.text(value.name);

        clone.show();
    });
}

function appendValues(sensorsMap, valuesJsonData) {
    $(valuesJsonData).each(function(key, value) {
        if (sensorsMap.has(value.sensorId)) {
            sensor = sensorsMap.get(value.sensorId);
            sensorElementId = '#sensor_element' + value.sensorId;
            renderSensor(sensorElementId, sensor, value);
        }
    });
}

function renderSensor(sensorElementId, sensor, value) {
    if (sensor.type === 'TEMPERATURE') {
        temperatureSensorRenderer(sensorElementId,sensor,value);
    } else if (sensor.type === 'VOLTAGE') {
        voltageSensorRenderer(sensorElementId,sensor,value);
    } else if (sensor.type === 'ON_OFF') {
        onOffSensorRenderer(sensorElementId,sensor,value);
    }
}

function temperatureSensorRenderer(sensorElementId, sensor, value) {
    var resValue = value.value + '\xB0'
    $(sensorElementId + ' .sensor_item_body .sensor_value').text(resValue);

    var sensorBody = $(sensorElementId + ' .sensor_item_body');
    var background = "greenyellow";
    sensorBody.css('background-color', background);
}

function voltageSensorRenderer(sensorElementId, sensor, value) {
    var resValue = value.value + ' V';
    $(sensorElementId + ' .sensor_item_body .sensor_value').text(resValue);

    var background = "greenyellow";
    sensorBody = $(sensorElementId + ' .sensor_item_body');
    sensorBody.css('background-color', background);
}

function onOffSensorRenderer(sensorElementId, sensor, value) {
    var resValue;
    if (value.value === 0) {
        resValue = 'On';
    } else if (value.value === 1) {
        resValue = 'Off';
    }
    $(sensorElementId + ' .sensor_item_body .sensor_value').text(resValue);

    var background;
    if (value.value === 0) {
        background = "blue";
    } else if (value.value === 1) {
        background = "white";
        ;
    }
    sensorBody = $(sensorElementId + ' .sensor_item_body');
    sensorBody.css('background-color', background);
}
