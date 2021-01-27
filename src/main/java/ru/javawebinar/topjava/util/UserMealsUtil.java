package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        System.out.println(filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));

        // Optional:
        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));

        // Optional 2:
        System.out.println(filteredByOneCycleOnThreads(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(filteredByOneCycleOnLambda(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(filteredByOneStream(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay) {

        List<UserMealWithExcess> result = new ArrayList<>();
        Map<LocalDate, Integer> excessPerDay = new HashMap<>();

        // first cycle for collect calories by date
        meals.forEach(meal -> excessPerDay.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum));

        // last cycle for collect meals by time and mark excess meals by caloriesPerDay
        meals.forEach(meal -> {
                    if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                        result.add(createUserMealWithExcess(meal, isExcess(excessPerDay, meal, caloriesPerDay)));
                    }
                }
        );

        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay) {

        // first stream for collect calories by date
        Map<LocalDate, Integer> excessPerDay = meals
                .stream()
                .collect(Collectors.toMap(
                        meal -> meal.getDateTime().toLocalDate(),
                        UserMeal::getCalories,
                        Integer::sum
                ));

        // last stream for collect meals by time and mark excess meals by caloriesPerDay
        return meals
                .stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> createUserMealWithExcess(meal, isExcess(excessPerDay, meal, caloriesPerDay)))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByOneCycleOnThreads(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay) {

        final List<UserMealWithExcess> result = new ArrayList<>();
        final Map<LocalDate, Integer> excessPerDay = new HashMap<>();

        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // unlock after complete excessPerDay and create pool threads
        synchronized (result) {
            // one cycle
            meals.forEach(meal -> {
                        excessPerDay.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);

                        if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                            pool.submit(() -> {
                                synchronized (result) {
                                    result.add(createUserMealWithExcess(
                                            meal,
                                            isExcess(excessPerDay, meal, caloriesPerDay)));
                                }
                            });
                        }
                    }
            );
        }

        // execute consistently thread from pool
        pool.shutdown();

        try {
            return pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MICROSECONDS) ? result : null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public static List<UserMealWithExcess> filteredByOneCycleOnLambda(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay) {

        final List<UserMealWithExcess> result = new ArrayList<>();
        final Map<LocalDate, Integer> excessPerDay = new HashMap<>();

        Predicate<Boolean> chainOfMeals = (meal) -> true;

        for (UserMeal meal : meals) {
            excessPerDay.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);

            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                chainOfMeals = chainOfMeals.and((foo) -> result.add(createUserMealWithExcess(
                        meal,
                        isExcess(excessPerDay, meal, caloriesPerDay))));
            }
        }

        chainOfMeals.test(true);

        return result;
    }

    public static List<UserMealWithExcess> filteredByOneStream(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime, int caloriesPerDay) {

        return meals.stream().parallel().collect(getUserMealWithExcessCollector(startTime, endTime, caloriesPerDay));
    }

    // custom collector for filteredByStreams2
    private static Collector<UserMeal, ?, List<UserMealWithExcess>> getUserMealWithExcessCollector(
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay) {

        return Collector.<UserMeal, Map.Entry<Map<LocalDate, Integer>, List<UserMeal>>, List<UserMealWithExcess>>of(

                // supplier use entry for collect data:
                // new HashMap<>() - excess per day
                // new ArrayList<>() - meals by time
                () -> new AbstractMap.SimpleImmutableEntry<>(new HashMap<>(), new ArrayList<>()),

                // accumulator calculate excess per day and filter meals by time
                // e - excess per day
                // m - meal
                (e, m) -> {
                    e.getKey().merge(m.getDateTime().toLocalDate(), m.getCalories(), Integer::sum);

                    if (TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime))
                        e.getValue().add(m);
                },


                // combiner for parallel stream (union parts)
                // e1 - first part excess per day
                // e1 - second part excess per day
                (e1, e2) -> {

                    e1.getValue().addAll(e2.getValue());

                    for (Map.Entry<LocalDate, Integer> e : e2.getKey().entrySet()) {
                        e1.getKey().merge(e.getKey(), e.getValue(), Integer::sum);
                    }

                    return e1;
                },

                // finisher create list of user meals with excess
                c -> c.getValue()
                        .stream()
                        .map(meal -> createUserMealWithExcess(meal, isExcess(c.getKey(), meal, caloriesPerDay)))
                        .collect(Collectors.toList())
        );
    }

    private static UserMealWithExcess createUserMealWithExcess(UserMeal meal, boolean excess) {
        return new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
    }

    private static boolean isExcess(Map<LocalDate, Integer> excessPerDay, UserMeal meal, int caloriesPerDay) {
        return excessPerDay.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
    }
}
