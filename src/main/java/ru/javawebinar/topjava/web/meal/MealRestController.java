package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;

@Controller
public class MealRestController {
    private final Logger log = LoggerFactory.getLogger(MealRestController.class);
    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll() {
        log.info("getAll");
        return service.getAll(SecurityUtil.authUserId());
    }

    public List<MealTo> getFiltered(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.info("getForPeriod userId={} startDate={} endDate={} startTime={} endTime={}", SecurityUtil.authUserId(), startDate, endDate, startTime, endTime);
        return service.getFiltered(SecurityUtil.authUserId(), startDate, endDate, startTime, endTime);
    }

    public Meal get(int mealId) {
        log.info("get userId={} mealId={}", SecurityUtil.authUserId(), mealId);
        return service.get(SecurityUtil.authUserId(), mealId);
    }

    public void delete(int mealId) {
        log.info("delete mealId={} for userId={}", mealId, SecurityUtil.authUserId());
        service.delete(SecurityUtil.authUserId(), mealId);
    }

    public void update(Meal meal, int mealId) {
        log.info("update userId={}, meal={}", SecurityUtil.authUserId(), meal);
        assureIdConsistent(meal, mealId);
        service.update(SecurityUtil.authUserId(), meal);
    }

    public Meal create(Meal meal) {
        log.info("create userId={}, meal={}", SecurityUtil.authUserId(), meal);
        return service.create(SecurityUtil.authUserId(), meal);
    }
}