package com.budgettracker.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeProfilePicture(MultipartFile file, Integer userId);
    void deleteFile(String filePath);
    byte[] loadFileAsBytes(String filePath);
    String getFileExtension(String filename);
    String getProfilePictureDir();
}

