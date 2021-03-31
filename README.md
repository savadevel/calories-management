Онлайн стажировка (Topjava): Java Enterprise Online Project 
===============================
Разработка полнофункционального Spring/JPA Enterprise приложения c авторизацией и правами доступа на основе ролей с использованием наиболее популярных инструментов и технологий Java: Maven, Spring MVC, Security, JPA(Hibernate), REST(Jackson), Bootstrap (css,js), datatables, jQuery + plugins, Java 8 Stream and Time API и хранением в базах данных Postgresql и HSQLDB.

![topjava_structure](https://user-images.githubusercontent.com/13649199/27433714-8294e6fe-575e-11e7-9c41-7f6e16c5ebe5.jpg)

# Using cURL for get meals of the user (authorised user) by REST queries
### Get all meals
If curl query right then return meals in json format and HTTP code is 200
```bash
curl -X GET -i http://localhost:8080/topjava/rest/meals/
```
### Get meals between start and end date / time (half open interval)
If curl query right then return meals in json format and HTTP code is 200
```bash
curl -X GET -i "http://localhost:8080/topjava/rest/meals/filter?startDate=2020-01-30&endDate=2020-01-31&startTime=10:00:00&endTime=15:00:00"
```
### Creat meal
If curl query right then return new meal (json format) and HTTP code is 201
```bash
curl -i -X POST -H "Content-Type: application/json" -d "{\"dateTime\":\"2021-02-02T10:00:00\",\"description\":\"description of new meal\",\"calories\":1000}" http://localhost:8080/topjava/rest/meals
```
### Update meal
If curl query right then return new meal (json format) and HTTP code is 204
```bash
curl -i -X PUT -H "Content-Type: application/json" -d "{\"id\":100013,\"dateTime\":\"2021-02-02T10:00:00\",\"description\":\"description of updated meal\",\"calories\":1000}" http://localhost:8080/topjava/rest/meals/100013
```
### Delete meal by ID 
If curl query right then return only HTTP code is 204, no any data
```bash
curl -X DELETE -i http://localhost:8080/topjava/rest/meals/{ID}
```

