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
    $.post("http://localhost:8080/sensors",
        function (sensorsJsonData) {
            console.log("sensors processing start");
            var sensors = sensorsJsonData.sensors;
            sensorMap = convertSensorsJsonToMap(sensors);
            console.log("sensors loaded: " + sensorMap.length);
            layoutSensors(sensors);
            loadValuesFromServer();
        }, 'json').done(function () {
        console.log("sensors loaded");
    }).fail(function (jqXHR, textStatus) {
        showAlert("Sensors loading failed!", jqXHR, textStatus);
    }).always(function () {
        console.log("sensors loading complete");
    });
}

function loadValuesFromServer() {
    $.post("http://localhost:8080/sensor_values",
        function (valuesJsonData) {
            console.log('sensor values processing start');
            renderSensorValues(sensorMap, valuesJsonData.values);
        },
        'json').done(function () {
        console.log("sensor values loaded");

        showCurrentDateTimeInTitle();
    }).fail(function (jqXHR, textStatus) {
        showAlert("Sensor values loading failed!", jqXHR, textStatus);
    }).always(function () {
        console.log("sensor values loading complete");
    });
}

function showCurrentDateTimeInTitle() {
    var now = new Date();
    var dateOut = $.format.date(now, 'yyyy/MM/dd HH:mm:ss');
    //show date time
    $('.refreshed').text("Loaded: " + dateOut);
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
    for ( const key in sensorsMap) {
        const sensor=sensorsMap[key]
        const sensorId=sensor.id
        sensorValue = valuesJsonData[sensorId];
        sensorElementId = '#sensor_element' + sensorId;
        renderSensor(sensorElementId, sensor, sensorValue??{sensorId: sensorId, value: 0.0, state: 'UNDEFINED'},);
        $(sensorElementId).show();
    }
}

function renderSensor(sensorElementId, sensor, value) {
    if (sensor.type === 'TEMPERATURE') {
        temperatureSensorRenderer(sensorElementId, value);
    } else if (sensor.type === 'VOLTAGE') {
        voltageSensorRenderer(sensorElementId, value);
    } else if (sensor.type === 'ON_OFF') {
        onOffSensorRenderer(sensorElementId, value);
    } else if (sensor.type === 'ALARM') {
        alertSensorRenderer(sensorElementId, value);
    }
}

function temperatureSensorRenderer(sensorElementId, sensorValue) {
    var resValue = sensorValue.state !='UNDEFINED'? sensorValue.value.toFixed(1) + '\xB0':'--\xB0'
    $(sensorElementId + ' .sensor_item_body .sensor_value').text(resValue)
    var sensorBody = $(sensorElementId + ' .sensor_item_body')
    const background = sensorBackgroundCalc(sensorValue)
    sensorBody.css('background', background)

    var gradient = $(sensorElementId + ' .sensor_item_body .sensor_gradient')
    textColor = calcGradientValueAndColor(sensorValue.gradient)
    gradient.text(textColor.text)
    gradient.css('color', textColor.color)
}

function calcGradientValueAndColor(gradient) {
    var text;
    var color;
    if (typeof (gradient) != "undefined" && gradient != null) {//gradient is set
        text = (gradient > 0 ? '+' : '') + gradient.toFixed(1);
        color = gradient > 0 ? 'red' : 'blue';
    } else {//if not defined
        text = '...';//estimated
        color = 'blue';
    }
    return {text: text, color: color};
}

