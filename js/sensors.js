function renderSensorsOnLoad() {
    //loadStubDataOnLoad();
    loadDataFromServer();
    setupDialogs()
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

function showGlobalLoader(){
     $('body').append('<div id="requestOverlay" class="request-overlay"></div>'); /*Create overlay on demand*/
     $("#requestOverlay").show();/*Show overlay*/
     $("#loader").show();
}

function hideGlobalLoader(){
    $("#requestOverlay").remove();/*Remove overlay*/
    $("#loader").hide();
}

function loadDataFromServer() {
   $.ajax({
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        url: window.location.protocol+"//"+window.location.host+"/tcontrol/api/sensors",
        beforeSend: function () {
            showGlobalLoader()
        },
        success: function (sensorsJsonData) {
            console.log("sensors processing start");
            var sensors = sensorsJsonData.sensors;
            sensorMap = convertSensorsJsonToMap(sensors);
            console.log("sensors loaded: " + sensorMap.length);
            layoutSensors(sensors);
            loadValuesFromServer();

        },
        error: function (jqXhr, textStatus, errorThrown) {
            hideGlobalLoader()
            showAlert("Sensors loading failed!", jqXHR, textStatus);
        },
        complete: function () {
            hideGlobalLoader()
            console.log("sensors loading complete");
        }
    });
}

function loadValuesFromServer() {
    $.post(window.location.protocol+"//"+window.location.host+":/tcontrol/api/sensor_values",
        function (valuesJsonData) {
            console.log('sensor values processing start');
            valuesMap = convertValuesJsonToMap(valuesJsonData.values)
            renderSensorValues(sensorMap, valuesMap);
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

function convertValuesJsonToMap(valuesJsonData) {
    var result = {};//new Map; //Waiting release of Draft ECMA-262 6th Edition
    $(valuesJsonData).each(function (key, value) {
        result[value.sensorId] = value;
    });
    return result;
}

function layoutSensors(sensorsJsonData) {
    $(sensorsJsonData).each(function (key, value) {
        clone = $('#sensor_element').clone();
        clone.appendTo('.sensor_items');
        sensorElementId = clone.attr('id') + value.id;
        clone.attr("id", sensorElementId);
        sensorTitle = $('#' + sensorElementId + ' #sensor_title');
        sensorTitle.text(value.name);
        //clone.show();
    });
}

function renderSensorValues(sensorsMap, valuesMap) {
    for ( const key in sensorsMap) {
        const sensor=sensorsMap[key]
        const sensorId=sensor.id
        sensorValue = valuesMap[sensorId];
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

    const sensorElement = $(sensorElementId + ' .sensor_item_body .sensor_value')
    const sensorBody = $(sensorElementId + ' .sensor_item_body')
    sensorElement.text(r.status)

    sensorBody.click(function (ev) {

        const currentTemperatureURL =
            window.location.protocol
            + "//" + window.location.host
            + ":/tcontrol/api/thermostat_current_temperature?sensorId="
            + sensor.sensorId

        $.ajax({
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                url: currentTemperatureURL,
                beforeSend: function () {
                    showSensorLoader(sensorElementId)
                },
                success: function (value) {
                     startHeatingDialog(sensorElementId, sensor, value)
                },
                error: function (jqXhr, textStatus, errorThrown) {
                    hideSensorLoader(sensorElementId)
                    alert("Error try again later: " + textStatus)
                },
                complete: function () {
                    hideSensorLoader(sensorElementId)
                }
            });
    })

    sensorBody.css('background', r.background);
    sensorBody.css('border-radius', 57.5);
}

function startHeatingDialog(sensorElementId, sensor, currentTemperature){
    const startHeatingDialog = document.getElementById('start-heating');

    var applyBtn = document.getElementById('start-heating-apply-btn');
    //remove previous listeners
    const clone = applyBtn.cloneNode(true);
    applyBtn.parentNode.replaceChild(clone, applyBtn);

    applyBtn = document.getElementById('start-heating-apply-btn');
    const titleComponent = document.getElementById('start-heating-dialog-title');
    titleComponent.textContent = $(sensorElementId + ' #sensor_title').text()
    const startHeatingDialogInput = document.getElementById("start-heating-input")

    applyBtn.addEventListener('click', () => {
         if(validateSensorTemperature(startHeatingDialogInput)){
               startHeating(sensorElementId, sensor)
         }
    });
    console.log('current temperature: ' + currentTemperature);

    startHeatingDialogInput.value = currentTemperature
    startHeatingDialog.style.visibility='visible'
    startHeatingDialog.showModal()
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

function setupDialogs(){
    const startHeatingDialog = document.getElementById('start-heating');
    const closeBtn = document.getElementById('start-heating-close-btn');
    closeBtn.addEventListener('click', () => {
        startHeatingDialog.style.visibility='hidden'
        startHeatingDialog.close()
    });
}

function showSensorLoader(sensorElementId){
   $(sensorElementId + ' .sensor_item_body .sensor-loader').css('visibility', 'visible')
   $('.sensor_item').find('.sensor_item_body').css('pointer-events', 'none')
}

function hideSensorLoader(sensorElementId){
   $(sensorElementId + ' .sensor_item_body .sensor-loader').css('visibility', 'hidden')
   $('.sensor_item').find('.sensor_item_body').css('pointer-events', 'all')
}

function startHeating(sensorElementId, sensor){
    const sensorElement = $(sensorElementId + ' .sensor_item_body .sensor_value')
    const startHeatingDialog = document.getElementById('start-heating');
    const popUpElement = document.getElementById("pop-up");
    popUpElement.show();

    const startHeatingDialogInput = document.getElementById("start-heating-input");

    let onOffUrl = window.location.protocol
        +'//'+window.location.host
        +'/tcontrol/api/start_process';

   var onOffRequest = {
        sensorId: sensor.sensorId,
        newValue: startHeatingDialogInput.value
    }

    startHeatingDialog.close();

    $.ajax({
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify(onOffRequest),
        url: onOffUrl,
        beforeSend: function () {
            showSensorLoader(sensorElementId)
        },
        success: function (data) {
            console.log('on finish: ' + data.value);
            sensor.value = data.value;
            sensorElement.text(r.status);
            const sensorBody = $(sensorElementId + ' .sensor_item_body')
            sensorBody.css('background', onOffSensorBackgroundCalc(sensor));
        },
        error: function (jqXhr, textStatus, errorThrown) {
            hideSensorLoader(sensorElementId)
            popUpElement.close();
            startHeatingDialog.close();
            alert("Error try again later: " + textStatus)
        },
        complete: function () {
            hideSensorLoader(sensorElementId)
            popUpElement.close();
            startHeatingDialog.close();
        }
    });
}

function validateSensorTemperature(input){
    if(input.value<4 || input.value>30){
        alert("Тепература должна быть в диапазоне: [4;30]")
        return false
    }
    return true
}


