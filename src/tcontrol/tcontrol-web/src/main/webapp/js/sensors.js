function renderSensorsOnLoad() {
    loadStubDataOnLoad();
    loadDataFromServer();
}

function loadStubDataOnLoad() {
//Stub data and rendering test
    var sensorsJsonData = [
        {name: 'Indoor', id: 1, type: 'TEMPERATURE',
            lowThreshold: 10, highThreshold: 31, thresholdLag: 2},
        {name: 'Outdoor', id: 2, type: 'TEMPERATURE',
            lowThreshold: -20, highThreshold: 32, thresholdLag: 2},
        {name: 'Cellar', id: 3, type: 'TEMPERATURE',
            lowThreshold: 0, highThreshold: 20, thresholdLag: 3},
        {name: 'Loft', id: 4, type: 'TEMPERATURE',
            lowThreshold: -20, highThreshold: 40, thresholdLag: 2},
        {name: 'Bath Room', id: 5, type: 'TEMPERATURE',
            lowThreshold: 5, highThreshold: 34, thresholdLag: 2},
        {name: 'Garage', id: 6, type: 'TEMPERATURE',
            lowThreshold: -40, highThreshold: 60, thresholdLag: 6},
        {name: 'Power', id: 8, type: 'VOLTAGE',
            lowThreshold: 190, highThreshold: 250, thresholdLag: 10},
        {name: 'Heating', id: 7, type: 'ON_OFF'},
    ];
    var valuesJsonData = [
        {sensorId: 1, value: 25.5},
        {sensorId: 2, value: -21.6},
        {sensorId: 3, value: +2.1},
        {sensorId: 4, value: -11.6},
        {sensorId: 5, value: 23.8},
        {sensorId: 6, value: -7.4},
        {sensorId: 7, value: 1},
        {sensorId: 8, value: 241},
    ];
    sensorMap = convertSensorsJsonToMap(sensorsJsonData);
    layoutSensors(sensorsJsonData);
    renderSensorValues(sensorMap, valuesJsonData);
}

function loadDataFromServer() {
    $.getJSON("/tcontrol-web/webresources/sensors", {format: "json"},
    function(result)
    {
        console.log("sensors processing start");
        $(result).each(function(key, value) {
            console.log(value.id + "," + value.type + "," + value.name);
        });
    }).done(function() {
        console.log("sensors loaded");
    }).fail(function() {
        var message = "sensors loading error";
        console.log(message);
        alert(message);
    }).always(function() {
        console.log("sensors loading complete");
    });
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
    thresholdRenderer(sensorBody, sensor, value);
}

var STATE_BACKGROUND = (function() {
    var private = {
        'NORMAL': 'linear-gradient(to bottom, lightgreen, greenyellow)',
        'ALERT': 'linear-gradient(to bottom, orange, red)',
        'WARNING': 'linear-gradient(to bottom, yellow, orange)',
        'OFF': 'linear-gradient(to bottom, white, lightgrey)',
        'ON': 'linear-gradient(to bottom, lightblue, lightskyblue )',
    };
    return {
        get: function(name) {
            return private[name];
        }
    };
})();
function thresholdRenderer(sensorBody, sensor, value) {
    var background = STATE_BACKGROUND.get('NORMAL');
    //low thressHold
    if (value.value <= sensor.lowThreshold) {
        background = STATE_BACKGROUND.get('ALERT');
    } else if (value.value <= sensor.lowThreshold + sensor.thresholdLag &&
            value.value > sensor.lowThreshold) {
        background = STATE_BACKGROUND.get('WARNING');
    }

    //high thressHold
    if (value.value >= sensor.highThreshold) {
        background = STATE_BACKGROUND.get('ALERT');
    } else if (value.value > sensor.highThreshold - sensor.thresholdLag &&
            value.value < sensor.highThreshold) {
        background = STATE_BACKGROUND.get('WARNING');
    }

    sensorBody.css('background', background);
}

function voltageSensorRenderer(sensorElementId, sensor, value) {
    var resValue = value.value + ' V';
    $(sensorElementId + ' .sensor_item_body .sensor_value').text(resValue);
    sensorBody = $(sensorElementId + ' .sensor_item_body');
    thresholdRenderer(sensorBody, sensor, value);
}

function onOffSensorRenderer(sensorElementId, sensor, value) {
    var resValue;
    if (value.value === 0) {
        resValue = 'Off';
    } else if (value.value === 1) {
        resValue = 'On';
    }
    $(sensorElementId + ' .sensor_item_body .sensor_value').text(resValue);
    var background;
    if (value.value === 0) {
        background = STATE_BACKGROUND.get('OFF');
    } else if (value.value === 1) {
        background = STATE_BACKGROUND.get('ON');
    }
    sensorBody = $(sensorElementId + ' .sensor_item_body');
    sensorBody.css('background', background);
    h = sensorBody.css('height');
    sensorBody.css('border-radius', 57.5);
}
