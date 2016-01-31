INSERT INTO dbtcontrol.sensors (id, serial_number, name, type, low_thresshold, high_thresshold,
								threshold_delta, description) VALUES
	(1, '28-000002c7c3ee', 'Дом', 'TEMPERATURE', 12, 28, 2, 'Дом'),
        (2, '28-000002c7d1b5', 'Улица', 'TEMPERATURE', -15, null, 5, 'Улица'),
        (3, '28-000002c7eed3', 'Подвал', 'TEMPERATURE', 5, 20, 2, 'Подвал'),
	(4, '28-0000066c6a65', 'Бокс', 'TEMPERATURE', 12, 30, 2, 'Бокс сигнализации');


INSERT INTO dbtcontrol.user_roles (id, role) VALUES 
	('1', 'User'),
	('2', 'SuperUser'),
	('3', 'Admin');

INSERT INTO dbtcontrol.users (id, email, login, password, name, surname, role, accost) VALUES  	
	(1, 'Alex.Pershin@gmail.com', 'alex', 'TheSnake', 'Alex', 'Pershin', '3', 'Mr. '),
	(2, 'buslavskii@gmail.com', 'anton', 'TheBeast', 'Anton', 'Buslavskii', '3', 'Sir ');

INSERT INTO dbtcontrol.profiles (user_id, sensor_id) VALUES
  	('1', '1'),        
  	('1', '2'),        
  	('1', '3'),        
  	('1', '4'),        
  	('2', '1'),        
  	('2', '2'),        
  	('2', '3'); 

--INSERT INTO dbtcontrol.sensor_values (sensor_id, timestamp, value) VALUES
  --	('4', '2016-01-01 00:01:02', '20.0');