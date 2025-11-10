package org.tcontrol.service.impl;

import org.apache.commons.csv.CSVFormat;

public interface CSVOptions {
    public static final CSVFormat EXCEL = CSVFormat.EXCEL;
    public static final String SENSOR_ID = "sensorId";
    public static final String SENSOR_CODE = "sensorCode";
    public static final String TIMESTAMP = "timestamp";
    public static final String VALUE = "value";
}
