#!/bin/bash
# Script untuk membersihkan file yang tidak perlu sebelum push ke GitHub
# Jalankan dengan: chmod +x CLEANUP_SCRIPT.sh && ./CLEANUP_SCRIPT.sh

echo "🧹 Membersihkan file yang tidak perlu..."

# Hapus node_modules
if [ -d "frontend/node_modules" ]; then
    echo "  ❌ Menghapus frontend/node_modules..."
    rm -rf frontend/node_modules
    echo "  ✅ frontend/node_modules dihapus"
fi

# Hapus build folders
BUILD_FOLDERS=(
    "spring-boot-backend/target"
    "frontend/.next"
    "frontend/dist"
    "flutter_app/build"
    "flutter_app/.dart_tool"
    "flutter_app/.flutter-plugins"
    "flutter_app/.flutter-plugins-dependencies"
    "flutter_app/.packages"
    "flutter_app/.pub-cache"
    "flutter_app/.pub"
)

for folder in "${BUILD_FOLDERS[@]}"; do
    if [ -d "$folder" ]; then
        echo "  ❌ Menghapus $folder..."
        rm -rf "$folder"
        echo "  ✅ $folder dihapus"
    fi
done

# Hapus uploads
if [ -d "spring-boot-backend/uploads" ]; then
    echo "  ❌ Menghapus spring-boot-backend/uploads..."
    rm -rf spring-boot-backend/uploads
    echo "  ✅ spring-boot-backend/uploads dihapus"
fi

# Hapus IDE files
if [ -d ".idea" ]; then
    echo "  ❌ Menghapus .idea..."
    rm -rf .idea
    echo "  ✅ .idea dihapus"
fi

if [ -d ".vscode" ]; then
    echo "  ❌ Menghapus .vscode..."
    rm -rf .vscode
    echo "  ✅ .vscode dihapus"
fi

# Hapus file IDE
find . -name "*.iml" -type f -delete 2>/dev/null
find . -name "*.iws" -type f -delete 2>/dev/null
find . -name "*.ipr" -type f -delete 2>/dev/null

# Hapus log files
echo "  ❌ Menghapus file log..."
find . -name "*.log" -type f -delete 2>/dev/null
echo "  ✅ File log dihapus"

# Hapus OS files
find . -name ".DS_Store" -type f -delete 2>/dev/null
find . -name "Thumbs.db" -type f -delete 2>/dev/null
find . -name "desktop.ini" -type f -delete 2>/dev/null

echo ""
echo "✨ Pembersihan selesai!"
echo ""
echo "📋 Checklist sebelum commit:"
echo "  [ ] Cek dengan 'git status'"
echo "  [ ] Pastikan tidak ada file sensitif (.env, API keys)"
echo "  [ ] Pastikan .gitignore sudah lengkap"
echo ""
echo "🚀 Siap untuk push ke GitHub!"

