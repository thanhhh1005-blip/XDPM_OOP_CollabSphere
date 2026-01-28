package com.collab.resourceservice.service;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class NifiClient {

    // Hàm này nhận file và tên endpoint (ví dụ: "classes", "subjects")
    public void sendFile(MultipartFile file, String endpoint) {
        // Nếu chạy Docker thì dùng "http://nifi:8091"
        // Nếu chạy Local thì dùng "http://localhost:8091"
        String nifiUrl = "http://localhost:8099/contentListener/" + endpoint;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadFile = new HttpPost(nifiUrl);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file.getInputStream(), ContentType.DEFAULT_BINARY, file.getOriginalFilename());
            
            uploadFile.setEntity(builder.build());

            System.out.println("🚀 Đang gửi file sang NiFi: " + nifiUrl);
            
            httpClient.execute(uploadFile, response -> {
                if (response.getCode() != 200) {
                    throw new RuntimeException("NiFi từ chối nhận file! Code: " + response.getCode());
                }
                return null;
            });

        } catch (IOException e) {
            throw new RuntimeException("Lỗi kết nối NiFi: " + e.getMessage());
        }
    }
}
