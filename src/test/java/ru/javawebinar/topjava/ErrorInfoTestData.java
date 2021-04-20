package ru.javawebinar.topjava;

import ru.javawebinar.topjava.util.exception.ErrorInfo;
import ru.javawebinar.topjava.util.exception.ErrorType;

import java.util.List;

import static ru.javawebinar.topjava.MealTestData.MEAL1_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

public class ErrorInfoTestData {
    public static TestMatcher<ErrorInfo> ERROR_INFO_MATCHER = TestMatcher.usingEqualsComparator(ErrorInfo.class);

    public static final ErrorInfo enErrorInfoByCreateMealWithInvalidCalories = new ErrorInfo("http://localhost/rest/profile/meals/", ErrorType.VALIDATION_ERROR, List.of("[calories] must be between 10 and 5000"));
    public static final ErrorInfo enErrorInfoByUpdateMealWithInvalidCalories = new ErrorInfo("http://localhost/rest/profile/meals/" + MEAL1_ID, ErrorType.VALIDATION_ERROR, List.of("[calories] must be between 10 and 5000"));

    public static final ErrorInfo enErrorInfoByCreateUserWithInvalidEmail = new ErrorInfo("http://localhost/rest/admin/users/", ErrorType.VALIDATION_ERROR, List.of("[email] must not be blank"));
    public static final ErrorInfo enErrorInfoByUpdateUserWithInvalidEmail = new ErrorInfo("http://localhost/rest/admin/users/" + USER_ID, ErrorType.VALIDATION_ERROR, List.of("[email] must not be blank"));
    public static final ErrorInfo enErrorInfoByCreateUserWithExistsEmail = new ErrorInfo("http://localhost/rest/admin/users/", ErrorType.VALIDATION_ERROR, List.of("[email] User with this email already exists"));
    public static final ErrorInfo enErrorInfoByUpdateUserWithExistsEmail = new ErrorInfo("http://localhost/rest/admin/users/" + USER_ID, ErrorType.VALIDATION_ERROR, List.of("[email] User with this email already exists"));

    public static final ErrorInfo enErrorInfoByRegisterUserWithInvalidEmail = new ErrorInfo("http://localhost/rest/profile/register", ErrorType.VALIDATION_ERROR, List.of("[email] must not be blank"));
    public static final ErrorInfo enErrorInfoByUpdateRegisteredUserWithInvalidEmail = new ErrorInfo("http://localhost/rest/profile", ErrorType.VALIDATION_ERROR, List.of("[email] must not be blank"));
    public static final ErrorInfo enErrorInfoByRegisterUserWithExistsEmail = new ErrorInfo("http://localhost/rest/profile/register", ErrorType.VALIDATION_ERROR, List.of("[email] User with this email already exists"));
    public static final ErrorInfo enErrorInfoByUpdateRegisteredUserWithExistsEmail = new ErrorInfo("http://localhost/rest/profile", ErrorType.VALIDATION_ERROR, List.of("[email] User with this email already exists"));
}
