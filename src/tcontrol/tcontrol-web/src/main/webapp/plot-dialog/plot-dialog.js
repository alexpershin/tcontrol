
function showPlotDialog(){
    document.getElementById('plot-dialog').style.visibility='visible'
    document.getElementById('overlay').style.visibility='visible'
}

function closePlotDialog(){
    document.getElementById('plot-dialog').style.visibility='hidden'
    document.getElementById('overlay').style.visibility='hidden'
    closeBtn = document.getElementById('plot-dialog-close-btn')
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

function setupPlot(sensorElementId, sensorValue, shape) {
 const sensorPlot = $(sensorElementId + ' .sensor_item_body .sensor_indicator_panel .sensor_plot')
    sensorPlot.click(function (ev) {

        checkIfTokenSet()

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
                headers: headers(),
                beforeSend: function () {
                    showSensorLoader(sensorElementId)
                },
                success: function (data) {
                     startPlotDialog(sensorElementId, sensorValue, data.values, shape)
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    requestErrorProcessing(jqXHR, textStatus, errorThrown, "Error try again later: " + textStatus)
                },
                complete: function () {
                   // close inside startPlotDialog()
                   // hideSensorLoader(sensorElementId)
                }
            });
    })
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

              checkIfTokenSet()

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
                        headers: headers(),
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
                            requestErrorProcessing(jqXHR, textStatus, errorThrown, "Error try again later: " + textStatus)
                        },
                        complete: function () {

                        }
                })
        }
    }
}