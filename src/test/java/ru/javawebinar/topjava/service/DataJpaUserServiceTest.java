package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.User;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles({"datajpa"})
public class DataJpaUserServiceTest extends UserServiceTest {
    @Test
    public void getWithUser() {
        User actual = service.getWithMeals(USER_ID);
        USER_MATCHER.assertMatch(actual, user);
        MEAL_MATCHER.assertMatch(actual.getMeals(), meals);
    }
}
