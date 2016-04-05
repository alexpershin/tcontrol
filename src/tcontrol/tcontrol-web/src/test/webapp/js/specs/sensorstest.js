describe("sensors logic test", function () {

    it("state background calculation", function () {
        r = sensorBackgroundCalc({sensorId: 1, value: 25.5, state: 'NORMAL'})

        expect(r).toBe(STATE_BACKGROUND.get('NORMAL'));
    });

});


describe("sensors render test", function () {

    beforeEach(function () {
        //fixture will be reset before each test
        jasmine.getFixtures()
                .fixturesPath = 'src/main/webapp/';
        loadFixtures('sensors-body.html');
    });

    it("simple renderer test", function () {
        clone = $('#sensor_element').clone();
        expect(clone.attr('id')).toBe('sensor_element');
    });

    it("temperature renderer test", function () {
        var sensorsJsonData = [
            {name: 'Indoor', id: 1, type: 'TEMPERATURE',
                lowThreshold: 10, highThreshold: 31, thresholdLag: 2},
        ];

        var valuesJsonData = [
            {sensorId: 1, value: 25.532, state: 'NORMAL', gradient: 0.34},
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

        sensorGradient = $('#' + sensorElementId +
                ' .sensor_item_body .sensor_gradient');
        expect(sensorGradient.text()).toBe('+0.3');

        sensorBody = $('#' + sensorElementId + ' .sensor_item_body');
        //expect(sensorBody.css('background')).toBe(STATE_BACKGROUND.get('NORMAL'));

    });

    it("temperature renderer test empty gradient", function () {
        var sensorsJsonData = [
            {name: 'Indoor', id: 1, type: 'TEMPERATURE',
                lowThreshold: 10, highThreshold: 31, thresholdLag: 2},
        ];

        var valuesJsonData = [
            {sensorId: 1, value: 25.532, state: 'NORMAL'},
        ];

        sensorMap = convertSensorsJsonToMap(sensorsJsonData);
        layoutSensors(sensorsJsonData);
        renderSensorValues(sensorMap, valuesJsonData);

        sensorElementId = 'sensor_element1';

        sensorGradient = $('#' + sensorElementId +
                ' .sensor_item_body .sensor_gradient');
        expect(sensorGradient.text()).toBe('...');
    });

    it("temperature renderer test negative gradient", function () {
        var sensorsJsonData = [
            {name: 'Indoor', id: 1, type: 'TEMPERATURE',
                lowThreshold: 10, highThreshold: 31, thresholdLag: 2},
        ];

        var valuesJsonData = [
            {sensorId: 1, value: 25.532, state: 'NORMAL', gradient: -0.2},
        ];

        sensorMap = convertSensorsJsonToMap(sensorsJsonData);
        layoutSensors(sensorsJsonData);
        renderSensorValues(sensorMap, valuesJsonData);

        sensorElementId = 'sensor_element1';

        sensorGradient = $('#' + sensorElementId +
                ' .sensor_item_body .sensor_gradient');
        expect(sensorGradient.text()).toBe('-0.2');
    });
    
    it("temperature renderer test zero gradient", function () {
        var sensorsJsonData = [
            {name: 'Indoor', id: 1, type: 'TEMPERATURE',
                lowThreshold: 10, highThreshold: 31, thresholdLag: 2},
        ];

        var valuesJsonData = [
            {sensorId: 1, value: 25.532, state: 'NORMAL', gradient: 0.0},
        ];

        sensorMap = convertSensorsJsonToMap(sensorsJsonData);
        layoutSensors(sensorsJsonData);
        renderSensorValues(sensorMap, valuesJsonData);

        sensorElementId = 'sensor_element1';

        sensorGradient = $('#' + sensorElementId +
                ' .sensor_item_body .sensor_gradient');
        expect(sensorGradient.text()).toBe('0.0');
    });
    
    it("temperature renderer test positive gradient", function () {
        var sensorsJsonData = [
            {name: 'Indoor', id: 1, type: 'TEMPERATURE',
                lowThreshold: 10, highThreshold: 31, thresholdLag: 2},
        ];

        var valuesJsonData = [
            {sensorId: 1, value: 25.532, state: 'NORMAL', gradient: 0.1},
        ];

        sensorMap = convertSensorsJsonToMap(sensorsJsonData);
        layoutSensors(sensorsJsonData);
        renderSensorValues(sensorMap, valuesJsonData);

        sensorElementId = 'sensor_element1';

        sensorGradient = $('#' + sensorElementId +
                ' .sensor_item_body .sensor_gradient');
        expect(sensorGradient.text()).toBe('+0.1');
    });
    
    it("gradient color test", function () {
        expect(calcGradientValueAndColor(0).color).toBe('blue');
        expect(calcGradientValueAndColor(-0.1).color).toBe('blue');
        expect(calcGradientValueAndColor(0.1).color).toBe('red');
        expect(calcGradientValueAndColor(null).color).toBe('blue');
        expect(calcGradientValueAndColor(null).text).toBe('...');
    });

    it("alarm renderer test", function () {
        var sensorsJsonData = [
            {name: 'Alarm', id: 1, type: 'ALARM'},
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
    });

    pAlertSensorStatusBacgroundTest = function (sensorValue, expectedStatus, expectedBackground) {
        it("alarm renderer test background", function () {
            result = alertSensorStatusBacgroundCalc(sensorValue);
            expect(result.status).toBe(expectedStatus);
            expect(result.background).toBe(expectedBackground);
        });
    }
    pAlertSensorStatusBacgroundTest({sensorId: 1, value: 1.0, state: 'ALERT'}, 'Alarm', STATE_BACKGROUND.get('ALARM_ON'));
    pAlertSensorStatusBacgroundTest({sensorId: 1, value: 0.0, state: 'NORMAL'}, 'Ok', STATE_BACKGROUND.get('ALARM_OFF'));
    pAlertSensorStatusBacgroundTest({sensorId: 1, value: 0.0, state: null}, undefined, STATE_BACKGROUND.get('UNDEFINED'));
    pAlertSensorStatusBacgroundTest({sensorId: 1, value: 0.0}, undefined, STATE_BACKGROUND.get('UNDEFINED'));

    it("on_off renderer test", function () {
        var sensorsJsonData = [
            {name: 'Heating', id: 1, type: 'ON_OFF'},
        ];

        var valuesJsonData = [
            {sensorId: 1, value: 1, state: 'ON'},
        ];

        sensorMap = convertSensorsJsonToMap(sensorsJsonData);
        layoutSensors(sensorsJsonData);
        renderSensorValues(sensorMap, valuesJsonData);

        sensorElementId = 'sensor_element1';
        sensorTitle = $('#' + sensorElementId + ' #sensor_title');
        expect(sensorTitle.text()).toBe('Heating');

        sensorValue = $('#' + sensorElementId +
                ' .sensor_item_body .sensor_value');
        expect(sensorValue.text()).toBe('On');
    });

    onOffSensorBacgroundTest = function (sensorValue, expectedStatus, expectedBackground) {
        it("on/off renderer test background", function () {
            result = onOffSensorBacgroundCalc(sensorValue);
            expect(result.status).toBe(expectedStatus);
            expect(result.background).toBe(expectedBackground);
        });
    }

    onOffSensorBacgroundTest({sensorId: 1, value: 1.0, state: 'ALERT'}, 'On', STATE_BACKGROUND.get('ON'));
    onOffSensorBacgroundTest({sensorId: 1, value: 0.0, state: 'NORMAL'}, 'Off', STATE_BACKGROUND.get('OFF'));
    onOffSensorBacgroundTest({sensorId: 1, value: 1, state: 'ALERT'}, 'On', STATE_BACKGROUND.get('ON'));
    onOffSensorBacgroundTest({sensorId: 1, value: 0, state: 'NORMAL'}, 'Off', STATE_BACKGROUND.get('OFF'));
    onOffSensorBacgroundTest({sensorId: 1, value: '1.0', state: 'ALERT'}, 'On', STATE_BACKGROUND.get('ON'));
    onOffSensorBacgroundTest({sensorId: 1, value: '0.0', state: 'NORMAL'}, 'Off', STATE_BACKGROUND.get('OFF'));
    onOffSensorBacgroundTest({sensorId: 1, value: '1', state: 'ALERT'}, 'On', STATE_BACKGROUND.get('ON'));
    onOffSensorBacgroundTest({sensorId: 1, value: '0', state: 'NORMAL'}, 'Off', STATE_BACKGROUND.get('OFF'));
    onOffSensorBacgroundTest({sensorId: 1, value: 0.0, state: null}, 'Off', STATE_BACKGROUND.get('OFF'));
    onOffSensorBacgroundTest({sensorId: 1, value: 0.0}, 'Off', STATE_BACKGROUND.get('OFF'));
});

