<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<%--<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>--%>
<html>
<head>
    <title>Meal list</title>
    <style>
        .normal {
            color: green;
        }

        .excess {
            color: red;
        }
    </style>
</head>
<body>
<section>
    <h3><a href="index.html">Home</a></h3>
    <hr/>
    <h2>Meals</h2>
    <a href="meals?action=create">Add Meal</a>
    <br><br>
    <form action="meals">
        <table>
            <tr>
                <td><label for="startDate">From date (inclusive):</label></td>
                <td><label for="endDate">To date (inclusive):</label></td>
                <td></td>
                <td><label for="startTime">From time (inclusive):</label></td>
                <td><label for="endTime">To time (exclusive):</label></td>
            </tr>
            <tr>
                <td><input type="date" id="startDate" name="startDate" value="${param.startDate}"></td>
                <td><input type="date" id="endDate" name="endDate" value="${param.endDate}"></td>
                <td></td>
                <td><input type="time" id="startTime" name="startTime" value="${param.startTime}"></td>
                <td><input type="time" id="endTime" name="endTime" value="${param.endTime}"></td>
            </tr>
            <tr>
                <td style="text-align:left"><input type="submit" value="Filter"></td>
                <td colspan="4"></td>
            </tr>
        </table>
    </form>
    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <c:forEach items="${meals}" var="meal">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.to.MealTo"/>
            <tr class="${meal.excess ? 'excess' : 'normal'}">
                <td>
                        <%--${meal.dateTime.toLocalDate()} ${meal.dateTime.toLocalTime()}--%>
                        <%--<%=TimeUtil.toString(meal.getDateTime())%>--%>
                        <%--${fn:replace(meal.dateTime, 'T', ' ')}--%>
                        ${fn:formatDateTime(meal.dateTime)}
                </td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
                <td><a href="meals?action=update&id=${meal.id}">Update</a></td>
                <td><a href="meals?action=delete&id=${meal.id}">Delete</a></td>
            </tr>
        </c:forEach>
    </table>
</section>
</body>
</html>