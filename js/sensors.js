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
        error: function (jqXHR, textStatus, errorThrown) {
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
    console.log('sensorValue: ' + sensorValue);
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
        'ALARM_ON': 'darkred',
        'ALARM_OFF': 'lightgreen',
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

        const currentTemperatureURL =
            window.location.protocol
            + "//" + window.location.host
            + ":/tcontrol/api/thermostat_current_temperature?sensorId="
            + sensorValue.sensorId

        $.ajax({
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                url: currentTemperatureURL,
                beforeSend: function () {
                    showSensorLoader(sensorElementId)
                },
                success: function (currentTemperatures) {
                     startHeatingDialog(sensorElementId, sensorValue, currentTemperatures)
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    hideSensorLoader(sensorElementId)
                    alert("Error try again later: " + textStatus)
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

function setupPlot(sensorElementId, sensorValue, shape) {
 const sensorPlot = $(sensorElementId + ' .sensor_item_body .sensor_indicator_panel .sensor_plot')
    sensorPlot.click(function (ev) {

        const lastValueURL =
            window.location.protocol
            + "//" + window.location.host
            + ":/tcontrol/api/sensor_previous_values?sensorId="
            + sensorValue.sensorId

        $.ajax({
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                url: lastValueURL,
                beforeSend: function () {
                    showSensorLoader(sensorElementId)
                },
                success: function (data) {
                     startPlotDialog(sensorElementId, sensorValue, data.values, shape)
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    hideSensorLoader(sensorElementId)
                    alert("Error try again later: " + textStatus)
                },
                complete: function () {
                    hideSensorLoader(sensorElementId)
                }
            });
    })
}

function startHeatingDialog(sensorElementId, sensorValue, currentTemperatures){
    const startHeatingDialog = document.getElementById('start-heating');

    var applyBtn = document.getElementById('start-heating-apply-btn');
    var rollbackBtn = document.getElementById('start-heating-rollback-btn')

    //remove previous listeners
    const applyButtonClone = applyBtn.cloneNode(true);
    applyBtn.parentNode.replaceChild(applyButtonClone, applyBtn);
    const rollbackButtonClone = rollbackBtn.cloneNode(true);
    rollbackBtn.parentNode.replaceChild(rollbackButtonClone, rollbackBtn);

    const startHeatingDialogRow = document.getElementById("thermostat-table-row");

    const thermostatRowBaseId = 'thermostat-table-row'
    $(currentTemperatures).each(function (key, currentTemperature) {
        cloneRow = $('#' + thermostatRowBaseId).clone()
        cloneRow.appendTo('.thermostat-table')
        thermostatRowId =  cloneRow.attr('id') + '-' + currentTemperature.thermostatCode
        cloneRow.attr("id", thermostatRowId)
        const startHeatingDialogInput = $('#' + thermostatRowId + ' #start-heating-input')

        const startHeatingDialogInputCaption = $('#' + thermostatRowId + ' #start-heating-input-caption')

        startHeatingDialogInput.val(currentTemperature.temperature)
        startHeatingDialogInputCaption.text(currentTemperature.thermostatName)
        $('#' + thermostatRowId).show();
    })

    applyBtn = document.getElementById('start-heating-apply-btn')
    closeBtn = document.getElementById('start-heating-close-btn')
    rollbackBtn = document.getElementById('start-heating-rollback-btn')
    const titleComponent = document.getElementById('start-heating-dialog-title')
    titleComponent.textContent = $(sensorElementId + ' #sensor_title').text()

    applyBtn.addEventListener('click', () => {
         $(currentTemperatures).each(function (key, currentTemperature) {
            thermostatRowId =  thermostatRowBaseId + '-' + currentTemperature.thermostatCode
            const startHeatingDialogInput = $('#' + thermostatRowId + ' #start-heating-input')
            if(validateSensorTemperature(startHeatingDialogInput)){
                currentTemperatures[key].temperature = startHeatingDialogInput.val()
            }
         })
        startHeating(sensorElementId, sensorValue, currentTemperatures)
    })
    closeBtn.addEventListener('click', () => {
         closeHeatingDialog()
    })
    rollbackBtn.addEventListener('click', () => {
        rollbackHeating(sensorElementId, sensorValue, currentTemperatures)
    })

    showHeatingDialog()
}

function startPlotDialog(sensorElementId, sensorValue, values, shape){
     closeBtn = document.getElementById('plot-dialog-close-btn')
     closeBtn.addEventListener('click', () => {
         closePlotDialog()
     })
     var data = [trace1/*, trace2, trace3*/];
     sensorTitle = $(sensorElementId + ' #sensor_title');

     var x = []
     var y = []
     var i = 0
     var xMin = Number.MAX_SAFE_INTEGER
     var xMax = 0
     $(values).each(function (key, value) {
          x[i] = new Date(value.timestamp)
          if(x[i] > xMax) xMax = x[i]
          if(x[i] < xMin) xMin= x[i]
          y[i] = value.value
          i++
     });

      var trace1 =
             {
               type: 'scatter',
               line: {shape: shape, color: 'red'},
               x: x,
               y: y,
               name: sensorTitle.text()
             }
       console.log(trace1)

 /*    var trace1 =
       {
         type: 'scatter',
         mode: "lines",
         x: ['2013-10-04 22:23:00', '2013-11-06 22:23:00', '2013-12-04 22:23:00', '2013-12-10 22:23:00', '2013-12-13 22:23:00'],
         y: [1, 3, 6, 1.5, 3.14],
         line: {color: 'red'},
         name: 'Комната'
       }
     ;

     var trace2 =
       {
         type: 'scatter',
         line: {shape: 'spline'},
         x: ['2013-10-04 22:23:00', '2013-11-06 22:23:00', '2013-12-04 22:23:00', '2013-12-10 22:23:00', '2013-12-13 22:23:00'],
         y: [4, 8, 11, 9, 6],
         line: {color: 'blue'},
         name: 'Коридор',
         line: {shape: 'spline'},
       }
     ;

     var trace3 =
       {
         type: 'scatter',
         mode: "lines",
         x: ['2013-10-04 22:23:00', '2013-11-06 22:23:00', '2013-12-04 22:23:00', '2013-12-10 22:23:00', '2013-12-13 22:23:00'],
         y: [0, 1, 1, 0, 1],
         line: {color: 'blue'},
         name: 'Котел',
         line: {shape: 'hv'}
       }
     ;*/

     var data = [trace1/*, trace2, trace3*/];

     var layout = {
       title: {
         text: 'Исторические данные' + (data.length == 1 ? ' (' + sensorTitle.text() + ')' : '')
       },
       xaxis: {
         range: [new Date(xMin), new Date(xMax)],
         type: 'date'
       },
       yaxis: {
         autorange: true,
         range: [0,12],
         type: 'linear'
       },
     }

     Plotly.newPlot('plot-dialog-diagram', data, layout, {scrollZoom: true});

     showPlotDialog()
}

function showHeatingDialog(){
    document.getElementById('start-heating').style.visibility='visible'
    document.getElementById('overlay').style.visibility='visible'
}

function closeHeatingDialog(){
    const thermostatRowBaseId = 'thermostat-table-row'
    $("[id^='" + thermostatRowBaseId + "-']").remove()
    document.getElementById('start-heating').style.visibility='hidden'
    document.getElementById('overlay').style.visibility='hidden'
}

function showPlotDialog(){
    document.getElementById('plot-dialog').style.visibility='visible'
    document.getElementById('overlay').style.visibility='visible'
}

function closePlotDialog(){
    document.getElementById('plot-dialog').style.visibility='hidden'
    document.getElementById('overlay').style.visibility='hidden'
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

function startHeating(sensorElementId, sensorValue, currentTemperatures){//sensorElementId, sensorValue
    const sensorElement = $(sensorElementId + ' .sensor_item_body .sensor_value')
    const startHeatingDialog = document.getElementById('start-heating');

    const popUpElement = document.getElementById("pop-up");
    popUpElement.style.visibility='visible';

    let onOffUrl = window.location.protocol
        +'//'+window.location.host
        +'/tcontrol/api/start_process';

   var changeThermostatTemperatureRequest = {
        sensorId: sensorValue.sensorId,
        newTemperatures: currentTemperatures
    }

    closeHeatingDialog()

    $.ajax({
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify(changeThermostatTemperatureRequest),
        url: onOffUrl,
        beforeSend: function () {
            showSensorLoader(sensorElementId)
        },
        success: function (data) {
            console.log('on finish: ' + data.value)
            sensorValue.value = data.value;
            const backgroundCalcResult = onOffSensorBackgroundCalc(sensorValue)
            sensorElement.text(backgroundCalcResult.status);
            const sensorBody = $(sensorElementId + ' .sensor_item_body')
            sensorBody.css('background', backgroundCalcResult.background);
            setSensorTime(sensorElementId, data.timestamp)
        },
        error: function (jqXHR, textStatus, errorThrown) {
            hideSensorLoader(sensorElementId)
            popUpElement.style.visibility='hidden'
            closeHeatingDialog()
            alert("Error try again later: " + textStatus)
        },
        complete: function () {
            hideSensorLoader(sensorElementId)
            popUpElement.style.visibility='hidden'
            closeHeatingDialog()
        }
    });
}

function rollbackHeating(sensorElementId, sensorValue, currentTemperatures){
    const sensorElement = $(sensorElementId + ' .sensor_item_body .sensor_value')
    const startHeatingDialog = document.getElementById('start-heating');

    const popUpElement = document.getElementById("pop-up");
    popUpElement.style.visibility='visible';

    let rollbackUrl = window.location.protocol
        + '//' + window.location.host
        + '/tcontrol/api/thermostat_rollback?sensorId='  + sensorValue.sensorId;

    closeHeatingDialog()

    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        data: sensorValue.sensorId,
        url: rollbackUrl,
        beforeSend: function () {
            showSensorLoader(sensorElementId)
        },
        success: function (data) {
            console.log('rollback success: ' + data.value)
            sensorValue.value = data.value;
            const backgroundCalcResult = onOffSensorBackgroundCalc(sensorValue)
            sensorElement.text(backgroundCalcResult.status);
            const sensorBody = $(sensorElementId + ' .sensor_item_body')
            sensorBody.css('background', backgroundCalcResult.background);
            setSensorTime(sensorElementId, data.timestamp)
        },
        error: function (jqXHR, textStatus, errorThrown) {
            hideSensorLoader(sensorElementId)
            popUpElement.style.visibility='hidden'
            alert("Error try again later: " + textStatus)
        },
        complete: function () {
            hideSensorLoader(sensorElementId)
            popUpElement.style.visibility='hidden'
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


