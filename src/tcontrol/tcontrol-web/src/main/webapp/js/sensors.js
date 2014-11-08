function sensorsJsonToMap(sensorsJsonData) {
    result = new Map();
    $(sensorsJsonData).each(function(key, value) {
        result.set(value.id, value);
    });
    return result;
}

function layout_sensors(sensorsJsonData) {
    $(sensorsJsonData).each(function(key, value) {
        clone = $('#sensor_element').clone();
        clone.appendTo('.sensor_items');

        sensorElementId = clone.attr('id') + value.id;
        clone.attr("id", sensorElementId);

        sensorTitle =
                $('#' + sensorElementId + ' #sensor_title');
        sensorTitle.text(value.name);

        clone.show();
    });
}

function append_values(sensorsMap, valuesJsonData) {
    $(valuesJsonData).each(function(key, value) {
        if (sensorsMap.has(value.sensorId)) {
            sensor = sensorsMap.get(value.sensorId);
            sensorElementId = '#sensor_element' + value.sensorId;
            sensorElement = $(sensorElementId);
            var resValue = '?';
            if (sensor.type === 'TEMPERATURE') {
                resValue = value.value + '\xB0';
            } else if (sensor.type === 'VOLTAGE') {
                resValue = value.value + ' V';
            } else if (sensor.type === 'ON_OFF') {
                if (value.value === 0) {
                    resValue = 'On';
                } else if (value.value === 1) {
                    resValue = 'Off';
                }
            }
            $(sensorElementId + ' .sensor_item_body .sensor_value').text(resValue);
        }
    });
}

