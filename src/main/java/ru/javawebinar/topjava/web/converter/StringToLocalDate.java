package ru.javawebinar.topjava.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringToLocalDate implements Converter<String, LocalDate> {

    public LocalDate convert(@Nullable String text) {
        return StringUtils.hasLength(text) ? LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }
}