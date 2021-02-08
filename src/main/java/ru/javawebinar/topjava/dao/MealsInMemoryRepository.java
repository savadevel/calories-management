package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealsInMemoryRepository implements RepositoryCrud<Meal> {
    private static final AtomicInteger nextId = new AtomicInteger(1);
    private static MealsInMemoryRepository instance = null;
    private final Map<Integer, Meal> mealsPerDay = new ConcurrentHashMap<>();

    public static MealsInMemoryRepository getInstance() {
        if (instance == null) {
            synchronized (MealsInMemoryRepository.class) {
                if (instance == null) {
                    instance = new MealsInMemoryRepository();
                }
            }
        }
        return instance;
    }

    private MealsInMemoryRepository() {
        MealsUtil.meals.forEach(this::create);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(mealsPerDay.values());
    }

    @Override
    public Meal getById(int id) throws IllegalArgumentException {
        return Optional.ofNullable(mealsPerDay.get(id)).orElseThrow(() -> new IllegalArgumentException("Meal not found with id=" + id));
    }

    @Override
    public Meal create(Meal meal) throws IllegalArgumentException {
        Meal result = new Meal(nextId.getAndIncrement(), meal.getDateTime(), meal.getDescription(), meal.getCalories());
        mealsPerDay.put(result.getId(), result);
        return result;
    }

    @Override
    public void update(Meal meal) throws IllegalArgumentException {
        mealsPerDay.put(meal.getId(), meal);
    }

    @Override
    public void delete(int id) throws IllegalArgumentException {
        mealsPerDay.remove(id);
    }
}
