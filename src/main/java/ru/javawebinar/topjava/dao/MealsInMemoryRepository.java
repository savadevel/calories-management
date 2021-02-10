package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealsInMemoryRepository implements RepositoryCrud<Meal> {
    private final AtomicInteger nextId = new AtomicInteger(1);
    private final Map<Integer, Meal> storeOfMeals = new ConcurrentHashMap<>();

    public MealsInMemoryRepository() {
        MealsUtil.meals.forEach(this::save);
    }

    @Override
    public synchronized List<Meal> getAll() {
        return new ArrayList<>(storeOfMeals.values());
    }

    @Override
    public synchronized Meal getById(int id) {
        return storeOfMeals.get(id);
    }

    @Override
    public synchronized Meal save(Meal meal)  {
        if (meal.getId() == 0) {
            Meal result = new Meal(nextId.getAndIncrement(), meal.getDateTime(), meal.getDescription(), meal.getCalories());
            storeOfMeals.put(result.getId(), result);
            return result;
        }
        return storeOfMeals.computeIfPresent(meal.getId(), (k, v) -> meal);
    }

    @Override
    public void delete(int id) throws IllegalArgumentException {
        storeOfMeals.remove(id);
    }
}
