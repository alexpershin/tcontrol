/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
describe("simple test", function() {
  var a;

  it("and so is a spec", function() {
    a = true;

    expect(a).toBe(true);
  });
});


describe("sensors test", function() {
  var a;

  it("state background calculation", function() {
    r=sensorBackgroundCalc({sensorId: 1, value: 25.5, state: 'NORMAL'})

    expect(r).toBe(STATE_BACKGROUND.get('NORMAL'));
  });
  
});