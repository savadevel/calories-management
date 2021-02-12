package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(m -> save(m.getUserId(), m));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        if (meal.isNew()) {
            log.info("create userId={}, meal={}", userId, meal);
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        log.info("update userId={}, meal={}", userId, meal);
        AtomicBoolean isUpdated = new AtomicBoolean(false);
        // handle case: update, but not present in storage
        repository.computeIfPresent(meal.getId(), (id, oldMeal) -> {
            if (oldMeal.getUserId() != userId) {
                log.error("can't update meal={{}} for userId={}", meal, userId);
                return null;
            }
            isUpdated.set(true);
            return meal;
        });
        return isUpdated.get() ? meal : null;
    }

    @Override
    public boolean delete(int userId, int mealId) {
        log.info("delete mealId={} for userId={}", mealId, userId);
        AtomicBoolean isDeleted = new AtomicBoolean(false);
        repository.computeIfPresent(mealId, (id, meal) -> {
            if (meal.getUserId() == userId) {
                isDeleted.set(true);
            } else {
                log.error("can't delete meal={{}} for userId={}", meal, userId);
            }
            return null;
        });
        return isDeleted.get();
    }

    @Override
    public Meal get(int userId, int mealId) {
        log.info("get mealId={} for userId={}", mealId, userId);
        Meal result = repository.get(mealId);
        return result.getUserId() == userId ? result : null;
    }

    @Override
    public Collection<Meal> getAll(int userId, LocalDate startDate, LocalDate endDate) {
        log.info("getAll userId={} startDate={} endDate={}", userId, startDate, endDate);
        return repository.values().stream()
                .filter(m -> m.getUserId() == userId && m.getDate().compareTo(startDate) >= 0 && m.getDate().compareTo(endDate) <= 0)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

