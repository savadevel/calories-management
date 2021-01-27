package ru.javawebinar.topjava.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.util.UserMealsUtil;

import javax.swing.text.Utilities;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx5G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class FilterUserMealBenchmark {

    @Param({"1000"})
    private int N;

    private static final List<UserMeal> TEMPLATE = Arrays.asList(
            new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
            new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
    );

    private List<UserMeal> DATA__FOR__TESTING;

    public static void main(String[] args) throws IOException, RunnerException {
        org.openjdk.jmh.Main.main(args);
    }

    @Setup
    public void setup() {
        DATA__FOR__TESTING = createData();
    }

    @Benchmark
    public void filteredByCycles() {
        UserMealsUtil.filteredByCycles(DATA__FOR__TESTING, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
    }

    @Benchmark
    public void filteredByStreams() {
        UserMealsUtil.filteredByStreams(DATA__FOR__TESTING, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
    }

    @Benchmark
    public void filteredByOneStream() {
        UserMealsUtil.filteredByOneStream(DATA__FOR__TESTING, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
    }

    @Benchmark
    public void filteredByOneCycleOnLambda() {
        UserMealsUtil.filteredByOneCycleOnLambda(DATA__FOR__TESTING, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
    }

    @Benchmark
    public void filteredByOneCycleOnThreads() {
        UserMealsUtil.filteredByOneCycleOnThreads(DATA__FOR__TESTING, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
    }

    private List<UserMeal> createData() {
        List<UserMeal> data = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            final int addDays = i;
            data.addAll(
                    TEMPLATE
                            .stream()
                            .map(it -> new UserMeal(
                                    it.getDateTime().plusDays(addDays),
                                    it.getDescription(),
                                    it.getCalories()))
                            .collect(Collectors.toList()));
        }

        return data;
    }
}

// Benchmark                                             (N)  Mode  Cnt  Score   Error  Units
//FilterUserMealBenchmark.filteredByCycles             1000  avgt   10  0,246 ± 0,019  ms/op
//FilterUserMealBenchmark.filteredByOneCycleOnLambda   1000  avgt   10  0,259 ± 0,007  ms/op
//FilterUserMealBenchmark.filteredByOneCycleOnThreads  1000  avgt   10  2,003 ± 0,155  ms/op
//FilterUserMealBenchmark.filteredByOneStream          1000  avgt   10  0,312 ± 0,056  ms/op
//FilterUserMealBenchmark.filteredByStreams            1000  avgt   10  0,261 ± 0,014  ms/op
