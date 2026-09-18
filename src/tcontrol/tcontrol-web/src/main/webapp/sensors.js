function renderSensorsOnLoad() {
    //loadStubDataOnLoad();
    loadDataFromServer();
    setupDialogs()
}

window.renderSensorsOnLoad = renderSensorsOnLoad;

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

function checkIfTokenSet(){
      const token = localStorage.getItem('auth_token')
      if (!token) {
            redirectToLogin()
      }
}

function redirectToLogin(){
    // 1. Capture the current page path and query parameters
    const currentPath = window.location.pathname + window.location.search

    // 2. Encode the path to ensure it safely passes through the URL
    const redirectParam = encodeURIComponent(currentPath)

    // 3. Redirect to login page with the return destination attached
    window.location.href = `/tcontrol/login-dialog/login-dialog.html?redirectTo=${redirectParam}`
}

function headers(){
    return {
           "Content-Type": "application/json",
           "Accept": "application/json",
           "Authorization": "Bearer " + localStorage.getItem('auth_token')
    }
}

function loadDataFromServer() {

   checkIfTokenSet()

   $.ajax({
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        url: window.location.protocol+"//"+window.location.host+"/tcontrol/api/sensors",
        method: "POST",
        headers: headers(),
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
        error: function (jqXHR, textStatus, errorThrown) {
            requestErrorProcessing(jqXHR, textStatus, errorThrown, "Sensors loading failed!")
        },
        complete: function () {
            hideGlobalLoader()
            console.log("sensors loading complete");
        }
    });
}

function requestErrorProcessing(jqXHR, textStatus, errorThrown, logMessage) {
    hideGlobalLoader()
    console.log(jqXHR.status)
    if(jqXHR.status == 403){
        const userConfirmed = confirm("Authorization error! Login?")
        if (userConfirmed) {
            redirectToLogin()
        } else {
            console.log("Action canceled by user.");
        }
    } else {
        showAlert(logMessage, jqXHR, textStatus)
    }
}

function loadValuesFromServer() {
    checkIfTokenSet()

     $.ajax({
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            url: window.location.protocol+"//"+window.location.host+":/tcontrol/api/sensor_values",
            method: "POST",
            headers: headers(),
            beforeSend: function () {
                showGlobalLoader()
            },
            success: function (valuesJsonData) {
                hideGlobalLoader()
                console.log('sensor values processing start');
                valuesMap = convertValuesJsonToMap(valuesJsonData.values)
                renderSensorValues(sensorMap, valuesMap);
            },
            error: function (jqXHR, textStatus, errorThrown) {
               requestErrorProcessing(jqXHR, textStatus, errorThrown, "Sensor values loading failed!")
            },
            complete: function () {
                 console.log("sensor values loading complete");
            }
        });
}

function showCurrentDateTimeInTitle() {
    var now = new Date();
    const timeFormatOptions = {
            hour12: false, // Set to false to use 24-hour format
            hour: '2-digit',
            minute: '2-digit'
        };
    var dateOut = now.toLocaleDateString() + " " + now.toLocaleTimeString('en-US', timeFormatOptions)
    //show date time
    $('.refreshed').text("Loaded: " + dateOut)
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
    });
}

