/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
describe("sensors logic test", function() {

    it("state background calculation", function() {
        r = sensorBackgroundCalc({sensorId: 1, value: 25.5, state: 'NORMAL'})

        expect(r).toBe(STATE_BACKGROUND.get('NORMAL'));
    });

});


describe("sensors render test", function() {

    beforeEach(function() {
        //fixture will be reset before each test
        jasmine.getFixtures()
                .fixturesPath = 'src/main/webapp/';
        loadFixtures('sensors-body.html');
    });

    it("simple renderer test", function() {
        clone = $('#sensor_element').clone();
        expect(clone.attr('id')).toBe('sensor_element');
    });

    it("temperature renderer test", function() {
        var sensorsJsonData = [
            {name: 'Indoor', id: 1, type: 'TEMPERATURE',
                lowThreshold: 10, highThreshold: 31, thresholdLag: 2},
        ];

        var valuesJsonData = [
            {sensorId: 1, value: 25.5, state: 'NORMAL'},
        ];

        sensorMap = convertSensorsJsonToMap(sensorsJsonData);
        layoutSensors(sensorsJsonData);
        renderSensorValues(sensorMap, valuesJsonData);
        
        sensorElementId = 'sensor_element1';
        sensorTitle = $('#' + sensorElementId + ' #sensor_title');
        expect(sensorTitle.text()).toBe('Indoor');
        
        sensorValue = $('#' + sensorElementId + 
                ' .sensor_item_body .sensor_value');
        expect(sensorValue.text()).toBe('25.5\xB0');
        
        sensorBody = $('#' + sensorElementId + ' .sensor_item_body');
        //expect(sensorBody.css('background')).toBe(STATE_BACKGROUND.get('NORMAL'));

    });
    
    it("alarm renderer test", function() {
        var sensorsJsonData = [
            {name: 'Alarm', id: 1, type: 'ALARM',
                lowThreshold: 0, highThreshold: 1, thresholdLag: 1},
        ];

        var valuesJsonData = [
            {sensorId: 1, value: 1.0, state: 'ALERT'},
        ];

        sensorMap = convertSensorsJsonToMap(sensorsJsonData);
        layoutSensors(sensorsJsonData);
        renderSensorValues(sensorMap, valuesJsonData);
        
        sensorElementId = 'sensor_element1';
        sensorTitle = $('#' + sensorElementId + ' #sensor_title');
        expect(sensorTitle.text()).toBe('Alarm');
        
        sensorValue = $('#' + sensorElementId + 
                ' .sensor_item_body .sensor_value');
        expect(sensorValue.text()).toBe('Alarm');
        
        sensorBody = $('#' + sensorElementId + ' .sensor_item_body');
        //expect(sensorBody.css('background')).toBe(STATE_BACKGROUND.get('NORMAL'));

    });
});

