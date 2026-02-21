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
                   // close inside startPlotDialog()
                   // hideSensorLoader(sensorElementId)
                }
            });
    })
}

function startHeatingDialog(sensorElementId, sensorValue, currentTemperatures){
    const startHeatingDialog = document.getElementById('start-heating');

    var applyBtn = document.getElementById('start-heating-apply-btn');
    var rollbackBtn = document.getElementById('start-heating-rollback-btn')
    var closeBtn = document.getElementById('start-heating-close-btn')

    //remove previous listeners
    const applyButtonClone = applyBtn.cloneNode(true);
    applyBtn.parentNode.replaceChild(applyButtonClone, applyBtn);
    const rollbackButtonClone = rollbackBtn.cloneNode(true);
    rollbackBtn.parentNode.replaceChild(rollbackButtonClone, rollbackBtn);
    const closeButtonClone = closeBtn.cloneNode(true);
    closeBtn.parentNode.replaceChild(closeButtonClone, closeBtn);

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
    rollbackBtn = document.getElementById('start-heating-rollback-btn')
    closeBtn = document.getElementById('start-heating-close-btn')
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

function startAlertsDialog(sensorElementId, sensorValue, currentAlerts){
    const alertsDialog = document.getElementById('alerts-dialog');

    const alertsRow = document.getElementById("alerts-row");

    const alertsRowBaseId = 'alerts-row'
    $(currentAlerts).each(function (key, currentAlert) {
        cloneRow = $('#' + alertsRowBaseId).clone()
        cloneRow.appendTo('.alerts-table')
        alertsRowId =  cloneRow.attr('id') + '-' + currentAlert.timestamp
        cloneRow.attr("id", alertsRowId)
        const alertsTimeInput = $('#' + alertsRowId + ' #alerts-time-input')

        const alertsMessageInput = $('#' + alertsRowId + ' #alerts-message-input')

        date = new Date()
        date.setTime(currentAlert.timestamp)
        const dateValue = formatTimestamp(date)

        alertsTimeInput.val(dateValue)
        alertsTimeInput.attr('readonly', true)
        alertsMessageInput.val(currentAlert.message)
        alertsMessageInput.attr('readonly', true)
        $('#' + alertsRowId).show();
    })

    //2 empty rows
    for (let i = 0; i < 2; i++) {
            cloneRow = $('#' + alertsRowBaseId).clone()
            cloneRow.appendTo('.alerts-table')
            alertsRowId =  cloneRow.attr('id') + '-empty' + (i+1)
            cloneRow.attr("id", alertsRowId)
            const alertsTimeInput = $('#' + alertsRowId + ' #alerts-time-input')
            const alertsMessageInput = $('#' + alertsRowId + ' #alerts-message-input')
            alertsTimeInput.attr('readonly', true)
            alertsMessageInput.attr('readonly', true)
            $('#' + alertsRowId).show();
    }

    closeBtn = document.getElementById('alerts-dialog-close-btn')
    //remove previous
    const closeButtonClone = closeBtn.cloneNode(true);
    closeBtn.parentNode.replaceChild(closeButtonClone, closeBtn);
    closeButtonClone.addEventListener('click', () => {
         closeAlertsDialog()
    })
    showAlertsDialog()
}

function formatTimestamp(timestamp) {
  const date = timestamp.toISOString().split('T')[0]
  const time = timestamp.toTimeString().split(' ')[0]//.replace(/:/g, '-');
  return `${date} ${time}`
}

function startPlotDialog(sensorElementId, sensorValue, values, shape){
    closeBtn = document.getElementById('plot-dialog-close-btn')
    searchBtn = document.getElementById('search-activate-btn')

    //remove previous listeners
    const closeBtnClone = closeBtn.cloneNode(true);
    closeBtn.parentNode.replaceChild(closeBtnClone, closeBtn);
    const searchBtnClone = searchBtn.cloneNode(true);
    searchBtn.parentNode.replaceChild(searchBtnClone, searchBtn);

    closeBtnClone.addEventListener('click', () => {
         closePlotDialog()
    })

     searchBtnClone.addEventListener('click', () => {
         searchDataInRange(sensorElementId, sensorValue)
     })
     toDate = new Date()//now
     fromDate = new Date()
     fromDate.setTime(fromDate.getTime() - 1*24*60*60*1000)//-1 day
     const dateFromTimeInputValue = new Date(fromDate.getTime() + fromDate.getTimezoneOffset() * -60 * 1000).toISOString().slice(0, 19)
     const dateToTimeInputValue = new Date(toDate.getTime() + toDate.getTimezoneOffset() * -60 * 1000).toISOString().slice(0, 19)
     dateFromField = document.getElementById('plot-dialog-from')
     dateFromField.value = dateFromTimeInputValue
     dateToField = document.getElementById('plot-dialog-to')
     dateToField.value = dateToTimeInputValue

     sensorTitle = $(sensorElementId + ' #sensor_title');

     prepareDataForPlot(values, shape, function(data, layout, config){
             Plotly.newPlot('plot-dialog-diagram', data, layout, config)
             hideSensorLoader(sensorElementId)
             showPlotDialog()
     });
}

function prepareDataForPlot(values, shape, drawPlotCallback ){
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

      console.log("ploty lib loading.. if need")
      import("../plotly-3.3.0.min.js").then((mod2) => {
          console.log("ploty lib loaded")

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
                 showlegend: false,
                 margin: {
                    l: 30,
                    r: 10,
                    b: 30,
                    t: 30,
                    pad: 1
                 },
                 paper_bgcolor: 'lightgray',
                 plot_bgcolor: 'lightgray',
           }

           var config = {
                  scrollZoom: true,
                  displayModeBar: true,
    //                modeBarButtonsToAdd: [
    //                     {
    //                           name: 'apply',
    //                           icon: Plotly.Icons.pencil,
    //                           direction: 'up',
    //                           click: function(gd) {alert('apply')}
    //                     }
    //                ],
                //  modeBarButtonsToRemove: ['pan2d','select2d','lasso2d','resetScale2d','zoomOut2d']
           }

          drawPlotCallback(data, layout, config)
      })
}

