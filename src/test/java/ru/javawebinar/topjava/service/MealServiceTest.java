package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.Util;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.UserTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal actualMeal = service.get(USER_MEAL_ID, USER_ID);
        assertMatch(actualMeal, meal);
    }

    @Test
    public void getMealOtherUser() {
        assertThrows(NotFoundException.class, () -> service.get(USER_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, NOT_FOUND));
    }

    @Test
    public void delete() {
        service.delete(USER_MEAL_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(USER_MEAL_ID, USER_ID));
    }

    @Test
    public void deletedNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, NOT_FOUND));
    }

    @Test
    public void deleteMealOtherUser() {
        assertThrows(NotFoundException.class, () -> service.delete(USER_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate startDate = LocalDate.of(2020, Month.JANUARY, 31);
        LocalDate endDate = LocalDate.of(2021, Month.JANUARY, 31);
        List<Meal> filtered = service.getBetweenInclusive(startDate, endDate, USER_ID);
        assertMatch(filtered, meals.stream()
                .filter(meal -> Util.isBetweenHalfOpen(meal.getDate(), startDate, endDate))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList()));
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, meals.stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList()));
    }

    @Test
    public void update() {
        Meal updated = getUpdatedMeal();
        service.update(updated, USER_ID);
        assertMatch(service.get(updated.getId(), USER_ID), getUpdatedMeal());
    }

    @Test
    public void updateMealOtherUser() {
        Meal updated = getUpdatedMeal();
        assertThrows(NotFoundException.class, () -> service.update(updated, ADMIN_ID));
    }

    @Test
    public void create() {
        Meal created = service.create(getNewMeal(), USER_ID);
        Meal newMeal = getNewMeal();
        newMeal.setId(created.getId());
        assertMatch(created, newMeal);
        assertMatch(service.get(created.getId(), USER_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() {
        service.create(getNewMeal(), USER_ID);
        assertThrows(DuplicateKeyException.class, () -> service.create(getNewMeal(), USER_ID));
    }
}