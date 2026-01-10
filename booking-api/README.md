# 🏥 Booking API

REST API система для управління записами пацієнтів до лікарів у клініці.

## 🎯 Про проєкт

**Booking API** — навчальний проєкт для демонстрації навичок backend розробки на Java/Spring Boot.

### Основний функціонал:
- ✅ CRUD операції для записів на прийом
- ✅ Валідація вхідних даних
- ✅ Централізована обробка помилок
- ✅ RESTful API з DTO pattern

## 🛠 Технології

- **Java 17** — основна мова
- **Spring Boot 3.x** — фреймворк
- **Spring Data JPA** — ORM
- **PostgreSQL 16** — база даних
- **Docker Compose** — контейнеризація
- **Maven** — збірка проєкту

## 🏗 Архітектура

Класична шарова архітектура:
- **Controller** → REST endpoints
- **Service** → бізнес-логіка
- **Repository** → доступ до БД
- **Entity** → JPA entities
- **DTO** → Data Transfer Objects

## 🚀 Запуск проєкту

### Вимоги
- JDK 17+
- Docker Desktop
- Maven 3.6+

### Кроки запуску

```bash
# 1. Клонувати репозиторій
git clone https://github.com/Nikos5894/booking-api.git
cd booking-api

# 2. Запустити PostgreSQL
docker-compose up -d

# 3. Запустити додаток
mvnw.cmd spring-boot:run  # Windows
./mvnw spring-boot:run     # Linux/Mac
```

Додаток буде доступний: **http://localhost:8082**

## 📡 API Endpoints

### Appointments

| Метод | Endpoint | Опис |
|-------|----------|------|
| GET | `/api/appointments` | Всі записи |
| GET | `/api/appointments/{id}` | Запис за ID |
| POST | `/api/appointments` | Створити запис |
| PUT | `/api/appointments/{id}` | Оновити запис |
| DELETE | `/api/appointments/{id}` | Видалити запис |

### Приклад створення запису

```json
POST /api/appointments
{
  "patientName": "Іван Петренко",
  "doctorName": "Др. Коваленко",
  "appointmentDate": "2026-01-15",
  "appointmentTime": "10:00",
  "status": "SCHEDULED"
}
```

## 🗄 Структура БД

### Таблиця: appointment

- `id` — BIGINT (Primary key)
- `patient_name` — VARCHAR(255)
- `doctor_name` — VARCHAR(255)
- `appointment_date` — DATE
- `appointment_time` — TIME
- `status` — VARCHAR(50) (SCHEDULED, CONFIRMED, CANCELLED, COMPLETED)
- `created_at` — TIMESTAMP
- `updated_at` — TIMESTAMP

## 📌 Roadmap

### v1.1 (у розробці)
- [ ] CRUD для Doctor entity
- [ ] CRUD для Patient entity
- [ ] Пагінація результатів
- [ ] Swagger документація

### v2.0 (плани)
- [ ] Spring Security + JWT
- [ ] Email нотифікації
- [ ] Flyway міграції
- [ ] CI/CD pipeline
- [ ] Docker образ

## 👨‍💻 Автор

**Микола Осадчук**
- GitHub: [@Nikos5894](https://github.com/Nikos5894)
- Email: kolia.osadchukcool@gmail.com

## 📄 Ліцензія

Навчальний проєкт (Educational purposes)

---

⭐ Якщо проєкт корисний — поставте зірочку на GitHub!