function renderSensorValues(sensorsMap, valuesMap) {
    for ( const key in sensorsMap) {
        const sensor=sensorsMap[key]
        const sensorId = sensor.id
        sensorValue = valuesMap[sensorId];
        sensorElementId = '#sensor_element' + sensorId;
        renderSensor(
            sensorElementId,
            sensor,
            sensorValue ? sensorValue : {sensorId: sensorId, value: undefined, state: 'UNDEFINED'},
        );
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
    setSensorTime(sensorElementId, value.timestamp)
}

function setSensorTime(sensorElementId, timestamp){
    if (timestamp !== null){
           var tadeTime = timestampToTime(timestamp)
           const timeWithoutAmPm = tadeTime ? tadeTime: '--'

           $(sensorElementId +' .sensor_item_body .sensor_indicator_panel .sensor_time').text (timeWithoutAmPm)
    }
}

function timestampToTime(timestamp){
         const dateOpts = {
                       hour12: false, // Set to false to use 24-hour format
                       hour: '2-digit',
                       minute: '2-digit',
                       second: '2-digit' // Optional: include seconds if desired
                   };

         return timestamp ? new Date(timestamp).toLocaleTimeString('en-US', dateOpts) : null
}

function temperatureSensorRenderer(sensorElementId, sensorValue) {
    console.log('sensorValue: ' + sensorValue.value);
    var resValue = sensorValue.value == null ? '--\xB0' : sensorValue.value.toFixed(1) + '\xB0'
    $(sensorElementId + ' .sensor_item_body .sensor_value').text(resValue)
    var sensorBody = $(sensorElementId + ' .sensor_item_body')
    const background = sensorBackgroundCalc(sensorValue)
    sensorBody.css('background', background)

    var gradient = $(sensorElementId + ' .sensor_item_body .sensor_gradient')
    textColor = calcGradientValueAndColor(sensorValue.gradient)
    gradient.text(textColor.text)
    gradient.css('color', textColor.color)

    fillMinMaxValue(sensorElementId, sensorValue)

    setupPlot(sensorElementId, sensorValue, 'spline')
}

function fillMinMaxValue(sensorElementId, sensorValue) {

    var minValue = $(sensorElementId + ' .sensor_item_body .sensor_min_value')
    var maxValue = $(sensorElementId + ' .sensor_item_body .sensor_max_value')
    var minTime = $(sensorElementId + ' .sensor_item_body .sensor_min_time')
    var maxTime = $(sensorElementId + ' .sensor_item_body .sensor_max_time')
    minValue.text(sensorValue.minValue == null ? '--': sensorValue.minValue.toFixed(1))
    maxValue.text(sensorValue.maxValue == null ? '--': sensorValue.maxValue.toFixed(1))

    const timeFormatOptions = {
        hour12: false, // Set to false to use 24-hour format
        hour: '2-digit',
        minute: '2-digit'
    };

    minTime.text(sensorValue.minValueTimestamp ? new Date(sensorValue.minValueTimestamp).toLocaleTimeString('en-US', timeFormatOptions) : '--')
    maxTime.text(sensorValue.maxValueTimestamp? new Date(sensorValue.maxValueTimestamp).toLocaleTimeString('en-US', timeFormatOptions): '--')

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
    const states = {
        'NORMAL': 'linear-gradient(to bottom, lightgreen, greenyellow)',
        'ALERT': 'linear-gradient(to bottom, orange, red)',
        'WARNING': 'linear-gradient(to bottom, yellow, orange)',
        'OFF': 'linear-gradient(to bottom, white, lightgrey)',
        'ON': 'linear-gradient(to bottom, lightblue, lightskyblue )',
        'ALARM_ON': 'red',
        'ALARM_OFF': 'lightgreen',
        'ALARM_WARNING': 'yellow',
        'UNDEFINED': 'gray'
    };
    return {
        get: function (name) {
            return states[name];
        }
    };
})();

function sensorBackgroundCalc(value) {
    return background = STATE_BACKGROUND.get(value.state);
}

function voltageSensorRenderer(sensorElementId, value) {
    var resValue = value.value == null ? '--' : value.value + ' V';
    $(sensorElementId + ' .sensor_item_body .sensor_value').text(resValue);
    sensorBody = $(sensorElementId + ' .sensor_item_body');
    sensorBody.css('background', sensorBackgroundCalc(value));
    fillMinMaxValue(sensorElementId, value)
    setupPlot(sensorElementId, value, 'spline')
}

function onOffSensorRenderer(sensorElementId, sensorValue) {
    const backgroundCalcResult = onOffSensorBackgroundCalc(sensorValue);

    const sensorElement = $(sensorElementId + ' .sensor_item_body .sensor_value')
    const sensorBody = $(sensorElementId + ' .sensor_item_body')

    sensorElement.click(function (ev) {

        checkIfTokenSet()

        const currentTemperatureURL =
            window.location.protocol
            + "//" + window.location.host
            + ":/tcontrol/api/thermostat_current_temperature?sensorId="
            + sensorValue.sensorId

        $.ajax({
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                headers: headers(),
                url: currentTemperatureURL,
                beforeSend: function () {
                    showSensorLoader(sensorElementId)
                },
                success: function (currentTemperatures) {
                     startHeatingDialog(sensorElementId, sensorValue, currentTemperatures)
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    requestErrorProcessing(jqXHR, textStatus, errorThrown, "Error try again later: " + textStatus )
                },
                complete: function () {
                    hideSensorLoader(sensorElementId)
                }
            });
    })

    setupPlot(sensorElementId, sensorValue, 'hv')

    sensorBody.css('background', backgroundCalcResult.background);
    sensorElement.text(backgroundCalcResult.status)
    sensorBody.css('border-radius', 57.5);
}



