DROP DATABASE IF EXISTS dbtcontrol; 

CREATE DATABASE IF NOT EXISTS dbtcontrol;-- IF NOT EXISTS;


CREATE TABLE IF NOT EXISTS dbtcontrol.sensors (
    SENSOR_ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    SENSOR_NAME VARCHAR(1024)
);
 

CREATE TABLE dbtcontrol.sensorValues (
    sensorId BIGINT UNSIGNED,
    valueDateTime DATETIME DEFAULT NULL,
    valueFigure DOUBLE,
    FOREIGN KEY (sensorId)
        REFERENCES sensors (SENSOR_ID)
);
 

CREATE TABLE dbtcontrol.users (
    userId BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    userEmail VARCHAR(256),
    userLogin VARCHAR(256),
    userPassword VARCHAR(256),
    userName VARCHAR(256),
    userSurname VARCHAR(256),
    userRole INT(1),
    userAccost VARCHAR(128)
);

 

CREATE TABLE dbtcontrol.userRoles (
    userRoleId INT,
    userRole VARCHAR(256)
); 


INSERT INTO dbtcontrol.userRoles (userRoleId, userRole) 
    VALUES ('1', 'User'),
           ('2', 'SuperUser'),
           ('3', 'Admin');


INSERT INTO dbtcontrol.users (userEmail, userLogin, userPassword, userName, userSurname, userRole, userAccost)
       VALUES  	('Alex.Pershin@gmail.com', 'alex', 'TheSnake', 'Alex', 'Pershin', '3', 'Mr. '),
         	('buslavskii@gmail.com', 'anton', 'TheBeast', 'Anton', 'Buslavskii', '3', 'Sir '),
         	('test@gmail.com', 'test', 'TheTest', 'Test', 'Testov', '1', 'Mrs. '),
         	('test1@gmail.com', 'test1', 'TheTest1', 'Test1', 'Testov1', '2', 'Ms. '),
         	('test2@gmail.com', 'test2', 'TheTest2', 'Test2', 'Testov2', '1', 'Dearest Mr. ');


INSERT INTO dbtcontrol.sensors (SENSOR_NAME)
       VALUES  	('Fire Sensor'),
         	('Glass Break Sensor'),
         	('On or Off sensor'),
         	('Light Sensor');


INSERT INTO dbtcontrol.sensorValues (sensorId, valueDateTime, valueFigure)
       VALUES  	('1', '2014-08-08 00:01:02', '123456'),
         	('1', '2014-08-08 00:10:11', '1011'),
         	('1', '2014-08-09 10:12:02', '101202'),
         	('1', '2014-08-10 14:01:02', '140102'),
         	('1', '2014-08-11 18:01:02', '180102'),
         	('1', '2014-08-12 12:01:57', '120157'),
         	('2', '2014-08-09 07:55:09', '075509'),
         	('2', '2014-08-10 08:16:17', '081617'),
         	('2', '2014-08-11 16:01:17', '160117'),
         	('2', '2014-08-12 00:01:02', '0.123678'),
         	('3', '2014-08-09 09:42:19', '12.777'),
         	('3', '2014-08-10 23:59:59', '1.987654321'),
         	('3', '2014-08-11 15:16:17', '12345');
