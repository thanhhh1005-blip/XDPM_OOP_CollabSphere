package com.collab.workspaceservice.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final MinioClient minioClient;

    // Lấy các thông số từ file application.yml
    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    public String storeFile(MultipartFile file) {
        try {
            // 1. Tạo tên file ngẫu nhiên để tránh trùng lặp
            // Ví dụ: 550e8400-e29b..._baitap.zip
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
            // 2. Chuẩn bị luồng dữ liệu
            InputStream inputStream = file.getInputStream();
            
            // 3. Đẩy file lên MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName) // Tên bucket: collab-sphere
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 4. Trả về đường dẫn đầy đủ để Frontend có thể truy cập
            // Kết quả: http://localhost:9000/collab-sphere/ten-file.zip
            return minioUrl + "/" + bucketName + "/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi upload file lên MinIO: " + e.getMessage());
        }
    }
}