DROP DATABASE IF EXISTS dbtcontrol; 

CREATE DATABASE IF NOT EXISTS dbtcontrol; -- IF NOT EXISTS;


CREATE TABLE IF NOT EXISTS dbtcontrol.sensors (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    type VARCHAR(20) NOT NULL,
    description VARCHAR(1024) DEFAULT NULL,
	low_thresshold DOUBLE,
	hight_threshold DOUBLE,
	threshold_delta DOUBLE
);
 

CREATE TABLE dbtcontrol.sensor_values (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sensor_id BIGINT UNSIGNED,
    timestamp DATETIME,
    value DOUBLE,
    FOREIGN KEY (sensor_id) REFERENCES dbtcontrol.sensors (id)
);
 

CREATE TABLE dbtcontrol.users (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(256),
    login VARCHAR(256),
    password VARCHAR(256),
    name VARCHAR(256),
    surname VARCHAR(256),
    role INT(1),
    accost VARCHAR(128)
);

 

CREATE TABLE dbtcontrol.user_roles (
    id INT PRIMARY KEY,
    role VARCHAR(256)
); 


INSERT INTO dbtcontrol.user_roles (id, role) 
    VALUES ('1', 'User'),
           ('2', 'SuperUser'),
           ('3', 'Admin');


INSERT INTO dbtcontrol.users (email, login, password, name, surname, role, accost)
       VALUES  	('Alex.Pershin@gmail.com', 'alex', 'TheSnake', 'Alex', 'Pershin', '3', 'Mr. '),
         	('buslavskii@gmail.com', 'anton', 'TheBeast', 'Anton', 'Buslavskii', '3', 'Sir ');


INSERT INTO dbtcontrol.sensors (id, name, type, description)
       VALUES  	(1, 'Indoor', 'TEMPERATURE', 'Indoor Temperature Sensor'),
         	(2, 'Outdoor', 'TEMPERATURE', 'Outdor Temperature Sensor'),
         	(3, 'Power', 'ON_OF', 'Power On or Off Sensor'),
         	(4, 'Door Closed', 'ALARM','Door Closed Sensor'),
         	(5, 'Power Voltage', 'VOLTAGE','Power Grid Voltage');


INSERT INTO dbtcontrol.sensor_values (sensor_id, timestamp, value)
       VALUES  	('1', '2014-08-08 00:01:02', '20.1'),
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