var STATE_BACKGROUND = (function () {
    var private = {
        'NORMAL': 'linear-gradient(to bottom, lightgreen, greenyellow)',
        'ALERT': 'linear-gradient(to bottom, orange, red)',
        'WARNING': 'linear-gradient(to bottom, yellow, orange)',
        'OFF': 'linear-gradient(to bottom, white, lightgrey)',
        'ON': 'linear-gradient(to bottom, lightblue, lightskyblue )',
        'ALARM_ON': 'darkred',
        'ALARM_OFF': 'lightgreen',
        'UNDEFINED': 'gray'
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

function voltageSensorRenderer(sensorElementId, value) {
    var resValue = value.value + ' V';
    $(sensorElementId + ' .sensor_item_body .sensor_value').text(resValue);
    sensorBody = $(sensorElementId + ' .sensor_item_body');
    sensorBody.css('background', sensorBackgroundCalc(value));
}

function onOffSensorRenderer(sensorElementId, sensor) {
    r = onOffSensorBackgroundCalc(sensor);

    let sensorElement = $(sensorElementId + ' .sensor_item_body .sensor_value');
    sensorElement.text(r.status);
    sensorElement.click(function (ev) {

        let value = ev.target.textContent;
        console.log('on start: ' + value);

        let onOffUrl = "";
        if (value === "Off") {
            onOffUrl = 'http://localhost:8080/start_process';
        } else if (value === "On") {
            onOffUrl = 'http://localhost:8080/stop_process';
        }

        $.ajax({
            type: 'POST',
            dataType: 'json',
            data: {sensorId: sensor.sensorId},
            url: onOffUrl,
            beforeSend: function () {
                $('body').append('<div id="requestOverlay" class="request-overlay"></div>'); /*Create overlay on demand*/
                $("#requestOverlay").show();/*Show overlay*/
                $("#loader").show();
            },
            success: function (data) {
                console.log('on finish: ' + data.value);
                sensor.value = data.value;
                r = onOffSensorBackgroundCalc(sensor);
                sensorElement.text(r.status);
                sensorBody = $(sensorElementId + ' .sensor_item_body');
                sensorBody.css('background', r.background);
            },
            error: function (jqXhr, textStatus, errorThrown) {
                alert("Error try again later: " + textStatus)
            },
            complete: function () {
                $("#requestOverlay").remove();/*Remove overlay*/
                $("#loader").hide();
            }
        });
    })
    sensorBody = $(sensorElementId + ' .sensor_item_body');
    sensorBody.css('background', r.background);
    sensorBody.css('border-radius', 57.5);
}

function onOffSensorBackgroundCalc(value) {
    var statusText;
    var background = STATE_BACKGROUND.get('UNDEFINED');
    if (Number(value.value) === 0.0) {
        statusText = 'Off';
        background = STATE_BACKGROUND.get('OFF');
    } else if (Number(value.value) === 1.0) {
        statusText = 'On';
        background = STATE_BACKGROUND.get('ON');
    }
    return {
        status: statusText,
        background: background
    };
}

function alertSensorRenderer(sensorElementId, value) {
    result = alertSensorStatusBackgroundCalc(value);

    sensorValue = $(sensorElementId + ' .sensor_item_body .sensor_value');
    sensorValue.text(result.status);

    sensorValue.css('top', '50px');
    sensorValue.css('left', '-47px');
    sensorValue.css('position', 'relative');
    sensorValue.css('width', '93px');
    sensorValue.css('height', '93px');
    sensorValue.css('margin', '0px');

    sensorBody = $(sensorElementId + ' .sensor_item_body');
    h = sensorBody.css('height');
    sensorBody.css('border-radius', 0);

    //See https://css-tricks.com/examples/ShapesOfCSS/
    sensorBody.css('margin', '0 auto 0px auto');
    sensorBody.css('width', 0);
    sensorBody.css('height', 0);
    sensorBody.css('line-height', 0);
    sensorBody.css('border-style', 'inset');
    sensorBody.css('border-width', '0 63px 115px 63px');
    sensorBody.css('border-color', 'transparent transparent ' + result.background + ' transparent');
    sensorBody.css('transform', 'rotate(360deg)');
    sensorBody.css('-ms-transform', 'rotate(360deg)');
    sensorBody.css('-moz-transform', 'rotate(360deg)');
    sensorBody.css('-webkit-transform:', 'rotate(360deg)');
    sensorBody.css('-o-transform', 'rotate(360deg)');
}

function alertSensorStatusBackgroundCalc(value) {
    var statusText;
    var background = STATE_BACKGROUND.get('UNDEFINED');
    if (value.state === 'NORMAL') {
        statusText = 'Ok';
        background = STATE_BACKGROUND.get('ALARM_OFF');
    } else if (value.state === 'ALERT') {
        statusText = 'Alarm';
        background = STATE_BACKGROUND.get('ALARM_ON');
    }
    return {
        status: statusText,
        background: background
    };
}


