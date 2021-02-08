<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="ru">
<head>
    <title>Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit meal</h2>
<form action="meals" method="post">
    <jsp:useBean id="meal" scope="request" type="ru.javawebinar.topjava.model.Meal"/>
    <jsp:useBean id="dateTimeFormatter" scope="request" type="java.time.format.DateTimeFormatter"/>
    <jsp:useBean id="action" scope="request" type="java.lang.String"/>
    <input type="hidden" id="action" name="action" value="${action}"/>
    <input type="hidden" id="id" name="id" value="${meal.id}"/>
    <table>
        <tr>
            <td><label for="dateTime">DateTime:</label></td>
            <td><input type="datetime-local" id="dateTime" name="dateTime" value="${meal.dateTime.format(dateTimeFormatter)}"/>
            </td>
        </tr>
        <tr>
            <td><label for="description">Description:</label></td>
            <td><input type="text" id="description" name="description" value="${meal.description}"/>
            </td>
        </tr>
        <tr>
            <td><label for="calories">Calories:</label></td>
            <td><input type="number" id="calories" name="calories" value="${meal.calories}"/></td>
        </tr>
        <tr>
            <td>
                <button type="submit" value="Submit">Save</button>
                <button type="button" value="Cancel" onClick="window.location.href='meals';">Cancel</button>
            </td>
            <td></td>
        </tr>
    </table>
</form>
</body>
</html>
