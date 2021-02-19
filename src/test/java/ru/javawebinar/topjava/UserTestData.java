package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.AbstractBaseEntity;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class UserTestData {
    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int USER_MEAL_ID = START_SEQ + 2;
    public static final int NOT_FOUND = 10;

    public static final User user = new User(USER_ID, "User", "user@yandex.ru", "password", Role.USER);
    public static final User admin = new User(ADMIN_ID, "Admin", "admin@gmail.com", "admin", Role.ADMIN);
    public static final Meal meal = new Meal(USER_MEAL_ID, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "1-Завтрак", 500);

    public static final List<Meal> meals = Arrays.asList(
            new Meal(START_SEQ + 2, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "1-Завтрак", 500),
            new Meal(START_SEQ + 3, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "1-Обед", 1000),
            new Meal(START_SEQ + 4, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "1-Ужин", 500),
            new Meal(START_SEQ + 5, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "1-Еда на граничное значение", 100),
            new Meal(START_SEQ + 6, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "1-Завтрак", 1000),
            new Meal(START_SEQ + 7, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "1-Обед", 500),
            new Meal(START_SEQ + 8, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "1-Ужин", 410)
    );

    private static List<String> ignoringFields = null;

    public static User getNewUser() {
        return new User(null, "New", "new@gmail.com", "newPass", 1555, false, new Date(), Collections.singleton(Role.USER));
    }

    public static User getUpdatedUser() {
        User updated = new User(user);
        updated.setEmail("update@gmail.com");
        updated.setName("UpdatedName");
        updated.setCaloriesPerDay(330);
        updated.setPassword("newPass");
        updated.setEnabled(false);
        updated.setRoles(Collections.singletonList(Role.ADMIN));
        return updated;
    }

    public static Meal getNewMeal() {
        return new Meal(null, LocalDateTime.of(2021, Month.JANUARY, 30, 10, 0), "1-Завтрак", 500);
    }

    public static Meal getUpdatedMeal() {
        Meal updated = new Meal(meal);
        updated.setDateTime(LocalDateTime.of(2021, Month.JANUARY, 30, 10, 0));
        updated.setDescription("UpdatedDescription");
        updated.setCalories(1000);
        return updated;
    }

    public static <T extends AbstractBaseEntity> void assertMatch(T actual, T expected) {
        assertThat(actual).usingRecursiveComparison().ignoringFields(getIgnoringFields()).isEqualTo(expected);
    }

    @SafeVarargs
    public static <T extends AbstractBaseEntity> void assertMatch(Iterable<T> actual, T... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static <T extends AbstractBaseEntity> void assertMatch(Iterable<T> actual, Iterable<T> expected) {
        assertThat(actual).usingElementComparatorIgnoringFields(getIgnoringFields()).isEqualTo(expected);
    }

    public static void setIgnoringFields(String ... ignoringFields) {
        UserTestData.ignoringFields = Arrays.asList(ignoringFields);
    }

    private static String[] getIgnoringFields() {
        return ignoringFields == null ? new String[0] : ignoringFields.toArray(new String[0]);
    }
}
