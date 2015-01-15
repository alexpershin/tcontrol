DROP SCHEMA IF EXISTS dbtcontrol;

CREATE SCHEMA dbtcontrol;

CREATE TABLE dbtcontrol.sensors(
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR NOT NULL,
    type VARCHAR NOT NULL,
    description VARCHAR DEFAULT NULL,
    low_thresshold DOUBLE,
    high_thresshold DOUBLE,
    threshold_delta DOUBLE
);

CREATE TABLE dbtcontrol.sensor_values (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sensor_id BIGINT UNSIGNED not null,
    timestamp DATETIME not null,
    value DOUBLE not null,
    FOREIGN KEY (sensor_id) REFERENCES dbtcontrol.sensors (id) 
); 

CREATE TABLE dbtcontrol.users (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR,
    login VARCHAR not null,
    password VARCHAR not null,
    name VARCHAR not null,
    surname VARCHAR,
    role INT not null,
    accost VARCHAR
);


CREATE TABLE dbtcontrol.profiles (
   link_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
   user_id BIGINT UNSIGNED not null,
   FOREIGN KEY (user_id) REFERENCES dbtcontrol.users (id),  
   sensor_id BIGINT UNSIGNED not null,
   FOREIGN KEY (sensor_id) REFERENCES dbtcontrol.sensors (id) 
);


CREATE TABLE dbtcontrol.user_roles (
    id INT PRIMARY KEY,
    role VARCHAR not null
);

INSERT INTO dbtcontrol.sensors (id, name, type, low_thresshold, high_thresshold,
								threshold_delta, description) VALUES
	(1, 'Indoor', 'TEMPERATURE', 10, 30, 2, 'Indoor Temperature Sensor'),
	(2, 'Outdoor', 'TEMPERATURE', -25, null, 5, 'Outdor Temperature Sensor'),
    (5, 'Power Voltage', 'VOLTAGE', 205, 255, 5, 'Power Grid Voltage');

INSERT INTO dbtcontrol.sensors (id, name, type,  description) VALUES
	(3, 'Power', 'ON_OF', 'Power On or Off Sensor'),
    (4, 'Door Closed', 'ALARM','Door Closed Sensor');


INSERT INTO dbtcontrol.sensors (name, type, low_thresshold, high_thresshold,
								threshold_delta, description) VALUES
	( 'test_sensor', 'TEMPERATURE', 10, 30, 2, 'Indoor Temperature Sensor');


INSERT INTO dbtcontrol.user_roles (id, role) VALUES 
	('1', 'User'),
	('2', 'SuperUser'),
	('3', 'Admin');

INSERT INTO dbtcontrol.users (email, login, password, name, surname, role, accost) VALUES  	
	('Alex.Pershin@gmail.com', 'alex', 'TheSnake', 'Alex', 'Pershin', '3', 'Mr. '),
	('buslavskii@gmail.com', 'anton', 'TheBeast', 'Anton', 'Buslavskii', '3', 'Sir ');

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
	('4', '2014-08-12 00:01:02', '1'),
	('4', '2014-08-09 09:42:19', '1'),
	('4', '2014-08-10 23:59:59', '1'),
	('5', '2014-08-11 15:16:17', '230.4');



INSERT INTO dbtcontrol.profiles (user_id, sensor_id) VALUES
  	('1', '1'),        
  	('1', '2'),        
  	('1', '4'),        
  	('1', '6'),        
  	('2', '3'),        
  	('2', '5'),        
  	('2', '6'); 
