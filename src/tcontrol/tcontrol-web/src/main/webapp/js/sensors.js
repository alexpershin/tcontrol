/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function layout_sensors(sensorsJsonData) {
    $(sensorsJsonData).each(function(key, value) {
        clone = $("#sensor_element").clone();
        clone.appendTo(".sensor_items");

        sensorElementId = clone.attr("id") + value.id;
        clone.attr("id", sensorElementId);

        sensorTitle =
                $('#' + sensorElementId + ' #sensor_title');
        sensorTitle.text(value.name);

        // alert(value.id);

        clone.show();
    });
}
