# KNU Auto Schedule Backend

Бекенд-частина системи автоматизації розкладу. Побудована на Spring Boot 3.

## Технологічний стек

- **Java:** 21 (Eclipse Temurin)
- **Framework:** Spring Boot 3.2.3
- **ORM:** Hibernate / JPA
- **Database:** PostgreSQL
- **API Documentation:** SpringDoc / Swagger
- **Timezone:** Europe/Kyiv (UTC+2/UTC+3)

## Локальний запуск (без Docker)

1.  **Налаштуйте PostgreSQL**: Створіть базу даних `schedule_db` та користувача `postgres` з паролем `password`.
2.  **Запустіть проект**:
    ```bash
    mvn spring-boot:run
    ```

## Перемінні оточення

Ви можете налаштувати підключення до БД через перемінні:
- `DB_HOST` (за замовчуванням: `localhost`)
- `DB_NAME` (за замовчуванням: `schedule_db`)
- `DB_USER` (за замовчуванням: `postgres`)
- `DB_PASSWORD` (за замовчуванням: `password`)

## Документація API

Доступна за адресою:
- [Swagger UI](http://localhost:8080/swagger-ui/index.html)
- [OpenAPI Specification (JSON)](http://localhost:8080/v3/api-docs)

## Особливості

- **Автоматична ініціалізація даних**: `DataInitializationService` завантажує початкові дані при кожному старті.
- **Мапінг DTO**: Власна реалізація `DtoMapper` для перетворення сутностей БД у формати для фронтенду.
- **Алгоритм складання**: Основна логіка знаходиться в пакеті `ua.kiev.univ.schedule.scheduler`.