function formatTimestamp(timestamp) {
  const date = timestamp.toISOString().split('T')[0]
  const time = timestamp.toTimeString().split(' ')[0]//.replace(/:/g, '-');
  return `${date} ${time}`
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
    } else{
        statusText = '--';
        background = STATE_BACKGROUND.get('UNDEFINED');
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

    sensorValue.css('top', '40px');
    sensorValue.css('left', '10px');
    sensorValue.css('position', 'relative');
    sensorValue.css('width', '93px');
    sensorValue.css('height', '93px');
    sensorValue.css('margin', '0px');

    sensorBody = $(sensorElementId + ' .sensor_item_body');
    sensorBody.css('border-color', 'transparent');

    drawTriangleWithBorder(sensorElementId, result.background)

     const sensorElement = $(sensorElementId + ' .sensor_item_body .sensor_value')

        sensorElement.click(function (ev) {

            const currentTemperatureURL =
                window.location.protocol
                + "//" + window.location.host
                + ":/tcontrol/api/alerts?sensorId="
                + value.sensorId

            $.ajax({
                    type: 'POST',
                    dataType: 'json',
                    contentType: 'application/json',
                    headers: headers(),
                    url: currentTemperatureURL,
                    beforeSend: function () {
                        showSensorLoader(sensorElementId)
                    },
                    success: function (currentAlerts) {
                         startAlertsDialog(sensorElementId, sensorValue, currentAlerts)
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        requestErrorProcessing(jqXHR, textStatus, errorThrown, "Error try again later: " + textStatus)
                    },
                    complete: function () {
                        hideSensorLoader(sensorElementId)
                    }
                });
        })

        setupPlot(sensorElementId, value, 'hv')

}

function drawTriangleWithBorder(sensorElementId, background) {
    const canvas = $(sensorElementId + ' .sensor_item_body .sensor_canvas').get(0)
    ctx = canvas.getContext("2d")
    sensorBody = $(sensorElementId + ' .sensor_item_body')
    h = 119*1.3
    w = 119*1.3

    // Define triangle points (an upward-pointing triangle)
    const p1X = w*0.95;
    const p1Y = h*0.05;
    const p2X = 2*w*0.95;
    const p2Y = h*0.95;
    const p3X = 0;
    const p3Y = h*0.95;

    // Draw the border (stroke)
    ctx.beginPath();
    ctx.moveTo(p1X, p1Y);
    ctx.lineTo(p2X, p2Y);
    ctx.lineTo(p3X, p3Y);
    ctx.closePath(); // Connects the last point to the first

    ctx.lineWidth = 5; // Border thickness
    ctx.strokeStyle = 'darkgrey'; // Border color
    ctx.stroke();

    // Fill the inside of the triangle (optional)
    ctx.fillStyle = background;
    ctx.fill();
}

function alertSensorStatusBackgroundCalc(value) {
    var statusText;
    var background = STATE_BACKGROUND.get('UNDEFINED');
    if (value.state === 'NORMAL') {
        statusText = 'Ok';
        background = STATE_BACKGROUND.get('ALARM_OFF');
    } else if (value.state === 'ALERT') {
        statusText = 'Crit';
        background = STATE_BACKGROUND.get('ALARM_ON');
    } else if (value.state === 'WARNING') {
        statusText = 'Warn';
        background = STATE_BACKGROUND.get('ALARM_WARNING');
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
        closeHeatingDialog()
    });
}

function showSensorLoader(sensorElementId){
   console.log('sensorElementId: ' + sensorElementId)
   $(sensorElementId + ' .sensor_item_body .sensor-loader').css('visibility', 'visible')
   $('.sensor_item').find('.sensor_item_body').css('pointer-events', 'none')
}

function hideSensorLoader(sensorElementId){
   $(sensorElementId + ' .sensor_item_body .sensor-loader').css('visibility', 'hidden')
   $('.sensor_item').find('.sensor_item_body').css('pointer-events', 'all')
}


function validateSensorTemperature(input){
    if(input.value<4 || input.value>30){
        alert("Тепература должна быть в диапазоне: [4;30]")
        return false
    }
    return true
}