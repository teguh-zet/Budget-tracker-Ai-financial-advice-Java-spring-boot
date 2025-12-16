-- Migration Script: Menambahkan kolom type ke tabel categories
-- Pilih salah satu opsi di bawah ini

-- ============================================
-- OPSI 1: HAPUS SEMUA KATEGORI (TERMUDAH)
-- ============================================
-- Gunakan opsi ini jika tidak masalah kehilangan data kategori lama
-- Data seeder akan otomatis membuat kategori baru saat aplikasi restart

-- Hapus foreign key constraint dulu (jika ada)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE categories;
SET FOREIGN_KEY_CHECKS = 1;

-- Setelah ini, restart aplikasi dan seeder akan membuat kategori baru dengan type


-- ============================================
-- OPSI 2: UPDATE DATA LAMA (JIKA INGIN MEMPERTAHANKAN DATA)
-- ============================================
-- Gunakan opsi ini jika ingin mempertahankan kategori yang sudah ada
-- Tapi perlu manual mapping kategori ke INCOME atau EXPENSE

-- Step 1: Tambah kolom dengan default value
ALTER TABLE categories 
ADD COLUMN type VARCHAR(10) NULL;

-- Step 2: Update kategori yang sudah ada (sesuaikan dengan kebutuhan)
-- Contoh: Kategori pemasukan
UPDATE categories SET type = 'INCOME' 
WHERE name IN ('Gaji', 'Freelance');

-- Contoh: Kategori pengeluaran (default untuk yang lain)
UPDATE categories SET type = 'EXPENSE' 
WHERE type IS NULL;

-- Step 3: Set kolom menjadi NOT NULL
ALTER TABLE categories 
MODIFY COLUMN type VARCHAR(10) NOT NULL;

-- Catatan: Setelah ini, pastikan semua kategori sudah punya type
-- Jika ada yang masih NULL, akan error


-- ============================================
-- OPSI 3: RESET SELURUH DATABASE (HANYA UNTUK DEVELOPMENT)
-- ============================================
-- Gunakan opsi ini HANYA jika:
-- - Masih development
-- - Tidak masalah kehilangan SEMUA data (users, transactions, dll)
-- - Ingin fresh start

-- Hapus semua tabel
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS monthly_summaries;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- Setelah ini, restart aplikasi
-- JPA akan membuat ulang semua tabel dan seeder akan membuat kategori baru

