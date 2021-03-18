package ru.javawebinar.topjava.repository.jdbc;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

public interface ValidateEntity<T> {
    default void validate(Validator validator, T entity) {
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        if (violations.size() != 0) throw new ConstraintViolationException(violations);
    }
}
