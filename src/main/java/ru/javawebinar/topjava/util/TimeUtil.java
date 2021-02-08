package ru.javawebinar.topjava.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static final DateTimeFormatter DATE_TIME_FORMATTER_FOR_MEALS = DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_FOR_MEAL = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static boolean isBetweenHalfOpen(LocalTime lt, LocalTime startTime, LocalTime endTime) {
        return lt.compareTo(startTime) >= 0 && lt.compareTo(endTime) < 0;
    }
}
