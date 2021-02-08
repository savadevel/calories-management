package ru.javawebinar.topjava.dao;

import java.util.List;

public interface RepositoryCrud <T> {
    List<T> getAll();
    T getById(int id) throws IllegalArgumentException;
    T create(T entity) throws IllegalArgumentException;
    void update(T entity) throws IllegalArgumentException;
    void delete(int id) throws IllegalArgumentException;
}
