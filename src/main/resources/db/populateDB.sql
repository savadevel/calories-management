DELETE FROM user_roles;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (datetime, description, calories, user_id)
values ('2020-01-30 10:00:00', '1-Завтрак', 500, (select id from users where email = 'user@yandex.ru')),
       ('2020-01-30 13:00:00', '1-Обед', 1000, (select id from users where email = 'user@yandex.ru')),
       ('2020-01-30 20:00:00', '1-Ужин', 500, (select id from users where email = 'user@yandex.ru')),
       ('2020-01-31 00:00:00', '1-Еда на граничное значение', 100, (select id from users where email = 'user@yandex.ru')),
       ('2020-01-31 10:00:00', '1-Завтрак', 1000, (select id from users where email = 'user@yandex.ru')),
       ('2020-01-31 13:00:00', '1-Обед', 500, (select id from users where email = 'user@yandex.ru')),
       ('2020-01-31 20:00:00', '1-Ужин', 410, (select id from users where email = 'user@yandex.ru')),
       ('2020-01-30 10:00:00', '2-Завтрак', 500, (select id from users where email = 'admin@gmail.com')),
       ('2020-01-30 13:00:00', '2-Обед', 1000, (select id from users where email = 'admin@gmail.com')),
       ('2020-01-30 20:00:00', '2-Ужин', 500, (select id from users where email = 'admin@gmail.com')),
       ('2020-01-31 00:00:00', '2-Еда на граничное значение', 100, (select id from users where email = 'admin@gmail.com')),
       ('2020-01-31 10:00:00', '2-Завтрак', 1000, (select id from users where email = 'admin@gmail.com')),
       ('2020-01-31 13:00:00', '2-Обед', 500, (select id from users where email = 'admin@gmail.com')),
       ('2020-01-31 20:00:00', '2-Ужин', 410, (select id from users where email = 'admin@gmail.com'))

