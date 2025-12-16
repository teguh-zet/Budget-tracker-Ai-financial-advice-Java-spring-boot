package com.budgettracker.service.impl;

import com.budgettracker.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {
    
    @Value("${file.profile-picture-dir:./uploads/profiles}")
    private String profilePictureDir;
    
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    @Override
    public String storeProfilePicture(MultipartFile file, Integer userId) {
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File tidak boleh kosong");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Ukuran file tidak boleh lebih dari 5MB");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Format file tidak didukung. Gunakan JPG, PNG, GIF, atau WEBP");
        }
        
        try {
            // Create directory if not exists
            Path uploadPath = Paths.get(profilePictureDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String filename = "profile_" + userId + "_" + UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(filename);
            
            // Save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Return relative path for database storage (will be used to construct URL)
            return filename;
            
        } catch (IOException e) {
            log.error("Error storing profile picture: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal menyimpan file: " + e.getMessage());
        }
    }
    
    @Override
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("File deleted: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public byte[] loadFileAsBytes(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new RuntimeException("File tidak ditemukan: " + filePath);
            }
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("Error loading file: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal membaca file: " + e.getMessage());
        }
    }
    
    @Override
    public String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
    
    @Override
    public String getProfilePictureDir() {
        return profilePictureDir;
    }
}

