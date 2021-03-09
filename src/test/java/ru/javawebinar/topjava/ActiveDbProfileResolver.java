package ru.javawebinar.topjava;

import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.util.ClassUtils;

import static ru.javawebinar.topjava.web.Profiles.*;

//http://stackoverflow.com/questions/23871255/spring-profiles-simple-example-of-activeprofilesresolver
public class ActiveDbProfileResolver implements ActiveProfilesResolver {
    //  Get DB profile depending of DB driver in classpath
    @Override
    public @NonNull
    String[] resolve(@NonNull Class<?> aClass) {
        return new String[]{getActiveDbProfile()};
    }
}
