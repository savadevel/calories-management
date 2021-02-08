<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="ru">
<style>
    table, th, td {
        border: 1px solid black;
        border-collapse: collapse;
    }

    th, td {
        padding: 15px;
    }

    tr.excess {
        color: red;
    }

    tr.without_excess {
        color: green;
    }
</style>
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<hr>
<a href="meal?action=EDIT_CREATE">Add Meal</a>
<hr>
<table>
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
        <th></th>
        <th></th>
    </tr>
    <jsp:useBean id="meals" scope="request" type="java.util.List<ru.javawebinar.topjava.model.MealTo>"/>
    <jsp:useBean id="dateTimeFormatter" scope="request" type="java.time.format.DateTimeFormatter"/>
    <c:forEach var="meal" items="${meals}">
        <tr class="${meal.excess? 'excess' : 'without_excess'}">
            <td>${meal.dateTime.format(dateTimeFormatter)}</td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td><a href="meal?action=EDIT_UPDATE&id=${meal.id}">Update</a></td>
            <td><a href="meals?action=DELETE&id=${meal.id}">Delete</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>