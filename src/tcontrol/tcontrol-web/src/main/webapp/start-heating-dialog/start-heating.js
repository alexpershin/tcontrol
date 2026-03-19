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

        const thermostatsNameColumn = $('#' + thermostatRowId + ' #thermostats-name-column')
        thermostatsNameColumn.text(currentTemperature.thermostatName)

        const dayHeatingTempInput = $('#' + thermostatRowId + ' #day-heating-temp-input')
        dayHeatingTempInput.val(currentTemperature.dayTemperature)

        const dayHeatingTimeInput = $('#' + thermostatRowId + ' #day-heating-time-input')
        dayHeatingTimeInput.val(convertTimeFromUTCToLocal(currentTemperature.dayTime))

        const nightHeatingTempInput = $('#' + thermostatRowId + ' #night-heating-temp-input')
        nightHeatingTempInput.val(currentTemperature.nightTemperature)

        const nightHeatingTimeInput = $('#' + thermostatRowId + ' #night-heating-time-input')
        nightHeatingTimeInput.val(convertTimeFromUTCToLocal(currentTemperature.nightTime))

        $('#' + thermostatRowId).show();
    })

    applyBtn = document.getElementById('start-heating-apply-btn')
    rollbackBtn = document.getElementById('start-heating-rollback-btn')
    closeBtn = document.getElementById('start-heating-close-btn')
    const titleComponent = document.getElementById('start-heating-dialog-title')
    titleComponent.textContent = $(sensorElementId + ' #sensor_title').text()

    applyBtn.addEventListener('click', () => {
         var res = true;
         $(currentTemperatures).each(function (key, currentTemperature) {
            thermostatRowId =  thermostatRowBaseId + '-' + currentTemperature.thermostatCode
            const dayHeatingTempInput = $('#' + thermostatRowId + ' #day-heating-temp-input')
            if(validateSensorTemperature(dayHeatingTempInput)){
                currentTemperatures[key].dayTemperature = dayHeatingTempInput.val()
            }
            const nightHeatingTempInput = $('#' + thermostatRowId + ' #night-heating-temp-input')
            if(validateSensorTemperature(nightHeatingTempInput)){
                currentTemperatures[key].nightTemperature = nightHeatingTempInput.val()
            }
            const dayHeatingTimeInput = $('#' + thermostatRowId + ' #day-heating-time-input')
            const nightHeatingTimeInput = $('#' + thermostatRowId + ' #night-heating-time-input')
            console.log(dayHeatingTimeInput.val())
            console.log(nightHeatingTimeInput.val())
            currentTemperatures[key].timeZone = new Date().getTimezoneOffset();
            if(validateSensorTime(dayHeatingTimeInput.val(), nightHeatingTimeInput.val(), currentTemperature.thermostatName)){
                currentTemperatures[key].dayTime = convertTimeToUtc(dayHeatingTimeInput.val())
                currentTemperatures[key].nightTime = convertTimeToUtc(nightHeatingTimeInput.val())
            } else {
                res = false
                return false
            }

         })
         if(res){
             startHeating(sensorElementId, sensorValue, currentTemperatures)
         }
    })
    closeBtn.addEventListener('click', () => {
         closeHeatingDialog()
    })
    rollbackBtn.addEventListener('click', () => {
        rollbackHeating(sensorElementId, sensorValue, currentTemperatures)
    })

    showHeatingDialog()
}

function convertTimeToUtc(time){
    const [hours, minutes] = time.split(':')
    const now = new Date()

    // Create a date object with local time
    now.setHours(hours);
    now.setMinutes(minutes);
    now.setSeconds(0);

    // Convert to UTC ISO string
    const utcTime = now.toISOString().substring(11, 16)
    return utcTime
}

function convertTimeFromUTCToLocal(utcTimeString){
    const todayDate = new Date().toISOString().split('T')[0]
    const utcDateTimeString = `${todayDate}T${utcTimeString}Z`
    const utcDateObject = new Date(utcDateTimeString)

    const options = {
            hour: '2-digit',
            minute: '2-digit',
            hour12: false // Use 24-hour format
    };

    const localTimeString = utcDateObject.toLocaleTimeString(navigator.language, options);

    return localTimeString
}

function validateSensorTime(dayHeatingTimeInput, nightHeatingTimeInput, thermostat){
     if (dayHeatingTimeInput == "") {
        alert("Please enter day time for '"+thermostat+"'")
        return false
     }
     if (nightHeatingTimeInput == "") {
        alert("Please enter night time for '"+thermostat+"'")
        return false
     }

     var timeNow = new Date()
     var timeParts = dayHeatingTimeInput.split(":")
     var inputDayTime = new Date(timeNow.getYear() , timeNow.getMonth() ,  timeNow.getDate() , parseInt(timeParts[0]), parseInt(timeParts[1]), 0, 0)
     timeParts = nightHeatingTimeInput.split(":")
     var inputNightTime = new Date(timeNow.getYear() , timeNow.getMonth() ,  timeNow.getDate() , parseInt(timeParts[0]), parseInt(timeParts[1]), 0, 0)

     var diff = Math.abs(inputNightTime.getTime() - inputDayTime.getTime())

     if( diff < 3600*1000 ){
        alert("Interval day-night time should be more than 1 hour '"+thermostat+"'")
        return false
     }

     return true
}
