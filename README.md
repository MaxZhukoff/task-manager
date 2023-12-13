# Task Management System

Простая система управления задачами. Система обеспечивает создание, редактирование, удаление и просмотр задач. Каждая задача содержит заголовок, описание, статус, а также автора задачи и исполнителя. Также к задачам можно оставлять комментарии.

## Локальный запуск проекта

1. Клонируйте репозиторий
2. Установить следующие переменные окружения:
    - DB_URL - URL для подключения к базе данных (например, jdbc:postgresql://localhost:5433/task_manager)
    - DB_USERNAME - имя пользователя базы данных (например, postgres)
    - DB_PASSWORD - пароль пользователя базы данных (например, postgres)
    - SEC_SECRET - секретный ключ для шифрования JWT-токенов (например, mysuperstrongsecretwithlength32!)
3. Запустите docker-compose с БД и Liquibase, а затем можно запускать приложение. Или сразу запустите приложение, контейнеры поднимутся сами.

По адресу http://localhost:8080/swagger-ui будет доступно описание API.