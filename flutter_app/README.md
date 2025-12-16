# Budget Tracker Flutter App 📱

Aplikasi Budget Tracker mobile (Android & iOS) yang dibangun dengan Flutter untuk mengelola keuangan pribadi.

**Platform:** Mobile (Android & iOS) - Bukan untuk Web

## Fitur

- ✅ Authentication (Register, Login, Profile)
- ✅ Transaction Management (CRUD)
- ✅ Category Management
- ✅ Monthly Summary dengan AI
- ✅ Budget Management
- ✅ Financial Goals
- ✅ User Management & Profile Picture
- ✅ Dashboard dengan Statistik
- ✅ Chart Visualisasi Data
- ✅ PDF Export

## 🚀 Quick Start

### Opsi 1: Menggunakan Android Studio (Recommended)

1. **Buka Project:**
   - File → Open → Pilih folder `flutter_app`
   - Wait for indexing selesai

2. **Setup Flutter Plugin:**
   - File → Settings → Plugins → Install "Flutter"
   - Restart Android Studio

3. **Setup Flutter SDK:**
   - File → Settings → Languages & Frameworks → Flutter
   - Set Flutter SDK path

4. **Konfigurasi API URL:**
   - Edit `lib/config/api_config.dart`
   - Android Emulator: `http://10.0.2.2:5001/api/v1`
   - Device Fisik: `http://<IP_KOMPUTER>:5001/api/v1`

5. **Setup Emulator:**
   - Tools → Device Manager → Create Device
   - Start emulator

6. **Run Aplikasi:**
   - Pilih device dari dropdown
   - Klik Run button (▶️) atau `Shift+F10`

**Lihat `ANDROID_STUDIO_GUIDE.md` untuk panduan lengkap!**

### Opsi 2: Menggunakan Command Line

### 1. Install Dependencies
```bash
cd flutter_app
flutter pub get
```

### 2. Konfigurasi API URL

Edit `lib/config/api_config.dart`:

**Android Emulator:**
```dart
static const String baseUrl = 'http://10.0.2.2:5001/api/v1';
```

**iOS/Web:**
```dart
static const String baseUrl = 'http://localhost:5001/api/v1';
```

**Device Fisik:**
```dart
static const String baseUrl = 'http://<IP_KOMPUTER>:5001/api/v1';
```

### 3. Pastikan Backend Berjalan
- Backend Spring Boot harus berjalan di `http://localhost:5001`
- Test: `curl http://localhost:5001/api/v1/health`

### 4. Run Aplikasi
```bash
# Cek device yang tersedia
flutter devices

# Run di device tertentu
flutter run -d <device_id>

# Atau run di default device
flutter run
```

## 📋 Prerequisites

### Untuk Android:
- Flutter SDK 3.0.0+
- Android Studio dengan Android SDK
- Android Emulator atau Android Device dengan USB Debugging

### Untuk iOS (macOS only):
- Flutter SDK 3.0.0+
- Xcode 14+
- iOS Simulator atau iOS Device
- CocoaPods

### Umum:
- Backend Spring Boot berjalan di `http://localhost:5001`
- Java JDK 11+ (untuk Android)

## 📖 Dokumentasi Lengkap

Lihat `RUN_FLUTTER.md` untuk panduan lengkap.

## Struktur Project

```
lib/
├── main.dart
├── models/          # Data models
├── services/        # API services
├── providers/       # State management
├── screens/         # UI screens
├── widgets/         # Reusable widgets
├── utils/           # Utilities
└── config/          # Configuration
```

## 🔧 Troubleshooting

### Error: "No devices found"
- Start emulator/simulator terlebih dahulu
- Atau hubungkan device fisik dengan USB debugging enabled

### Error: "Connection refused"
- Pastikan backend sudah berjalan
- Cek API URL di `lib/config/api_config.dart`
- Untuk device fisik, pastikan IP benar

### Error: "Package not found"
```bash
flutter clean
flutter pub get
```

## 📝 Development

- **Hot Reload**: Tekan `r` di terminal
- **Hot Restart**: Tekan `R` di terminal
- **Quit**: Tekan `q` di terminal

## 📚 Dokumentasi

- `MOBILE_QUICK_START.md` - **⭐ Quick start untuk mobile app**
- `MOBILE_SETUP.md` - Panduan lengkap setup mobile (Android & iOS)
- `ANDROID_STUDIO_GUIDE.md` - Panduan menggunakan Android Studio
- `QUICK_START.md` - Quick start guide umum
- `RUN_FLUTTER.md` - Panduan menjalankan app
- `FLUTTER_IMPLEMENTATION_GUIDE.md` - Guide implementasi
- `IMPLEMENTATION_SUMMARY.md` - Summary implementasi
- `FLUTTER_ERRORS_FIXED.md` - Daftar error yang sudah diperbaiki




