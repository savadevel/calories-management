package ru.javawebinar.topjava.dao;

import java.util.List;

public interface RepositoryCrud <T> {
    List<T> getAll();
    T getById(int id);
    T save(T entity);
    void delete(int id);
}
