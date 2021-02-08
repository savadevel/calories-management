package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.RepositoryCrud;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.dao.MealsInMemoryRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.TimeUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final String PAGE_OF_MEALS = "meals.jsp";
    private static final String PAGE_OF_MEAL = "meal.jsp";
    private static final String SERVLET_OF_MEALS = "meals";
    private static final RepositoryCrud<Meal> repositoryOfMeals = MealsInMemoryRepository.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        log.debug("request meals by get");
        try {
            switch (getAction(request)) {
                case LIST:
                    log.debug("request show list of meals");
                    onList(request);
                    request.getRequestDispatcher(PAGE_OF_MEALS).forward(request, response);
                    break;
                case EDIT_CREATE:
                    log.debug("request show edit page for creat meal");
                    onEdit(request, ActionWithEntity.CREATE, new Meal(0, LocalDateTime.now(), "", 0));
                    request.getRequestDispatcher(PAGE_OF_MEAL).forward(request, response);
                    break;
                case EDIT_UPDATE:
                    log.debug("request show edit page for update meal (id={})", request.getParameter("id"));
                    onEdit(request, ActionWithEntity.UPDATE, repositoryOfMeals.getById(getIdMeal(request)));
                    request.getRequestDispatcher(PAGE_OF_MEAL).forward(request, response);
                    break;
                case DELETE:
                    log.debug("request delete meal (id={})", request.getParameter("id"));
                    repositoryOfMeals.delete(Integer.parseInt(request.getParameter("id")));
                    onList(request);
                    response.sendRedirect(SERVLET_OF_MEALS);
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("request meals by post");
        request.setCharacterEncoding("UTF-8");
        try {
            switch (getAction(request)) {
                case UPDATE: {
                    log.debug("update meal (id={}, dateTime={}, description={}, calories={})",
                            request.getParameter("id"),
                            request.getParameter("dateTime"),
                            request.getParameter("description"),
                            request.getParameter("calories"));
                    onCreateOrUpdate(request, repositoryOfMeals::update);
                    break;
                }
                case CREATE: {
                    log.debug("create meal (dateTime={}, description={}, calories={})",
                            request.getParameter("dateTime"),
                            request.getParameter("description"),
                            request.getParameter("calories"));

                    onCreateOrUpdate(request, (meal) -> {
                        repositoryOfMeals.create(meal);
                        log.debug("created meal (id={})", meal.getId());
                    });
                    break;
                }
            }
            response.sendRedirect(SERVLET_OF_MEALS);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void onList(HttpServletRequest request) {
        request.setAttribute("meals", MealsUtil.getMealsToPerDay(repositoryOfMeals.getAll()));
        request.setAttribute("dateTimeFormatter", TimeUtil.DATE_TIME_FORMATTER_FOR_MEALS);
    }

    private void onEdit(HttpServletRequest request, ActionWithEntity action, Meal meal)  {
        request.setAttribute("meal", meal);
        request.setAttribute("action", action.name());
        request.setAttribute("dateTimeFormatter", TimeUtil.DATE_TIME_FORMATTER_FOR_MEAL);
    }

    private void onCreateOrUpdate(HttpServletRequest request, Consumer<Meal> consumer) {
        Meal meal = new Meal(
                getIdMeal(request),
                LocalDateTime.parse(request.getParameter("dateTime"), TimeUtil.DATE_TIME_FORMATTER_FOR_MEAL),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        consumer.accept(meal);
    }

    private int getIdMeal(HttpServletRequest request) {
        return request.getParameter("id") == null ? 0 : Integer.parseInt(request.getParameter("id"));
    }

    private ActionWithEntity getAction(HttpServletRequest request) {
        return request.getParameter("action") == null ?
                ActionWithEntity.LIST :
                ActionWithEntity.valueOf(request.getParameter("action"));
    }
}
