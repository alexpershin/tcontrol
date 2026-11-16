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

function startAlertsDialog(sensorElementId, sensorValue, currentAlerts){
    const alertsDialog = document.getElementById('alerts-dialog');

    const alertsRow = document.getElementById("alerts-row");

    const alertsRowBaseId = 'alerts-row'
    var alertId=1;
    $(currentAlerts).each(function (key, currentAlert) {
        cloneRow = $('#' + alertsRowBaseId).clone()
        cloneRow.appendTo('.alerts-table')
        alertsRowId =  cloneRow.attr('id') + '-' + alertId
        alertId = alertId + 1
        cloneRow.attr("id", alertsRowId)
        const alertsTimeInput = $('#' + alertsRowId + ' #alerts-time-input')
        const alertsCountInput = $('#' + alertsRowId + ' #alerts-count-input')
        const alertsMessageInput = $('#' + alertsRowId + ' #alerts-message-input')

        date = new Date()
        date.setTime(currentAlert.lastTimestamp)
        const dateValue = formatTimestamp(date)

        alertsTimeInput.val(dateValue)
        alertsTimeInput.attr('readonly', true)
        alertsCountInput.val(currentAlert.count)
        alertsCountInput.attr('readonly', true)
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
