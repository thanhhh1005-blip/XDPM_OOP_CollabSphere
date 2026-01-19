package com.collabsphere.identity.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Lấy email gửi từ file config (application-secret.yml)
    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async // Chạy ở luồng riêng, không làm user phải chờ
    public void sendWelcomeEmail(String toEmail, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("🎉 Chào mừng bạn gia nhập hệ thống!");

            // Nội dung HTML đơn giản, đẹp mắt
            String htmlContent = """
                <div style="font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                    <h2 style="color: #007bff;">Xin chào %s!</h2>
                    <p>Chúc mừng bạn đã đăng ký tài khoản thành công thông qua Google.</p>
                    <p>Bây giờ bạn có thể trải nghiệm đầy đủ các tính năng của chúng tôi.</p>
                    <br>
                    <p>Trân trọng,<br><b>Admin Team</b></p>
                </div>
                """.formatted(fullName);

            helper.setText(htmlContent, true); // true = gửi dạng HTML

            mailSender.send(message);
            System.out.println("✅ Đã gửi mail thành công đến: " + toEmail);

        } catch (MessagingException e) {
            System.err.println("❌ Lỗi gửi mail: " + e.getMessage());
        }
    }
}