DROP SCHEMA IF EXISTS dbtcontrol;

CREATE SCHEMA dbtcontrol;

CREATE TABLE dbtcontrol.sensors(
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR NOT NULL,
    type VARCHAR NOT NULL,
    description VARCHAR DEFAULT NULL,
    low_thresshold DOUBLE,
    high_thresshold DOUBLE,
    threshold_delta DOUBLE,
    serial_number VARCHAR NOT NULL UNIQUE
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
