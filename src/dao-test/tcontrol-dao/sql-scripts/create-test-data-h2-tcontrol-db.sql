INSERT INTO dbtcontrol.sensors (id, serial_number, name, type, low_thresshold, high_thresshold,
								threshold_delta, description) VALUES
	(1, '28-000000abc', 'Indoor', 'TEMPERATURE', 10, 30, 2, 'Indoor Temperature Sensor'),
	(2, '28-000001abc', 'Outdoor', 'TEMPERATURE', -25, null, 5, 'Outdor Temperature Sensor'),
        (5, '28-000003abc', 'Power Voltage', 'VOLTAGE', 205, 255, 5, 'Power Grid Voltage');

INSERT INTO dbtcontrol.sensors (id, serial_number, name, type,  description) VALUES
	(3, '28-000004abc', 'Power', 'ON_OFF', 'Power On or Off Sensor'),
        (4, '28-000005abc', 'Door Closed', 'ALARM','Door Closed Sensor');

INSERT INTO dbtcontrol.sensors (name, serial_number, type, low_thresshold, high_thresshold,
								threshold_delta, description) VALUES
	( '28-000006abc', 'test_sensor', 'TEMPERATURE', 10, 30, 2, 'Indoor Temperature Sensor' );

INSERT INTO dbtcontrol.user_roles (id, role) VALUES 
	('1', 'User'),
	('2', 'SuperUser'),
	('3', 'Admin');

INSERT INTO dbtcontrol.users (id, email, login, password, name, surname, role, accost) VALUES  	
	(1, 'Alex.Pershin@gmail.com', 'alex', 'TheSnake', 'Alex', 'Pershin', '3', 'Mr. '),
	(2, 'buslavskii@gmail.com', 'anton', 'TheBeast', 'Anton', 'Buslavskii', '3', 'Sir ');

INSERT INTO dbtcontrol.sensor_values (sensor_id, timestamp, value) VALUES
  	('1', '2014-08-08 00:01:02', '20.1'),        
	('1', '2014-08-08 00:10:11', '20.25'),
	('1', '2014-08-09 10:12:02', '21.30'),
	('2', '2014-08-10 14:01:02', '10.01'),
	('2', '2014-08-11 18:01:02', '12.06'),
	('2', '2014-08-12 12:01:57', '8.56'),
	('3', '2014-08-09 07:55:09', '1'),
	('3', '2014-08-10 08:16:17', '0'),
	('3', '2014-08-11 16:01:17', '1'),
	('4', '2014-08-12 00:01:02', '0'),
	('4', '2014-08-09 09:42:19', '0'),
	('4', '2014-08-10 23:59:59', '1'),
	('5', '2014-08-11 15:16:17', '230.6');

INSERT INTO dbtcontrol.profiles (user_id, sensor_id) VALUES
  	('1', '1'),        
  	('1', '2'),        
  	('1', '4'),        
  	('1', '6'),        
  	('2', '3'),        
  	('2', '5'),        
  	('2', '6'); 