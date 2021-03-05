package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.Meal;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles({"datajpa"})
public class DataJpaMealServiceTest extends MealServiceTest {
    @Test
    public void getWithUser() {
        Meal actual = service.getWithUser(MEAL1_ID, USER_ID);
        MEAL_MATCHER.assertMatch(actual, meal1);
        USER_MATCHER.assertMatch(actual.getUser(), user);
    }
}
