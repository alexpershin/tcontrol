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

INSERT INTO dbtcontrol.sensors (
    id, name, type, low_thresshold, 
    high_thresshold,threshold_delta, description) 
VALUES
    (1, 'Indoor', 'TEMPERATURE', 10, 30, 2, 'Indoor Temperature Sensor'),
    (2, 'Outdoor', 'TEMPERATURE', -25, null, 5, 'Outdor Temperature Sensor'),
    (5, 'Power Voltage', 'VOLTAGE', 205, 255, 5, 'Power Grid Voltage');
