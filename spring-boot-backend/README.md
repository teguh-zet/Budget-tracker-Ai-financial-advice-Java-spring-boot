# Budget Tracker API - Spring Boot

Backend API untuk aplikasi Budget Tracker yang di-migrate dari Express.js ke Spring Boot.

## Teknologi

- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Data JPA** (MySQL)
- **Spring Security** (JWT Authentication)
- **Lombok**
- **Maven**
- **SpringDoc OpenAPI** (Swagger)

## Prerequisites

- Java 21 atau lebih tinggi
- Maven 3.6+
- MySQL 8.0+
- (Opsional) IDE seperti IntelliJ IDEA atau Eclipse

## Setup

### 1. Clone Repository

```bash
cd spring-boot-backend
```

### 2. Konfigurasi Database

Buat file `application.yml` atau gunakan environment variables:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/budget_tracker?useSSL=false&serverTimezone=Asia/Jakarta
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

jwt:
  secret: your-secret-key-change-in-production
  expiration: 86400000

openrouter:
  api-key: your-openrouter-api-key
```

Atau gunakan environment variables:

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_DATABASE=budget_tracker
export DB_USER=root
export DB_PASSWORD=your_password
export JWT_SECRET=your-secret-key
export OPENROUTER_API_KEY=your-api-key
```

### 3. Setup Database

**Otomatis (Recommended)**:
- Database akan dibuat otomatis oleh JPA (`ddl-auto=update`)
- Data kategori akan di-seed otomatis saat aplikasi pertama kali dijalankan (jika tabel kosong)
- Lihat `DATA_SEEDING.md` untuk detail

**Manual** (jika perlu):

```sql
CREATE DATABASE budget_tracker;

-- Tabel users
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    number VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabel categories
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabel transactions
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    amount VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    note TEXT,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- Tabel monthly_summaries
CREATE TABLE monthly_summaries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    month VARCHAR(25) NOT NULL,
    year VARCHAR(4) NOT NULL,
    total_income VARCHAR(255) NOT NULL,
    total_expense VARCHAR(255) NOT NULL,
    balance VARCHAR(255) NOT NULL,
    ai_summary TEXT,
    ai_recomendation TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 4. Build Project

```bash
mvn clean install
```

### 5. Run Application

**Development mode:**
```bash
mvn spring-boot:run
```

Atau dengan profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Production mode:**
```bash
java -jar target/budget-tracker-api-1.0.0.jar --spring.profiles.active=prod
```

## API Endpoints

Base URL: `http://localhost:5001/api/v1`

### Health Check
- `GET /api/v1/health` - Health check
- `GET /api/v1/db-ping` - Database connection test

### Authentication
- `POST /api/v1/auth/register` - Register user baru
- `POST /api/v1/auth/login` - Login user
- `GET /api/v1/auth/profile` - Get profile (protected)

### Users
- `GET /api/v1/users` - Get all users
- `GET /api/v1/users/:id` - Get user by ID
- `POST /api/v1/users` - Create user
- `PUT /api/v1/users/:id` - Update user
- `DELETE /api/v1/users/:id` - Delete user

### Categories (Protected)
- `GET /api/v1/category` - Get all categories
- `GET /api/v1/category/:id` - Get category by ID
- `POST /api/v1/category` - Create category
- `PUT /api/v1/category/:id` - Update category
- `DELETE /api/v1/category/:id` - Delete category

### Transactions (Protected)
- `GET /api/v1/transaction` - Get all transactions (with pagination & search)
  - Query params: `page`, `limit`, `search`
- `GET /api/v1/transaction/:id` - Get transaction by ID
- `POST /api/v1/transaction` - Create transaction
- `PUT /api/v1/transaction/:id` - Update transaction
- `DELETE /api/v1/transaction/:id` - Delete transaction
- `GET /api/v1/transaction/monthly-summary` - Get monthly summary stats
- `GET /api/v1/transaction/monthly-chart` - Get monthly chart data
- `GET /api/v1/transaction/today` - Get today's transactions
- `GET /api/v1/transaction/today-expense-stats` - Get today's expense statistics

