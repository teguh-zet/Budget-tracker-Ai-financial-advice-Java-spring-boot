# 💰 Budget Tracker Application

Aplikasi web untuk tracking budget dan keuangan pribadi dengan fitur AI Financial Advisor.

## 🏗️ Tech Stack

### Backend
- **Node.js** 20.x
- **Express.js** 5.x
- **Sequelize ORM** - Database management
- **MySQL** - Database
- **JWT** - Authentication
- **Zod** - Validation

### Frontend
- **Next.js** 15.x
- **React** 19
- **TypeScript**
- **Tailwind CSS** 4
- **Axios** - HTTP client
- **Recharts** - Data visualization

## 📁 Struktur Project

```
backend/
├── src/
│   ├── modules/          # Feature modules (auth, transaction, category, etc)
│   ├── middlewares/      # Express middlewares
│   ├── errors/           # Custom error handlers
│   ├── config/           # Configuration files
│   ├── store/            # Database connection
│   ├── routes.js         # Main routes
│   ├── app.js            # Express app setup
│   └── server.js         # Server entry point
├── migrations/           # Database migrations
├── seeders/              # Database seeders
└── models/               # Sequelize models

frontend/
├── src/
│   ├── app/              # Next.js app router pages
│   ├── pages/            # Page components
│   ├── services/         # API service functions
│   ├── ui/               # Reusable UI components
│   ├── interfaces/       # TypeScript interfaces
│   └── utils/            # Utility functions
└── public/               # Static assets
```

## 🚀 Quick Start

### Prasyarat
- Node.js 20.x
- MySQL 8.0+
- npm atau yarn

### 1. Clone Repository

```bash
git clone <repository-url>
cd budget-tracker/check/backend
```

### 2. Setup Backend

```bash
cd backend

# Install dependencies
npm install

# Setup environment variables
cp .env.example .env
# Edit .env dengan credentials database Anda

# Run migrations
npx sequelize-cli db:migrate

# Run seeders (optional)
npx sequelize-cli db:seed:all

# Start development server
npm run dev
```

Backend akan berjalan di `http://localhost:5001`

### 3. Setup Frontend

```bash
cd frontend

# Install dependencies
npm install

# Setup environment variables
cp .env.example .env
# Edit .env dengan API URL backend

# Start development server
npm run dev
```

Frontend akan berjalan di `http://localhost:3000`

## 📝 Environment Variables

### Backend (.env)

Lihat `backend/.env.example` untuk contoh lengkap. Minimum yang diperlukan:

```env
DB_USER=your_db_user
DB_PASSWORD=your_db_password
DB_DATABASE=your_database_name
DB_HOST=localhost
DB_PORT=3306
SERVER_PORT=5001
JWT_SECRET=your_secret_key
```

### Frontend (.env)

```env
NEXT_PUBLIC_API_DEV_BASE_URL_V1=http://localhost:5001/api/v1
NEXT_PUBLIC_API_PROD_BASE_URL_V1=https://your-backend-url.com/api/v1
```

## 📚 API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Register user baru
- `POST /api/v1/auth/login` - Login user
- `GET /api/v1/auth/profile` - Get user profile (protected)

### Transactions
- `GET /api/v1/transaction` - Get all transactions (protected)
- `POST /api/v1/transaction` - Create transaction (protected)
- `PUT /api/v1/transaction/:id` - Update transaction (protected)
- `DELETE /api/v1/transaction/:id` - Delete transaction (protected)

### Categories
- `GET /api/v1/category` - Get all categories (protected)
- `POST /api/v1/category` - Create category (protected)

### Monthly Summary
- `GET /api/v1/monthly-summary` - Get monthly summaries (protected)

### Health Check
- `GET /api/v1/health` - Server health check

## 🗄️ Database Schema

### Users
- id, name, email, password, number, created_at, updated_at

### Categories
- id, name, type (income/expense), created_at, updated_at

### Transactions
- id, type (income/expense), amount, date, note, user_id, category_id, created_at, updated_at

### Monthly Summaries
- id, user_id, month, year, total_income, total_expense, balance, created_at, updated_at

## 🔐 Authentication

Aplikasi menggunakan JWT (JSON Web Tokens) untuk authentication. Setelah login, token harus disertakan di header:

```
Authorization: Bearer <token>
```

## 🧪 Testing

### Backend Health Check

```bash
curl http://localhost:5001/api/v1/health
```

### Database Connection Test

```bash
curl http://localhost:5001/api/v1/db-ping
```

## 📦 Scripts

### Backend
- `npm start` - Start production server
- `npm run dev` - Start development server dengan nodemon

### Frontend
- `npm run dev` - Start development server
- `npm run build` - Build untuk production
- `npm start` - Start production server
- `npm run lint` - Run ESLint

## 🐛 Troubleshooting

### Database Connection Error
- Pastikan MySQL service running
- Cek credentials di .env
- Pastikan database sudah dibuat

### Port Already in Use
- Backend default: 5001
- Frontend default: 3000
- Ubah di .env jika port sudah digunakan

### Migration Errors
```bash
# Reset database (HATI-HATI: akan menghapus semua data)
npx sequelize-cli db:migrate:undo:all
npx sequelize-cli db:migrate
```

## 📄 License

ISC

## 👥 Contributors

- Teazet Team

## 📞 Support

Untuk pertanyaan atau masalah, silakan buat issue di repository.

