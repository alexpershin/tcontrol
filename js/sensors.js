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
            appendText(sensorElementId, sensor, value);
            appendState(sensorElementId, sensor, value);
        }
    });
}

function appendText(sensorElementId, sensor, value) {
    sensorElement = $(sensorElementId);

    var resValue = '?';
    if (sensor.type === 'TEMPERATURE') {
        resValue = value.value + '\xB0';
    } else if (sensor.type === 'VOLTAGE') {
        resValue = value.value + ' V';
    } else if (sensor.type === 'ON_OFF') {
        if (value.value === 0) {
            resValue = 'On';
        } else if (value.value === 1) {
            resValue = 'Off';
        }
    }
    $(sensorElementId + ' .sensor_item_body .sensor_value').text(resValue);
}


function appendState(sensorElementId, sensor, value) {
    sensorBody = $(sensorElementId + ' .sensor_item_body');
    var background;
    if (sensor.type === 'TEMPERATURE') {
        background = "greenyellow";
    } else if (sensor.type === 'VOLTAGE') {
        background = "greenyellow";
    } else if (sensor.type === 'ON_OFF') {
        if (value.value === 0) {
            background = "blue";
        } else if (value.value === 1) {
            background = "white";
            ;
        }
    }
    sensorBody.css('background-color', background);
}
