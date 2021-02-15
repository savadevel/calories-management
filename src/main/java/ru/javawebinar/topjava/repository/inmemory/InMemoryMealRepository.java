package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        save(1, new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак1", 500));
        save(1, new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед1", 1000));
        save(1, new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин1", 500));
        save(1, new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение1", 100));
        save(1, new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак1", 1000));
        save(1, new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед1", 500));
        save(1, new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин1", 410));
        save(2, new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак2", 500));
        save(2, new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед2", 1000));
        save(2, new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин2", 500));
        save(2, new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение2", 100));
        save(2, new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак2", 1000));
        save(2, new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед2", 500));
        save(2, new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин2", 410));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        if (meal.isNew()) {
            log.info("create userId={}, meal={}", userId, meal);
            meal.setId(counter.incrementAndGet());
            return repository.computeIfAbsent(userId, ConcurrentHashMap::new).put(meal.getId(), meal);
        }
        log.info("update userId={}, meal={}", userId, meal);
        Map<Integer, Meal> meals = repository.get(userId);
        return meals == null ? null : meals.computeIfPresent(meal.getId(), (k, v) -> meal);
    }

    @Override
    public boolean delete(int userId, int mealId) {
        log.info("delete mealId={} for userId={}", mealId, userId);
        Map<Integer, Meal> meals = repository.get(userId);
        return meals != null && meals.remove(mealId) != null;
    }

    @Override
    public Meal get(int userId, int mealId) {
        log.info("get mealId={} for userId={}", mealId, userId);
        Map<Integer, Meal> meals = repository.get(userId);
        return meals == null ? null : meals.get(mealId);
    }

    @Override
    public List<Meal> getAll(int userId) {
        log.info("getAll userId={}", userId);
        Map<Integer, Meal> meals = repository.get(userId);
        return meals == null
                ? Collections.emptyList()
                : meals.values().stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getForPeriod(int userId, LocalDate startDate, LocalDate endDate) {
        log.info("getForPeriod userId={} startDate={} endDate={}", userId, startDate, endDate);
        Map<Integer, Meal> meals = repository.get(userId);
        return meals == null
                ? Collections.emptyList()
                : meals.values().stream()
                .filter(m -> DateTimeUtil.isBetweenClosedInterval(m.getDate(), startDate, endDate))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

