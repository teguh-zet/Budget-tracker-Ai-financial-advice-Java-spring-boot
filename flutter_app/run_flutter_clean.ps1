# Script untuk menjalankan Flutter Clean
# Cara pakai: Klik kanan file ini → Run with PowerShell

Write-Host "Mencari Flutter SDK..." -ForegroundColor Yellow

# Cek beberapa lokasi umum Flutter SDK
$flutterPaths = @(
    "$env:LOCALAPPDATA\flutter",
    "$env:USERPROFILE\flutter",
    "$env:USERPROFILE\AppData\Local\flutter",
    "C:\src\flutter",
    "C:\flutter",
    "D:\flutter",
    "E:\flutter"
)

$flutterFound = $false
$flutterPath = ""

foreach ($path in $flutterPaths) {
    if (Test-Path "$path\bin\flutter.bat") {
        $flutterPath = "$path\bin\flutter.bat"
        $flutterFound = $true
        Write-Host "Flutter ditemukan di: $path" -ForegroundColor Green
        break
    }
}

if (-not $flutterFound) {
    Write-Host "Flutter SDK tidak ditemukan di lokasi umum." -ForegroundColor Red
    Write-Host ""
    Write-Host "Cara menemukan Flutter SDK path:" -ForegroundColor Yellow
    Write-Host "1. Buka Android Studio" -ForegroundColor White
    Write-Host "2. File → Settings → Languages & Frameworks → Flutter" -ForegroundColor White
    Write-Host "3. Copy Flutter SDK path" -ForegroundColor White
    Write-Host "4. Edit script ini dan ganti path di bawah:" -ForegroundColor White
    Write-Host ""
    Write-Host "Atau gunakan cara yang lebih mudah:" -ForegroundColor Yellow
    Write-Host "- Buka Terminal di Android Studio (View → Tool Windows → Terminal)" -ForegroundColor White
    Write-Host "- Ketik: flutter clean" -ForegroundColor White
    Write-Host ""
    Write-Host "Atau:" -ForegroundColor Yellow
    Write-Host "- Klik kanan folder flutter_app → Flutter → Flutter Clean" -ForegroundColor White
    exit 1
}

Write-Host ""
Write-Host "Menjalankan Flutter Clean..." -ForegroundColor Yellow
Write-Host ""

# Change to flutter_app directory
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptPath

# Run flutter clean
& $flutterPath clean

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "Flutter Clean berhasil!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Lanjutkan dengan:" -ForegroundColor Yellow
    Write-Host "flutter pub get" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "Error saat menjalankan Flutter Clean" -ForegroundColor Red
}

