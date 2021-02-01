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
import java.util.stream.Stream;

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
        System.out.println(filteredByOneStreamOnMyCollector(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(filteredByOneStreamOnCollectors(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDate = new HashMap<>();
        // first cycle for collect calories by date
        meals.forEach(meal -> caloriesByDate.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum));

        List<UserMealWithExcess> result = new ArrayList<>();
        // last cycle for collect meals by time and mark excess meals by caloriesPerDay
        meals.forEach(meal -> {
                    if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                        result.add(createUserMealWithExcess(meal,caloriesByDate.get(meal.getDateTime().toLocalDate()) > caloriesPerDay));
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
        Map<LocalDate, Integer> caloriesByDate = meals.stream()
                .collect(Collectors.toMap(
                        meal -> meal.getDateTime().toLocalDate(),
                        UserMeal::getCalories,
                        Integer::sum
                ));
        // last stream for collect meals by time and mark excess meals by caloriesPerDay
        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> createUserMealWithExcess(meal,caloriesByDate.get(meal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByOneCycleOnThreads(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay) {
        final List<UserMealWithExcess> result = new ArrayList<>();
        final Map<LocalDate, Integer> caloriesByDate = new HashMap<>();
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // unlock after complete caloriesByDate and create pool threads
        synchronized (result) {
            // one cycle
            meals.forEach(meal -> {
                        caloriesByDate.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
                        if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                            pool.submit(() -> {
                                synchronized (result) {
                                    result.add(createUserMealWithExcess(meal, caloriesByDate.get(meal.getDateTime().toLocalDate()) > caloriesPerDay));
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
        final Map<LocalDate, Integer> caloriesByDate = new HashMap<>();
        Predicate<Boolean> builderResult = (meal) -> true;

        for (UserMeal meal : meals) {
            caloriesByDate.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                builderResult = builderResult.and(prev -> result.add(createUserMealWithExcess(meal, caloriesByDate.get(meal.getDateTime().toLocalDate()) > caloriesPerDay)));
            }
        }
        builderResult.test(true);
        return result;
    }

    public static List<UserMealWithExcess> filteredByOneStreamOnMyCollector(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime, int caloriesPerDay) {
        return meals.parallelStream()
                .sequential()
                .collect(getUserMealWithExcessCollector(startTime, endTime, caloriesPerDay));
    }

    // custom collector for filteredByStreams2
    private static Collector<UserMeal, ?, List<UserMealWithExcess>> getUserMealWithExcessCollector(
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay) {
        return Collector.<UserMeal, Map.Entry<Map<LocalDate, Integer>, List<UserMeal>>, List<UserMealWithExcess>>of(
                // entry for collect data:
                // new HashMap<>() - calories by date
                // new ArrayList<>() - meals on date
                () -> new AbstractMap.SimpleImmutableEntry<>(new HashMap<>(), new ArrayList<>()),
                // calculate excess per day and filter meals by time
                // c - collect calories by date and meals
                // m - next meal
                (c, m) -> {
                    c.getKey().merge(m.getDateTime().toLocalDate(), m.getCalories(), Integer::sum);
                    if (TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime)) {
                        c.getValue().add(m);
                    }
                },
                // c1 - first part collect calories by date and meals
                // c1 - second part collect calories by date and meals
                (c1, c2) -> {
                    c1.setValue(
                            Stream
                                    .concat(c1.getValue().stream(), c2.getValue().stream())
                                    .collect(Collectors.toList()));
                    for (Map.Entry<LocalDate, Integer> e : c2.getKey().entrySet()) {
                        c1.getKey().merge(e.getKey(), e.getValue(), Integer::sum);
                    }
                    return c1;
                },
                // full collect calories by date and suitable meals
                c -> c.getValue()
                        .stream()
                        .map(meal -> createUserMealWithExcess(meal, c.getKey().get(meal.getDateTime().toLocalDate()) > caloriesPerDay))
                        .collect(Collectors.toList())
        );
    }

    public static List<UserMealWithExcess> filteredByOneStreamOnCollectors(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay) {
        class HelperCollectCaloriesByDate {
            private int calories;
            private final LocalDate date;

            public HelperCollectCaloriesByDate(UserMeal meal) {
                date = meal.getDateTime().toLocalDate();
                calories = meal.getCalories();
            }

            public void add(HelperCollectCaloriesByDate helper) {
                calories += helper.calories;
            }

            @Override
            public boolean equals(Object other) {
                if (this == other) return true;
                if (other == null || getClass() != other.getClass()) return false;
                HelperCollectCaloriesByDate helperOther = (HelperCollectCaloriesByDate) other;
                // check equals only date for grouping calories by date
                if (Objects.equals(date, helperOther.date)) {
                    // increase calories in other because it already in HashMap (below Collectors.groupingBy)
                    helperOther.add(this);
                    return true;
                }
                return false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(date);
            }
        }

        return meals.parallelStream()
                .collect(Collectors.collectingAndThen(
                        // grouping by date and calculate calories by date,
                        // if next meal has same date (HelperCollectCaloriesByDate.equal)
                        Collectors.groupingBy(HelperCollectCaloriesByDate::new),
                        // HashMap
                        // key - calories on date (HelperCollectCaloriesByDate)
                        // value - meals on date (List)
                        mealsOnDays -> mealsOnDays.entrySet().stream()
                                .flatMap(mealsOnDay -> mealsOnDay
                                        .getValue()
                                        .stream()
                                        .filter(m -> TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime))
                                        .map(m -> createUserMealWithExcess(m, mealsOnDay.getKey().calories > caloriesPerDay))
                                )
                                .collect(Collectors.toList())
                ));
    }

    private static UserMealWithExcess createUserMealWithExcess(UserMeal meal, boolean excess) {
        return new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
    }
}
