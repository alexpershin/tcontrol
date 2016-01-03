function renderSensorsOnLoad() {
    //loadStubDataOnLoad();
    loadDataFromServer();
}

function loadStubDataOnLoad() {
//Stub data and rendering test
    var sensorsJsonData = [
        {name: 'Indoor', id: 1, type: 'TEMPERATURE'},
        {name: 'Outdoor', id: 2, type: 'TEMPERATURE'},
        {name: 'Cellar', id: 3, type: 'TEMPERATURE'},
        {name: 'Loft', id: 4, type: 'TEMPERATURE'},
        {name: 'Bath Room', id: 5, type: 'TEMPERATURE'},
        {name: 'Garage', id: 6, type: 'TEMPERATURE'},
        {name: 'Power', id: 8, type: 'VOLTAGE'},
        {name: 'Heating', id: 7, type: 'ON_OFF'},
    ];
    var valuesJsonData = [
        {sensorId: 1, value: 25.5, state: 'NORMAL'},
        {sensorId: 2, value: -21.6, state: 'ALERT'},
        {sensorId: 3, value: +2.1, state: 'WARNING'},
        {sensorId: 4, value: -11.6, state: 'NORMAL'},
        {sensorId: 5, value: 23.8, state: 'NORMAL'},
        {sensorId: 6, value: -7.4, state: 'ALERT'},
        {sensorId: 7, value: 1, state: 'ON'},
        {sensorId: 8, value: 241, state: 'WARNING'},
    ];
    sensorMap = convertSensorsJsonToMap(sensorsJsonData);
    layoutSensors(sensorsJsonData);
    renderSensorValues(sensorMap, valuesJsonData);
}

function loadDataFromServer() {
    $.getJSON("/tcontrol-web/webresources/sensor/sensors", {format: "json"},
            function (sensorsJsonData)
            {
                console.log("sensors processing start");
                var sensors = sensorsJsonData.sensors;
                sensorMap = convertSensorsJsonToMap(sensors);
                console.log("sensors loaded: " + sensorMap.length);
                layoutSensors(sensors);
                loadValuesFromServer();
            }).done(function () {
        console.log("sensors loaded");
    }).fail(function (jqXHR, textStatus) {
        showAlert("Sensors loading failed!", jqXHR, textStatus);
    }).always(function () {
        console.log("sensors loading complete");
    });
}

function loadValuesFromServer() {
    $.getJSON("/tcontrol-web/webresources/sensor/sensor_values",
            {format: "json"},
            function (valuesJsonData)
            {
                console.log('sensor values processing start');
                renderSensorValues(sensorMap, valuesJsonData.values);
            }).done(function () {
        console.log("sensor values loaded");
    }).fail(function (jqXHR, textStatus) {
        showAlert("Sensor values loading failed!", jqXHR, textStatus);
    }).always(function () {
        console.log("sensor values loading complete");
    });
}

function showAlert(title, jqXHR, textStatus) {
    var message = jqXHR.responseText;
    console.log("error: " + textStatus);
    console.log("incoming Text: " + message);
    console.log(message);
    alert(title + "\n" + message);
}

function convertSensorsJsonToMap(sensorsJsonData) {
    var result = {};//new Map; //Waiting release of Draft ECMA-262 6th Edition
    $(sensorsJsonData).each(function (key, value) {
        result[value.id] = value;
    });
    return result;
}

function layoutSensors(sensorsJsonData) {
    $(sensorsJsonData).each(function (key, value) {
        clone = $('#sensor_element').clone();
        clone.appendTo('.sensor_items');
        sensorElementId = clone.attr('id') + value.id;
        clone.attr("id", sensorElementId);
        sensorTitle =
                $('#' + sensorElementId + ' #sensor_title');
        sensorTitle.text(value.name);
        //clone.show();
    });
}

function renderSensorValues(sensorsMap, valuesJsonData) {
    $(valuesJsonData).each(function (key, value) {
        if (sensorsMap[value.sensorId] !== null) {
            sensor = sensorsMap[value.sensorId];
            sensorElementId = '#sensor_element' + value.sensorId;
            renderSensor(sensorElementId, sensor, value);
            $(sensorElementId).show();
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
    } else if (sensor.type === 'ALARM') {
        alertSensorRenderer(sensorElementId, sensor, value);
    }
}

function temperatureSensorRenderer(sensorElementId, sensor, value) {
    var resValue = value.value + '\xB0'
    $(sensorElementId + ' .sensor_item_body .sensor_value').text(resValue);
    var sensorBody = $(sensorElementId + ' .sensor_item_body');
    sensorBody.css('background', sensorBackgroundCalc(value));
}

var STATE_BACKGROUND = (function () {
    var private = {
        'NORMAL': 'linear-gradient(to bottom, lightgreen, greenyellow)',
        'ALERT': 'linear-gradient(to bottom, orange, red)',
        'WARNING': 'linear-gradient(to bottom, yellow, orange)',
        'OFF': 'linear-gradient(to bottom, white, lightgrey)',
        'ON': 'linear-gradient(to bottom, lightblue, lightskyblue )',
        'ALARM_ON': 'darkred',
        'ALARM_OFF': 'olivedrab',
    };
    return {
        get: function (name) {
            return private[name];
        }
    };
})();

function sensorBackgroundCalc(value) {
    return background = STATE_BACKGROUND.get(value.state);
}

function voltageSensorRenderer(sensorElementId, sensor, value) {
    var resValue = value.value + ' V';
    $(sensorElementId + ' .sensor_item_body .sensor_value').text(resValue);
    sensorBody = $(sensorElementId + ' .sensor_item_body');
    sensorBody.css('background', sensorBackgroundCalc(value));
}

function onOffSensorRenderer(sensorElementId, sensor, value) {
    var resValue;
    var background;
    if (value.value == 0) {
        resValue = 'Off';
        background = STATE_BACKGROUND.get('OFF');
    } else if (value.value == 1) {
        resValue = 'On';
        background = STATE_BACKGROUND.get('ON');
    }
    $(sensorElementId + ' .sensor_item_body .sensor_value').text(resValue);

    sensorBody = $(sensorElementId + ' .sensor_item_body');
    sensorBody.css('background', background);
    h = sensorBody.css('height');
    sensorBody.css('border-radius', 57.5);
}

function alertSensorRenderer(sensorElementId, sensor, value) {
    var resValue;
    var background;
    if (value.state == 'NORMAL') {
        resValue = 'Ok';
        background = STATE_BACKGROUND.get('ALARM_OFF');
    } else if (value.state == 'ALERT') {
        resValue = 'Alarm';
        background = STATE_BACKGROUND.get('ALARM_ON');
    }
    sensorValue = $(sensorElementId + ' .sensor_item_body .sensor_value');
    sensorValue.text(resValue);
    sensorValue.css('margin', '57px 0 0 -40px');

    sensorBody = $(sensorElementId + ' .sensor_item_body');
    //sensorBody.css('background', background);
    h = sensorBody.css('height');
    sensorBody.css('border-radius', 0);

    //See https://css-tricks.com/examples/ShapesOfCSS/
    sensorBody.css('width', 0);
    sensorBody.css('height', 0);
    sensorBody.css('border-left', '63px solid transparent');
    sensorBody.css('border-right', '63px solid transparent');
    sensorBody.css('border-bottom', '115px solid ' + background);
}
