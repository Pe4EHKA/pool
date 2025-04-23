# 🏊‍♂️ Swim-Pool Service

REST-микросервис на Java / Spring Boot, который автоматизирует работу бассейна  
(регистрация клиентов и управление записями на посещение).

* **Стек:** Spring Boot 3 ➜ Web MVC, Validation, Spring Data JPA, PostgreSQL
* **Профиль тестирования:** H2 in-memory, JUnit 5 + Mockito
* **Сборка:** Maven
* **CI:** GitLab CI (юнит-тесты, линтер, сборка Docker-образа)

---

## 📌 Ключевые возможности

| Требование                            | Реализация                                                                          |
|---------------------------------------|-------------------------------------------------------------------------------------|
| Запись **только в рабочие часы**      | Таблицы `working_schedule` и `schedule_exception` + проверка в `ReservationService` |
| **Лимит 10** записей на час           | сервис считает текущую загрузку (`countByStartTime`), БД — индекс                   |
| Поиск записей по **ФИО / дате**       | GET `/api/v0/pool/timetable/search?name=&date=`                                     |
| Раздельные графики для **праздников** | `schedule_exception` (is_working = true/false)                                      |
| **≤ 1 запись/день/клиента**           | функциональный UNIQUE-индекс `ux_res_client_day`                                    |
| Запись на **несколько часов подряд**  | поле `hours`, сервис бронирует N часовых слотов транзакционно                       |

---

## ⚙️ Запуск проекта

```bash
# 1. задать переменные окружения (см. .env.example)
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pool
export SPRING_DATASOURCE_USERNAME=pool
export SPRING_DATASOURCE_PASSWORD=secret

# 2. собрать и запустить
./mvnw clean package
java -jar target/pool-service.jar
```

---

## 📚 HTTP-API (кратко)

| Method     | URL                                                | Описание         |
|------------|----------------------------------------------------|------------------|
| **GET**    | `/api/v0/pool/client/all`                          | список клиентов  |
| **GET**    | `/api/v0/pool/client/get?id={id}`                  | один клиент      |
| **POST**   | `/api/v0/pool/client/add`                          | создать клиента  |
| **PUT**    | `/api/v0/pool/client/update`                       | обновить клиента |
| **GET**    | `/api/v0/pool/timetable/all?date=YYYY-MM-DD`       | занятые слоты    |
| **GET**    | `/api/v0/pool/timetable/available?date=YYYY-MM-DD` | свободные слоты  |
| **POST**   | `/api/v0/pool/timetable/reserve`                   | забронировать    |
| **DELETE** | `/api/v0/pool/timetable/cancel`                    | отменить         |
| **GET**    | `/api/v0/pool/timetable/search?name=&date=`        | поиск записей    |


---

## 🗄️ Схема базы данных

> SQL‑миграции: `src/main/resources/schema-prod.sql`

### Таблица `clients`

| колонка    | тип            | ограничения               |
|------------|----------------|---------------------------|
| id         | `BIGINT`       | PK, GENERATED AS IDENTITY |
| name       | `VARCHAR(256)` | **NOT NULL**              |
| phone      | `VARCHAR(50)`  | **UNIQUE, NOT NULL**      |
| email      | `VARCHAR(256)` | **UNIQUE, NOT NULL**      |
| created_at | `TIMESTAMPTZ`  | default `now()`           |

Индексы: `idx_client_name` по полю `name` для быстрого поиска.

---

### Таблица `working_schedule`

| колонка     | тип    | комментарий       |
|-------------|--------|-------------------|
| day_of_week | `INT`  | PK; 1=Mon … 7=Sun |
| start_time  | `TIME` | начало работы     |
| end_time    | `TIME` | конец работы      |

Constraint `chk_working_schedule` гарантирует, что `end_time > start_time`.

---

### Таблица `schedule_exception`

Используется для праздников и перенесённых дней.

| колонка        | тип             | описание                    |
|----------------|-----------------|-----------------------------|
| exception_date | `DATE` PK       |
| is_working     | `BOOLEAN`       | `false` → бассейн закрыт    |
| start_time     | `TIME` nullable | начало работы (если открыт) |
| end_time       | `TIME` nullable | конец работы                |

Проверка `chk_schedule_is_working` не позволит указать время для закрытого дня и наоборот.

---

### Таблица `reservation`

| колонка               | тип                        | ограничения                                |
|-----------------------|----------------------------|--------------------------------------------|
| id                    | `BIGSERIAL` PK             |
| client_id             | `BIGINT` FK → `clients.id` |
| start_time / end_time | `TIMESTAMPTZ`              | **целочасовые**, `end_time > start_time`   |
| order_id              | `UUID`                     | **UNIQUE** — публичный идентификатор брони |
| created_at            | `TIMESTAMPTZ`              | default `now()`                            |

Ключевые индексы и ограничения

```sql
-- запрет второй записи того же клиента в тот же день
CREATE UNIQUE INDEX ux_res_client_day
    ON reservation (client_id, (start_time::date));

-- ускоряем подсчёт занятости часа
CREATE INDEX idx_res_time ON reservation (start_time);

-- ограничение «ровно на часовые границы»
CONSTRAINT chk_reservation_time
  CHECK (
    date_part('minute', start_time) = 0
    AND date_part('second', start_time) = 0
    AND date_part('minute', end_time)   = 0
    AND date_part('second', end_time)   = 0
  );
```

---

## 🛠️ Тестирование

| Уровень | Инструменты      | Покрытие              |
|---------|------------------|-----------------------|
| Unit    | JUnit 5, Mockito | сервисы, ErrorHandler |
| Web     | Spring MockMvc   | контроллеры           |

```bash
./mvnw test
```

---
