package ru.javawebinar.topjava.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class StringToLocalTime implements Converter<String, LocalTime> {

    public LocalTime convert(@Nullable String text) {
        return StringUtils.hasLength(text) ? LocalTime.parse(text, DateTimeFormatter.ISO_LOCAL_TIME) : null;
    }
}