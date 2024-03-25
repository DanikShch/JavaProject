# Шифратор персональных данных
Шифратор персональных данных: Сервис должен принимать входящий текст и возвращать найденные в тексте email адреса, телефоны
https://sonarcloud.io/project/overview?id=DanikShch_labsJava
# Задание 1 - Basic REST service
1. Создать и запустить локально простейший веб/REST сервис, используя любой открытый пример с использованием Java stack: Spring (Spring Boot)/maven/gradle/Jersey/ Spring MVC.
2. Добавить GET ендпоинт, принимающий входные параметры в качестве queryParams в URL согласно варианту, и возвращающий любой hard-coded результат в виде JSON согласно варианту.
# Задание 2 - JPA (Hibernate/Spring Data)
1. Подключить в проект БД (PostgreSQL/MySQL/и т.д.).
(0 - 7 баллов) - Реализация связи один ко многим @OneToMany
(8 - 10 баллов) - Реализация связи многие ко многим @ManyToMany
2. Реализовать CRUD-операции со всеми сущностями.
# Задание 3 - Data caching
1. Добавить в проект GET ендпоинт (он должен быть полезный) с параметром(-ами). Данные должны быть получены из БД с помощью ""кастомного"" запроса (@Query) с параметром(-ами) ко вложенной сущности.
2. Добавить простейший кэш в виде in-memory Map (в виде отдельного бина).
# Задание 4 - Error logging/handling
1. Обработать 400 и 500 ошибки.
2. Добавить логирование действий и ошибок (аспекты).
3. Подключить Swagger & CheckStyle. Убрать стилистические ошибки.
