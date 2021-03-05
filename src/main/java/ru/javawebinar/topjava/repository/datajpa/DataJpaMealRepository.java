package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {
    private final CrudUserRepository crudUserRepository;
    private final CrudMealRepository crudMealRepository;

    public DataJpaMealRepository(CrudUserRepository crudUserRepository, CrudMealRepository crudRepository) {
        this.crudUserRepository = crudUserRepository;
        this.crudMealRepository = crudRepository;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        meal.setUser(crudUserRepository.getOne(userId));
        return meal.isNew()
                ? crudMealRepository.save(meal)
                : get(meal.id(), userId) == null
                ? null
                : crudMealRepository.save(meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        return crudMealRepository.deleteByIdAndUserId(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        return crudMealRepository.getMealByIdAndUserid(id, userId);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudMealRepository.getMealByUserIdOrderByDateTimeDesc(userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudMealRepository.getMealByDateTimeGreaterThanEqualAndDateTimeLessThanAndUserId(startDateTime, endDateTime, userId);
    }
}
