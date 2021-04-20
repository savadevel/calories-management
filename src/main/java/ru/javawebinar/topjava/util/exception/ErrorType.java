package ru.javawebinar.topjava.util.exception;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorType {
    APP_ERROR("common.appError"),
    DATA_NOT_FOUND("common.dataNotFound"),
    DATA_ERROR("common.dataError"),
    VALIDATION_ERROR("common.validationError");

    private final String description;

    ErrorType(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}
