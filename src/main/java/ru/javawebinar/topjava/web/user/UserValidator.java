package ru.javawebinar.topjava.web.user;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.LanguageUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Component
public class UserValidator implements Validator{
    private final static String USER_EMAIL_FIELD = "email";
    private final static String USER_EMAIL_ERROR_MESSAGE = "user.emailExistError";

    private final LocalValidatorFactoryBean validator;
    private final UserService service;
    private final LanguageUtil languageUtil;

    private final Map<Class<?>, BiFunction<Object, UserService, Boolean>> validateEmailByUserClass = new HashMap<>(){{
        put(User.class, (t, s) -> {
            User user = (User) t;
            User persistentUser = s.getByEmail(user.getEmail());
            return persistentUser != null && (user.isNew() || !persistentUser.getId().equals(user.getId()));
        });

        put(UserTo.class, (t, s) -> {
            AuthorizedUser authorizedUser = SecurityUtil.safeGet();
            User persistentUser = s.getByEmail(((UserTo) t).getEmail());
            return persistentUser != null && (authorizedUser == null || !persistentUser.getId().equals(authorizedUser.getId()))  ;
        });
    }};

    {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
    }

    public UserValidator(UserService service, LanguageUtil languageUtil) {
        this.service = service;
        this.languageUtil = languageUtil;
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return User.class.isAssignableFrom(clazz) || UserTo.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        validator.validate(target, errors);
        if (errors.getFieldErrors().stream().anyMatch(ef -> ef.getField().equals(USER_EMAIL_FIELD)))
            return;
        try {
            if (validateEmailByUserClass.get(target.getClass()).apply(target, service)) {
                errors.rejectValue(USER_EMAIL_FIELD, USER_EMAIL_ERROR_MESSAGE, languageUtil.getLocalizedMessage(USER_EMAIL_ERROR_MESSAGE));
            }
        } catch (NotFoundException ignored) {
        }
    }

}
