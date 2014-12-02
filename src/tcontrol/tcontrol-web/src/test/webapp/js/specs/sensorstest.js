/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
describe("sensors logic test", function() {
    
 it("state background calculation", function() {
    r=sensorBackgroundCalc({sensorId: 1, value: 25.5, state: 'NORMAL'})

    expect(r).toBe(STATE_BACKGROUND.get('NORMAL'));
  });
  
});


describe("sensors render test", function() {
  
  beforeEach( function(){
    //fixture will be reset before each test
    jasmine.getFixtures()
        .fixturesPath = 'src/main/webapp/';
    loadFixtures( 'sensors-body.html');
  } );
  
  it("voltage renderer test", function() {
      clone = $('#sensor_element').clone();
      expect(clone.attr('id')).toBe('sensor_element');
      //voltageSensorRenderer
  });
  
});

