-- Seed data untuk categories
-- Hanya insert jika belum ada data

INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Gaji', 'Penghasilan dari pekerjaan tetap', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Gaji');

INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Makanan & Minuman', 'Belanja makan harian dan jajan', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Makanan & Minuman');

INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Transportasi', 'Ongkos transportasi dan bahan bakar', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Transportasi');

INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Tempat Tinggal', 'Sewa, listrik, dan air', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Tempat Tinggal');

INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Hiburan', 'Streaming, game, atau liburan', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Hiburan');

INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Belanja', 'Belanja pakaian dan kebutuhan pribadi', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Belanja');

INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Freelance', 'Penghasilan dari pekerjaan lepas', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Freelance');

INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Pendidikan', 'Buku dan biaya kursus', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Pendidikan');

INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Investasi', 'Dana untuk saham atau reksadana', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Investasi');

INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Donasi', 'Zakat dan sumbangan sosial', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Donasi');

INSERT INTO categories (name, description, created_at, updated_at)
SELECT 'Lainnya', 'Kategori tidak terdefinisi', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Lainnya');

