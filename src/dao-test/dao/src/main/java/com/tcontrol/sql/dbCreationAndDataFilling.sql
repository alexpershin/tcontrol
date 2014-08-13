-- return this script inside the project
DROP DATABASE dbTcontrol IF EXISTS;

CREATE DATABASE dbTcontrol; -- IF NOT EXISTS;

CREATE TABLE sensors (
Id bigint unsigned unique auto_increment PRIMARY KEY,
sensorName varchar(1024)
);

CREATE TABLE sensorValues (
sensorId bigint unsigned, 
valueDateTime datetime default null,
valueFigure double,
FOREIGN KEY (sensorId) references sensors(Id)
); 

CREATE TABLE users (
userId bigint unsigned auto_increment, 
userEmail varchar (256),
userLogin varchar (256),
userPassword varchar (256), 
userName varchar (256),
userSurname varchar (256),
userRole int (1),
userAccost varchar (128)
); 


CREATE TABLE userRoles (
userRoleId int, 
userRole varchar (256)
); 


INSERT into userRoles (userRoleId, userRoleName) VALUES 
('1', 'User'),
('2', 'SuperUser'),
('3', 'Admin');


INSERT INTO users (userEmail, userLogin, userPassword, userName, userSurname, userRole)
       VALUES  	('Alex.Pershin@gmail.com', 'alex', 'TheSnake', 'Alex', 'Pershin', '3'),
         	('buslavskii@gmail.com', 'anton', 'TheBeast', 'Anton', 'Buslavskii', '3'),
         	('test@gmail.com', 'test', 'TheTest', 'Test', 'Testov', '1'),
         	('test1@gmail.com', 'test1', 'TheTest1', 'Test1', 'Testov1', '2'),
         	('test2@gmail.com', 'test2', 'TheTest2', 'Test2', 'Testov2', '1');


INSERT INTO sensors (sensorName)
       VALUES  	('Fire Sensor'),
         	('Glass Break Sensor'),
         	('On or Off sensor'),
         	('Light Sensor');


INSERT INTO sensorValues (sensorId, valueDateTime, valueFigure)
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
