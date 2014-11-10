function renderSensorsOnLoad() {
    //Stub data and rendering test
    var sensorsJsonData = [
        {name: 'Indor', id: 1, type: 'TEMPERATURE'},
        {name: 'Outdor', id: 2, type: 'TEMPERATURE'},
        {name: 'Cellar', id: 3, type: 'TEMPERATURE'},
        {name: 'Loft', id: 4, type: 'TEMPERATURE'},
        {name: 'Bath Room', id: 5, type: 'TEMPERATURE'},
        {name: 'Garage', id: 6, type: 'TEMPERATURE'},
        {name: 'Heating', id: 7, type: 'ON_OFF'},
        {name: 'Power', id: 8, type: 'VOLTAGE'},
    ];
    var valuesJsonData = [
        {sensorId: 1, value: 25.5},
        {sensorId: 2, value: -12.6},
        {sensorId: 3, value: +10.1},
        {sensorId: 4, value: -11.6},
        {sensorId: 5, value: 23.8},
        {sensorId: 6, value: -7.4},
        {sensorId: 7, value: 1},
        {sensorId: 8, value: 231},
    ];
    sensorMap = convertSensorsJsonToMap(sensorsJsonData);
    layoutSensors(sensorsJsonData);
    renderSensorValues(sensorMap, valuesJsonData);
}

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

function renderSensorValues(sensorsMap, valuesJsonData) {
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
        temperatureSensorRenderer(sensorElementId, sensor, value);
    } else if (sensor.type === 'VOLTAGE') {
        voltageSensorRenderer(sensorElementId, sensor, value);
    } else if (sensor.type === 'ON_OFF') {
        onOffSensorRenderer(sensorElementId, sensor, value);
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
