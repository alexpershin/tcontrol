DROP DATABASE IF EXISTS dbtcontrol; 

CREATE DATABASE IF NOT EXISTS dbtcontrol; -- IF NOT EXISTS;


CREATE TABLE IF NOT EXISTS dbtcontrol.sensors (
    sensor_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sensor_name VARCHAR(1024)
);
 

CREATE TABLE dbtcontrol.sensorValues (
    sensor_id BIGINT UNSIGNED,
    value_datetime DATETIME DEFAULT NULL,
    value_figure DOUBLE,
    FOREIGN KEY (sensor_id)
        REFERENCES dbtcontrol.sensors (sensor_id)
);
 

CREATE TABLE dbtcontrol.users (
    user_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_email VARCHAR(256),
    user_login VARCHAR(256),
    user_password VARCHAR(256),
    user_name VARCHAR(256),
    user_surname VARCHAR(256),
    user_role INT(1),
    user_accost VARCHAR(128)
);

 

CREATE TABLE dbtcontrol.userRoles (
    user_role_id INT,
    user_role VARCHAR(256)
); 


INSERT INTO dbtcontrol.userRoles (user_role_id, user_role) 
    VALUES ('1', 'User'),
           ('2', 'SuperUser'),
           ('3', 'Admin');


INSERT INTO dbtcontrol.users (user_email, user_login, user_password, user_name, user_surname, user_role, user_accost)
       VALUES  	('Alex.Pershin@gmail.com', 'alex', 'TheSnake', 'Alex', 'Pershin', '3', 'Mr. '),
         	('buslavskii@gmail.com', 'anton', 'TheBeast', 'Anton', 'Buslavskii', '3', 'Sir '),
         	('test@gmail.com', 'test', 'TheTest', 'Test', 'Testov', '1', 'Mrs. '),
         	('test1@gmail.com', 'test1', 'TheTest1', 'Test1', 'Testov1', '2', 'Ms. '),
         	('test2@gmail.com', 'test2', 'TheTest2', 'Test2', 'Testov2', '1', 'Dearest Mr. ');


INSERT INTO dbtcontrol.sensors (sensor_name)
       VALUES  	('Fire Sensor'),
         	('Glass Break Sensor'),
         	('On or Off sensor'),
         	('Light Sensor');


INSERT INTO dbtcontrol.sensorValues (sensor_id, value_datetime, value_figure)
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
