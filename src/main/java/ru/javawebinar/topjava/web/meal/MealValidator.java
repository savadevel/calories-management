package ru.javawebinar.topjava.web.meal;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.web.LanguageUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.List;

@Component
public class MealValidator implements Validator{
    private final static String MEAL_DATE_TIME_FIELD = "dateTime";
    private final static String MEAL_DATE_TIME_ERROR_MESSAGE = "meal.dateTimeExistsError";

    private final LocalValidatorFactoryBean validator;
    private final MealService service;
    private final LanguageUtil languageUtil;

    {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
    }

    public MealValidator(MealService service, LanguageUtil languageUtil) {
        this.service = service;
        this.languageUtil = languageUtil;
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return Meal.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        validator.validate(target, errors);
        if (errors.getFieldErrors().stream().anyMatch(ef -> ef.getField().equals(MEAL_DATE_TIME_FIELD)))
            return;
        Meal meal = (Meal) target;
        List<Meal> meals = service.getBetweenInclusive(meal.getDate(), meal.getDate(), SecurityUtil.authUserId());
        if (meals != null && meals.stream().anyMatch(m -> m.getDateTime().equals(meal.getDateTime()) && (meal.isNew() || !m.getId().equals(meal.getId()) ))) {
            errors.rejectValue(MEAL_DATE_TIME_FIELD, MEAL_DATE_TIME_ERROR_MESSAGE, languageUtil.getLocalizedMessage(MEAL_DATE_TIME_ERROR_MESSAGE));
        }
    }

}
