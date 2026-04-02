# KNU Auto Schedule Frontend

Клієнтська частина системи автоматизації розкладу. Побудована на Next.js.

## Технологічний стек

- **Next.js:** 16+ (App Router)
- **React:** 19+
- **Styling:** MUI (Material UI)
- **Data Fetching:** Axios, TanStack React Query
- **State Management:** React Hooks
- **Language:** TypeScript

## Локальний запуск (без Docker)

1.  **Встановіть залежності**:
    ```bash
    npm install
    ```
2.  **Запустіть режим розробки**:
    ```bash
    npm run dev
    ```
3.  **Відкрийте**: [http://localhost:3000](http://localhost:3000)

## Зв'язок з бекендом

Для зв'язку з API використовується проксі-механізм Next.js (`rewrites` у `next.config.mjs`):
- Запити до `/api/*` перенаправляються на бекенд (за замовчуванням `localhost:8080`).

## Структура проекту

- `src/app` — Маршрути та основні сторінки.
- `src/components/views` — Відображення для окремих сутностей (Факультети, Кафедри, Викладачі тощо).
- `src/lib/api` — Клієнт для роботи з API (`scheduleApi.ts`).
