package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

class MealRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = MealRestController.REST_URL + "/";

    @Autowired
    private MealService mealService;

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_TO_MATCHER.contentJson(MealsUtil.getTos(meals, SecurityUtil.authUserCaloriesPerDay())));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "delete/" + MEAL1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        MEAL_MATCHER.assertMatch(mealService.getAll(USER_ID), meal7, meal6, meal5, meal4, meal3, meal2);
    }

    @Test
    void update() throws Exception {
        Meal meal = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + "update/" + MEAL1_ID).contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(meal)))
                .andDo(print())
                .andExpect(status().isNoContent());
        MEAL_MATCHER.assertMatch(mealService.get(MEAL1_ID, USER_ID), meal);
    }

    @Test
    void create() throws Exception {
        Meal meal = getNew();
        perform(MockMvcRequestBuilders.post(REST_URL + "create").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(meal)))
                .andDo(print())
                .andDo(result -> meal.setId(JsonUtil.readValue(result.getResponse().getContentAsString(), Meal.class).id()))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_MATCHER.contentJson(meal));
    }

    @Test
    void getBetween() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("startDate", "2020-01-30")
                .param("endDate", "2020-01-30")
                .param("startTime", "00:00:00")
                .param("endDate", "23:00:00"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_TO_MATCHER.contentJson(MealsUtil.getTos(List.of(meal3, meal2, meal1), SecurityUtil.authUserCaloriesPerDay())));
    }
}