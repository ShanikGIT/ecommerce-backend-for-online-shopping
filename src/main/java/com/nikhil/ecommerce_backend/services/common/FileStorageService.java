package com.nikhil.ecommerce_backend.services.common;

import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;

public interface FileStorageService {

    String storeProductImage(MultipartFile file, String subDirectory);

    void storeUserImage(MultipartFile file, Long userId);

    Optional<String> findUserImageName(Long userId);

    void deleteFile(String fileName, String subDirectory);

    Optional<String> findProductImage(String image);
}