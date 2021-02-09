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

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final String PAGE_OF_MEALS = "meals.jsp";
    private static final String PAGE_OF_MEAL = "meal.jsp";
    private static final String SERVLET_OF_MEALS = "meals";
    private final RepositoryCrud<Meal> repositoryOfMeals = new MealsInMemoryRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.debug("request meals by get query");

        try {
            switch (getAction(request)) {
                case LIST:
                    onList(request, response);
                    break;
                case EDIT_CREATE:
                    onOpenFormCreate(request, response);
                    break;
                case EDIT_UPDATE:
                    onOpenFormUpdate(request, response);
                    break;
                case DELETE:
                    onDelete(request, response);
                    break;
                default:
                    response.sendRedirect(SERVLET_OF_MEALS);
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("request meals by post query");
        request.setCharacterEncoding("UTF-8");
        try {
            switch (getAction(request)) {
                case UPDATE:
                    onUpdate(request, response);
                    break;
                case CREATE:
                    onCreate(request, response);
                    break;
                default:
                    response.sendRedirect(SERVLET_OF_MEALS);
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }

    private void onList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("request list of meals");
        request.setAttribute("meals", MealsUtil.getMealsToPerDay(repositoryOfMeals.getAll(), MealsUtil.EXCESS_CALORIES_PER_DAY));
        request.setAttribute("dateTimeFormatter", TimeUtil.DATE_TIME_FORMATTER_FOR_MEALS);
        request.getRequestDispatcher(PAGE_OF_MEALS).forward(request, response);
    }

    private void onOpenFormCreate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("request show edit page for creat meal");
        request.setAttribute("meal", new Meal(0, LocalDateTime.now(), "", 0));
        request.setAttribute("action", ActionWithEntity.CREATE.name());
        request.setAttribute("dateTimeFormatter", TimeUtil.DATE_TIME_FORMATTER_FOR_MEAL);
        request.getRequestDispatcher(PAGE_OF_MEAL).forward(request, response);
    }

    private void onOpenFormUpdate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("request show edit page for update meal (id={})", request.getParameter("id"));
        Meal meal = repositoryOfMeals.getById(getIdMeal(request));
        if (meal == null) {
            response.sendRedirect(SERVLET_OF_MEALS);
            return;
        }
        request.setAttribute("meal", meal);
        request.setAttribute("action", ActionWithEntity.UPDATE.name());
        request.setAttribute("dateTimeFormatter", TimeUtil.DATE_TIME_FORMATTER_FOR_MEAL);
        request.getRequestDispatcher(PAGE_OF_MEAL).forward(request, response);
    }

    private void onDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("request delete meal (id={})", request.getParameter("id"));
        repositoryOfMeals.delete(getIdMeal(request));
        response.sendRedirect(SERVLET_OF_MEALS);
    }

    private void onCreate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("request create meal (dateTime={}, description={}, calories={})",
                request.getParameter("dateTime"),
                request.getParameter("description"),
                request.getParameter("calories"));
        Meal meal = repositoryOfMeals.save(getMealByAction(ActionWithEntity.CREATE, request));
        log.debug("created meal (id={})", meal.getId());
        response.sendRedirect(SERVLET_OF_MEALS);
    }

    private void onUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("request update meal (id={}, dateTime={}, description={}, calories={})",
                request.getParameter("id"),
                request.getParameter("dateTime"),
                request.getParameter("description"),
                request.getParameter("calories"));
        repositoryOfMeals.save(getMealByAction(ActionWithEntity.UPDATE, request));
        response.sendRedirect(SERVLET_OF_MEALS);
    }

    private int getIdMeal(HttpServletRequest request) {
        return request.getParameter("id") == null ? 0 : Integer.parseInt(request.getParameter("id"));
    }

    private ActionWithEntity getAction(HttpServletRequest request) {
        return request.getParameter("action") == null ?
                ActionWithEntity.LIST :
                ActionWithEntity.valueOf(request.getParameter("action"));
    }

    private Meal getMealByAction(ActionWithEntity action, HttpServletRequest request) {
        return new Meal(
                action == ActionWithEntity.CREATE ? 0 : getIdMeal(request),
                LocalDateTime.parse(request.getParameter("dateTime"), TimeUtil.DATE_TIME_FORMATTER_FOR_MEAL),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
    }
}
