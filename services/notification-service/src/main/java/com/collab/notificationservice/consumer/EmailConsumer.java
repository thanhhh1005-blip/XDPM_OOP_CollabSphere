package com.collab.notificationservice.consumer;

import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class EmailConsumer {

    @Autowired
    private JavaMailSender mailSender; // Tự động nhận cấu hình từ application.yml

    @RabbitListener(queues = "notification.queue")
    public void receiveMessage(Map<String, Object> message) {
        try {
            // 1. Lấy dữ liệu từ tin nhắn RabbitMQ
            String to = (String) message.get("to");
            String subject = (String) message.get("subject");
            String body = (String) message.get("body");

            System.out.println("📩 [SMTP] Đang gửi mail tới: " + to);

            // 2. Tạo nội dung email
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom("Thanhhh2005@gmail.com"); // Người gửi (Phải trùng với username trong yml)
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true = Cho phép HTML (In đậm, link...)

            // 3. Gửi đi (Dùng App Password cấu hình sẵn)
            mailSender.send(mimeMessage);
            
            System.out.println("✅ [SMTP] Gửi thành công!");

        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi mail: " + e.getMessage());
        }
    }
}