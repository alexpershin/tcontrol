<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
    <head>
        <title>Sensors</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <script src="resources/js/jquery/jquery-2.1.1.js" type="text/javascript"></script>
    </head>
    <body onload="onBodyLoad()">
        <div id="sensorsDivHeader"> 
            <script  type="text/javascript">
                function onBodyLoad() {
                    $.getJSON("/spring-web-service/sensors",
                            {
                                format: "json"
                            },
                    function(result)
                    {
                        $.each(result, function(i, item) {
                            addSensor(item.name, item.id);
                        });
                    })
                            .success(function() {
                                var sensorsDivHeader = document.getElementById("sensorsDivHeader");
                                var newContent = document.createTextNode("Sensors request success!");
                                sensorsDivHeader.appendChild(newContent);
                                sensorsDivHeader.style.color = "green";
                            })
                            .error(function() {
                                var sensorsDivHeader = document.getElementById("sensorsDivHeader");
                                var newContent = document.createTextNode("Sensors request failed!");
                                sensorsDivHeader.appendChild(newContent);
                                sensorsDivHeader.style.color = "red";
                            })
                            .complete(function() {

                            });

                }
                function addSensor(name, id) {
                    // create a new div element 
                    // and give it some content 
                    var newDiv = document.createElement("div");
                    var newContent = document.createTextNode("Sensor: name = " + name + ", id = " + id);
                    newDiv.appendChild(newContent); //add the text node to the newly created div. 

                    // add the newly created element and its content into the DOM 
                    var currentDiv = document.getElementById("sensorsDivHeader");
                    document.body.insertBefore(newDiv, currentDiv);
                }
            </script>
        </div>
    </body>

</html>
