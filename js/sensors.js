function layout_sensors(sensorsJsonData) {
    $(sensorsJsonData).each(function(key, value) {
        clone = $("#sensor_element").clone();
        clone.appendTo(".sensor_items");

        sensorElementId = clone.attr("id") + value.id;
        clone.attr("id", sensorElementId);

        sensorTitle =
                $('#' + sensorElementId + ' #sensor_title');
        sensorTitle.text(value.name);

        clone.show();
    });
}

function append_values(sensorsJsonData, valuesJsonData) {
    $(valuesJsonData).each(function(key, value) {
        sensorElementId = "#sensor_element" + value.sensorId;
        sensor = $(sensorElementId);
        $(sensorElementId + ' .sensor_item_body').text(value.value);
    });
}