### Monthly Summary (Protected)
- `GET /api/v1/monthly-summary` - Get all monthly summaries
- `GET /api/v1/monthly-summary/:id` - Get monthly summary by ID
- `POST /api/v1/monthly-summary` - Create monthly summary
- `PUT /api/v1/monthly-summary/:id` - Update monthly summary
- `DELETE /api/v1/monthly-summary/:id` - Delete monthly summary
- `POST /api/v1/monthly-summary/generate` - Generate AI-powered monthly summary

## Authentication

Semua endpoint kecuali `/auth/register`, `/auth/login`, `/health`, dan `/db-ping` memerlukan JWT token.

**Header:**
```
Authorization: Bearer <token>
```

## Response Format

### Success Response
```json
{
  "success": true,
  "message": "Success message",
  "data": {...}
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error message"
}
```

### Pagination Response
```json
{
  "success": true,
  "message": "Success message",
  "data": [...],
  "pagination": {
    "total": 100,
    "page": 1,
    "limit": 10,
    "totalPage": 10
  }
}
```

## Swagger Documentation

Setelah aplikasi berjalan, akses Swagger UI di:
- **Swagger UI**: http://localhost:5001/swagger-ui.html
- **API Docs**: http://localhost:5001/api-docs

## Contoh Request

### Register
```bash
curl -X POST http://localhost:5001/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "number": "081234567890"
  }'
```

### Login
```bash
curl -X POST http://localhost:5001/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Get Transactions (Protected)
```bash
curl -X GET "http://localhost:5001/api/v1/transaction?page=1&limit=10" \
  -H "Authorization: Bearer <token>"
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | Database host | `localhost` |
| `DB_PORT` | Database port | `3306` |
| `DB_DATABASE` | Database name | `budget_tracker` |
| `DB_USER` | Database user | `root` |
| `DB_PASSWORD` | Database password | - |
| `JWT_SECRET` | JWT secret key | - |
| `JWT_EXPIRATION` | JWT expiration (ms) | `86400000` (1 day) |
| `OPENROUTER_API_KEY` | OpenRouter API key | - |
| `SERVER_PORT` | Server port | `5001` |
| `ALLOWED_ORIGINS` | CORS allowed origins | `http://localhost:3000` |

## Testing

```bash
# Run tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## Deployment

### Docker

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/budget-tracker-api-1.0.0.jar app.jar
EXPOSE 5001
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build Docker Image
```bash
docker build -t budget-tracker-api .
docker run -p 5001:5001 \
  -e DB_HOST=your-db-host \
  -e DB_PASSWORD=your-password \
  -e JWT_SECRET=your-secret \
  budget-tracker-api
```

## Troubleshooting

### Database Connection Error
- Pastikan MySQL sudah berjalan
- Cek kredensial database di `application.yml`
- Pastikan database sudah dibuat

### JWT Token Invalid
- Pastikan `JWT_SECRET` sudah di-set
- Token expired (default: 1 hari)
- Pastikan header `Authorization: Bearer <token>` sudah benar

### Port Already in Use
- Ubah `SERVER_PORT` di `application.yml` atau environment variable
- Atau kill process yang menggunakan port 5001

## Migration Notes

### Perbedaan dengan Express.js Backend

1. **Response Format**: Tetap sama dengan Express.js (`success`, `message`, `data`)
2. **Endpoint Path**: Semua endpoint tetap sama
3. **Authentication**: JWT Bearer token (sama)
4. **Validation**: Menggunakan Jakarta Validation (`@Valid`, `@NotNull`, dll)
5. **Error Handling**: Global exception handler dengan format response yang sama

### Database Schema
- Schema database tetap sama dengan Express.js
- Menggunakan JPA dengan naming strategy yang kompatibel

## License

ISC

## Author

Migrated from Express.js to Spring Boot

