# Script untuk membersihkan file yang tidak perlu sebelum push ke GitHub
# Jalankan dengan: .\CLEANUP_SCRIPT.ps1

Write-Host "🧹 Membersihkan file yang tidak perlu..." -ForegroundColor Cyan

# Hapus node_modules
if (Test-Path "frontend/node_modules") {
    Write-Host "  ❌ Menghapus frontend/node_modules..." -ForegroundColor Yellow
    Remove-Item -Recurse -Force "frontend/node_modules"
    Write-Host "  ✅ frontend/node_modules dihapus" -ForegroundColor Green
}

# Hapus build folders
$buildFolders = @(
    "spring-boot-backend/target",
    "frontend/.next",
    "frontend/dist",
    "flutter_app/build",
    "flutter_app/.dart_tool",
    "flutter_app/.flutter-plugins",
    "flutter_app/.flutter-plugins-dependencies",
    "flutter_app/.packages",
    "flutter_app/.pub-cache",
    "flutter_app/.pub"
)

foreach ($folder in $buildFolders) {
    if (Test-Path $folder) {
        Write-Host "  ❌ Menghapus $folder..." -ForegroundColor Yellow
        Remove-Item -Recurse -Force $folder
        Write-Host "  ✅ $folder dihapus" -ForegroundColor Green
    }
}

# Hapus uploads
if (Test-Path "spring-boot-backend/uploads") {
    Write-Host "  ❌ Menghapus spring-boot-backend/uploads..." -ForegroundColor Yellow
    Remove-Item -Recurse -Force "spring-boot-backend/uploads"
    Write-Host "  ✅ spring-boot-backend/uploads dihapus" -ForegroundColor Green
}

# Hapus IDE files
$ideFiles = @(
    ".idea",
    ".vscode",
    "*.iml",
    "*.iws",
    "*.ipr"
)

foreach ($item in $ideFiles) {
    if ($item -like "*.*") {
        # File pattern
        Get-ChildItem -Path . -Filter $item -Recurse -ErrorAction SilentlyContinue | ForEach-Object {
            Write-Host "  ❌ Menghapus $($_.FullName)..." -ForegroundColor Yellow
            Remove-Item -Force $_.FullName
            Write-Host "  ✅ $($_.Name) dihapus" -ForegroundColor Green
        }
    } else {
        # Folder
        if (Test-Path $item) {
            Write-Host "  ❌ Menghapus $item..." -ForegroundColor Yellow
            Remove-Item -Recurse -Force $item
            Write-Host "  ✅ $item dihapus" -ForegroundColor Green
        }
    }
}

# Hapus log files
Write-Host "  ❌ Menghapus file log..." -ForegroundColor Yellow
Get-ChildItem -Path . -Filter "*.log" -Recurse -ErrorAction SilentlyContinue | ForEach-Object {
    Remove-Item -Force $_.FullName
}
Write-Host "  ✅ File log dihapus" -ForegroundColor Green

# Hapus OS files
$osFiles = @(
    ".DS_Store",
    "Thumbs.db",
    "desktop.ini"
)

foreach ($file in $osFiles) {
    Get-ChildItem -Path . -Filter $file -Recurse -ErrorAction SilentlyContinue | ForEach-Object {
        Write-Host "  ❌ Menghapus $($_.FullName)..." -ForegroundColor Yellow
        Remove-Item -Force $_.FullName
        Write-Host "  ✅ $($_.Name) dihapus" -ForegroundColor Green
    }
}

Write-Host "`n✨ Pembersihan selesai!" -ForegroundColor Green
Write-Host "`n📋 Checklist sebelum commit:" -ForegroundColor Cyan
Write-Host "  [ ] Cek dengan 'git status'" -ForegroundColor White
Write-Host "  [ ] Pastikan tidak ada file sensitif (.env, API keys)" -ForegroundColor White
Write-Host "  [ ] Pastikan .gitignore sudah lengkap" -ForegroundColor White
Write-Host "`n🚀 Siap untuk push ke GitHub!" -ForegroundColor Green

