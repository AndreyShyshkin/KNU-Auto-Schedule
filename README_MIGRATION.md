# KNU Auto Schedule Migration

This project is a modernization of the legacy Java Swing application.

## Structure

*   `backend-new/`: Spring Boot Application (Java 17+)
*   `frontend/`: Next.js Application (TypeScript, Tailwind)
*   `backend/`: Legacy Code (Reference)

## Prerequisites

*   Java 17 or higher
*   Node.js 18 or higher
*   Gradle (if not using wrapper, though wrapper is recommended)

## How to Run

### Backend (Spring Boot)

1.  Navigate to `backend-new`.
2.  Run the application:
    ```bash
    gradle bootRun
    ```
    *Note: If you don't have gradle installed globally, you may need to install it or use a wrapper if generated.*

3.  The API will be available at `http://localhost:8080`.
4.  H2 Console (Database): `http://localhost:8080/h2-console`
    *   JDBC URL: `jdbc:h2:mem:scheduledb`
    *   User: `sa`
    *   Password: (empty)

### Frontend (Next.js)

1.  Navigate to `frontend`.
2.  Install dependencies (first time only):
    ```bash
    npm install
    ```
3.  Run the development server:
    ```bash
    npm run dev
    ```
4.  Open `http://localhost:3000`.

## Architecture

*   **Database:** H2 (In-memory for Dev), mapped via JPA Entities.
*   **API:** RESTful endpoints in `ua.kiev.univ.schedule.controller`.
*   **UI:** React components fetching data via Axios.
