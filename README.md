# KNU Auto Schedule

Система автоматизації складання розкладу для КНУ ім. Тараса Шевченка. Проект складається з Java (Spring Boot) бекенду, Next.js фронтенду та бази даних PostgreSQL.

## Швидкий запуск (Docker)

Найпростіший спосіб запустити весь проект — використати Docker Compose:

```bash
docker-compose up --build
```

Після запуску:
- **Frontend:** [http://localhost:3000](http://localhost:3000)
- **Backend API:** [http://localhost:8080/api](http://localhost:8080/api)
- **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Структура проекту

- `/backend` — Серверна частина на Java 21 (Spring Boot, Hibernate, PostgreSQL).
- `/frontend` — Клієнтська частина на Next.js (React, MUI, TanStack Query).
- `docker-compose.yml` — Конфігурація для запуску всього стеку в контейнерах.

## Системні вимоги

- Docker & Docker Compose
- Java 21 (для локальної розробки бекенду)
- Node.js 20+ (для локальної розробки фронтенду)

## Налаштування TimeZone

Проект налаштований на використання часового поясу `Europe/Kyiv`. Це сконфігуровано на рівнях:
1. JVM (в коді бекенду).
2. Spring/Jackson (в `application.properties`).
3. Контейнерів Docker (через змінну `TZ`).
