package com.nikhil.ecommerce_backend.services.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path baseStorageLocation;
    private final Path userImageLocation;
    private final Path productImageLocation;
    private static final String[] ALLOWED_EXTENSIONS = {"jpeg", "jpg", "png", "bmp"};

    public FileStorageServiceImpl(@Value("${file.upload-dir}") String uploadDir) {
        this.baseStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.userImageLocation = this.baseStorageLocation.resolve("users");
        this.productImageLocation = this.baseStorageLocation.resolve("products");

        try {
            Files.createDirectories(baseStorageLocation);
            Files.createDirectories(userImageLocation);
            Files.createDirectories(productImageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the upload directories.", ex);
        }
    }

    @Override
    public String storeProductImage(MultipartFile file, String subDirectory) {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        String storedFileName = UUID.randomUUID() + extension;

        try {
            Path targetLocation = this.baseStorageLocation.resolve(subDirectory).resolve(storedFileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return storedFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + storedFileName, ex);
        }
    }

    @Override
    public void storeUserImage(MultipartFile file, Long userId) {
        findUserImageName(userId).ifPresent(oldFile -> deleteFile(oldFile, "users"));

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
        String storedFileName = userId + "." + extension; // User images have a predictable name

        try {
            Path targetLocation = this.userImageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store user image " + storedFileName, ex);
        }
    }

    @Override
    public Optional<String> findUserImageName(Long userId) {
        for (String extension : ALLOWED_EXTENSIONS) {
            Path potentialFile = userImageLocation.resolve(userId + "." + extension);
            if (Files.exists(potentialFile)) {
                return Optional.of(userId + "." + extension);
            }
        }
        return Optional.empty();
    }
    public Optional<String> findProductImage(String imageName) {

            Path potentialFile = productImageLocation.resolve(imageName);
            if (Files.exists(potentialFile)) {
                return Optional.of(imageName);
            }
        return Optional.empty();
    }

    @Override
    public void deleteFile(String fileName, String subDirectory) {
        try {
            Path filePath = this.baseStorageLocation.resolve(subDirectory).resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            System.err.println("Could not delete file: " + fileName + " from " + subDirectory);
        }
    }
}