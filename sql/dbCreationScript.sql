CREATE DATABASE dbTcontrol IF NOT EXISTS;

CREATE TABLE sensors (
Id bigint unsigned unique auto_increment PRIMARY KEY,
sensorName varchar(1024)
);

CREATE TABLE sensorValues (
sensorId bigint unsigned, 
valueDateTime datetime default null,
valueFigure double,
FOREIGN KEY (sensorId) references sensors(Id)
)