function showPlotDialog(){
    document.getElementById('plot-dialog').style.visibility='visible'
    document.getElementById('overlay').style.visibility='visible'
}

function closePlotDialog(){
    document.getElementById('plot-dialog').style.visibility='hidden'
    document.getElementById('overlay').style.visibility='hidden'
    closeBtn = document.getElementById('plot-dialog-close-btn')
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

function showAlertsDialog(){
    document.getElementById('alerts-dialog').style.visibility='visible'
    document.getElementById('overlay').style.visibility='visible'
}

function closeAlertsDialog(){
    const alertsRow = 'alerts-row'
    $("[id^='" + alertsRow + "-']").remove()
    document.getElementById('alerts-dialog').style.visibility='hidden'
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
                    url: currentTemperatureURL,
                    beforeSend: function () {
                        showSensorLoader(sensorElementId)
                    },
                    success: function (currentAlerts) {
                         startAlertsDialog(sensorElementId, sensorValue, currentAlerts)
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

function searchDataInRange(sensorElementId, sensorValue){
    dateFrom = new Date(document.getElementById('plot-dialog-from').value)
    dateTo = new Date(document.getElementById('plot-dialog-to').value)
    if(dateFrom.getTime() >= dateTo.getTime()){
        alert("Задайте правильно диапазоны дат: от < до")
    } else {
        minFromDate = new Date()
        minFromDate.setTime(dateTo.getTime() - 7*24*60*60*1000) //-7 days
        console.log(minFromDate)

        if(dateFrom.getTime() < minFromDate.getTime() ){
             alert("Период должен быть не больше 7 дней")
        } else {
              const historyURL =
                    window.location.protocol
                    + "//" + window.location.host
                    + ":/tcontrol/api/sensor_history_values?"
                    + "sensorId=" + sensorValue.sensorId
                    + "&from=" + dateFrom.toISOString()
                    + "&to=" + dateTo.toISOString()

                $.ajax({
                        type: 'GET',
                        dataType: 'json',
                        contentType: 'application/json',
                        url: historyURL,
                        beforeSend: function () {
                            showGlobalLoader()
                        },
                        success: function (data) {
                            prepareDataForPlot(data.values, 'hv', function(data, layout, config){
                                Plotly.newPlot('plot-dialog-diagram', data, layout, config)
                                hideGlobalLoader()
                            });
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            hideGlobalLoader()
                            alert("Error try again later: " + textStatus)
                        },
                        complete: function () {

                        }
                })

        }
    }
}

function validateSensorTemperature(input){
    if(input.value<4 || input.value>30){
        alert("Тепература должна быть в диапазоне: [4;30]")
        return false
    }
    return true
}