package com.budgettracker.controller;

import com.budgettracker.dto.request.CreateUserRequest;
import com.budgettracker.dto.request.UpdateUserRequest;
import com.budgettracker.dto.response.ApiResponse;
import com.budgettracker.dto.response.UserResponse;
import com.budgettracker.service.FileStorageService;
import com.budgettracker.service.UserService;
import com.budgettracker.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "API untuk mengelola user dan profile picture")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    
    private final UserService userService;
    private final FileStorageService fileStorageService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        List<UserResponse> users = userService.getAll();
        return ResponseEntity.ok(ApiResponse.success("User Berhasil Di dapat!", users));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Integer id) {
        UserResponse user = userService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("User Berhasil Di dapat!", user));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(
            @Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User berhasil dibuat", user));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("User berhasil di update", user));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("User berhasil di hapus", null));
    }
    
    @Operation(
            summary = "Upload profile picture",
            description = "Upload foto profil user. Format yang didukung: JPG, PNG, GIF, WEBP. Max size: 5MB. Memerlukan JWT token."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Foto profil berhasil diupload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "File tidak valid atau terlalu besar"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/profile/picture")
    public ResponseEntity<ApiResponse<UserResponse>> uploadProfilePicture(
            @Parameter(description = "File gambar (JPG, PNG, GIF, WEBP, max 5MB)", required = true)
            @RequestParam("file") MultipartFile file) {
        Integer userId = SecurityUtil.getCurrentUserId();
        
        String filePath = fileStorageService.storeProfilePicture(file, userId);
        UserResponse user = userService.updateProfilePicture(userId, filePath);
        
        return ResponseEntity.ok(ApiResponse.success("Foto profil berhasil diupload", user));
    }
    
    @GetMapping("/profile/picture")
    public ResponseEntity<byte[]> getProfilePicture(
            @RequestParam(required = false) Integer userId) {
        Integer targetUserId = userId != null ? userId : SecurityUtil.getCurrentUserId();
        
        // Get actual file path from entity (not URL)
        com.budgettracker.entity.User userEntity = userService.getEntityById(targetUserId);
        
        if (userEntity.getProfilePicture() == null || userEntity.getProfilePicture().isEmpty()) {
            throw new com.budgettracker.exception.NotFoundException("Foto profil tidak ditemukan");
        }
        
        byte[] imageBytes = userService.getProfilePicture(userEntity.getProfilePicture());
        
        // Determine content type from file extension
        String contentType = MediaType.IMAGE_JPEG_VALUE;
        String filePath = userEntity.getProfilePicture().toLowerCase();
        if (filePath.endsWith(".png")) {
            contentType = MediaType.IMAGE_PNG_VALUE;
        } else if (filePath.endsWith(".gif")) {
            contentType = MediaType.IMAGE_GIF_VALUE;
        } else if (filePath.endsWith(".webp")) {
            contentType = "image/webp";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(imageBytes);
    }
    
    @DeleteMapping("/profile/picture")
    public ResponseEntity<ApiResponse<Object>> deleteProfilePicture() {
        Integer userId = SecurityUtil.getCurrentUserId();
        UserResponse user = userService.getById(userId);
        
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            fileStorageService.deleteFile(user.getProfilePicture());
            userService.updateProfilePicture(userId, null);
        }
        
        return ResponseEntity.ok(ApiResponse.success("Foto profil berhasil dihapus", null));
    }
}

