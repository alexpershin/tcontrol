package com.controller;

import org.junit.jupiter.api.Test;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class ControllerTest {
    @Test
    public void parseDateTimeTest(){
        String s="2025-11-07T01:27:00.000+03:00";
        String isoString = "2023-12-25T10:15:30+05:30";
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        OffsetDateTime r = OffsetDateTime.parse(s, formatter);
       // DateTimeFormat.ISO.DATE_TIME.
    }
}
