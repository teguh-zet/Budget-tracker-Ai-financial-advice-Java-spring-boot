package com.budgettracker.config;

import com.budgettracker.entity.Category;
import com.budgettracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {
    
    private final CategoryRepository categoryRepository;
    
    @Override
    public void run(String... args) {
        seedCategories();
    }
    
    private void seedCategories() {
        if (categoryRepository.count() > 0) {
            log.info("Categories already exist, skipping seed");
            return;
        }
        
        List<Category> categories = Arrays.asList(
            // Kategori Pemasukan (INCOME)
            Category.builder()
                .name("Gaji")
                .description("Penghasilan dari pekerjaan tetap")
                .type(Category.CategoryType.INCOME)
                .build(),
            Category.builder()
                .name("Freelance")
                .description("Penghasilan dari pekerjaan lepas")
                .type(Category.CategoryType.INCOME)
                .build(),
            Category.builder()
                .name("Investasi")
                .description("Return dari investasi (dividen, capital gain)")
                .type(Category.CategoryType.INCOME)
                .build(),
            Category.builder()
                .name("Bonus")
                .description("Bonus atau tunjangan tambahan")
                .type(Category.CategoryType.INCOME)
                .build(),
            Category.builder()
                .name("Hadiah")
                .description("Hadiah atau pemberian dari orang lain")
                .type(Category.CategoryType.INCOME)
                .build(),
            Category.builder()
                .name("Penjualan")
                .description("Penghasilan dari penjualan barang")
                .type(Category.CategoryType.INCOME)
                .build(),
            Category.builder()
                .name("Lainnya (Pemasukan)")
                .description("Kategori pemasukan lainnya")
                .type(Category.CategoryType.INCOME)
                .build(),
            
            // Kategori Pengeluaran (EXPENSE)
            Category.builder()
                .name("Makanan & Minuman")
                .description("Belanja makan harian dan jajan")
                .type(Category.CategoryType.EXPENSE)
                .build(),
            Category.builder()
                .name("Transportasi")
                .description("Ongkos transportasi dan bahan bakar")
                .type(Category.CategoryType.EXPENSE)
                .build(),
            Category.builder()
                .name("Tempat Tinggal")
                .description("Sewa, listrik, dan air")
                .type(Category.CategoryType.EXPENSE)
                .build(),
            Category.builder()
                .name("Hiburan")
                .description("Streaming, game, atau liburan")
                .type(Category.CategoryType.EXPENSE)
                .build(),
            Category.builder()
                .name("Belanja")
                .description("Belanja pakaian dan kebutuhan pribadi")
                .type(Category.CategoryType.EXPENSE)
                .build(),
            Category.builder()
                .name("Pendidikan")
                .description("Buku dan biaya kursus")
                .type(Category.CategoryType.EXPENSE)
                .build(),
            Category.builder()
                .name("Investasi")
                .description("Dana untuk saham atau reksadana")
                .type(Category.CategoryType.EXPENSE)
                .build(),
            Category.builder()
                .name("Donasi")
                .description("Zakat dan sumbangan sosial")
                .type(Category.CategoryType.EXPENSE)
                .build(),
            Category.builder()
                .name("Kesehatan")
                .description("Biaya kesehatan dan obat-obatan")
                .type(Category.CategoryType.EXPENSE)
                .build(),
            Category.builder()
                .name("Lainnya (Pengeluaran)")
                .description("Kategori pengeluaran lainnya")
                .type(Category.CategoryType.EXPENSE)
                .build()
        );
        
        categoryRepository.saveAll(categories);
        log.info("Successfully seeded {} categories ({} income, {} expense)", 
                categories.size(),
                categories.stream().filter(c -> c.getType() == Category.CategoryType.INCOME).count(),
                categories.stream().filter(c -> c.getType() == Category.CategoryType.EXPENSE).count());
    }
}

