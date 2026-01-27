// package com.collab.notificationservice.controller;

// import com.collab.notificationservice.dto.EmailRequestDTO;
// import com.collab.notificationservice.dto.NotificationResponseDTO;
// import com.collab.notificationservice.service.EmailNotificationService;
// import com.collab.notificationservice.service.NotificationService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/users")
// @RequiredArgsConstructor
// public class NotificationController {

//     private final NotificationService notificationService;
//     private final EmailNotificationService emailNotificationService;

    
//    @PostMapping("/email")
// public ResponseEntity<?> sendEmail(@RequestBody EmailRequestDTO request) {
//     emailNotificationService.sendEmail(request);
//     return ResponseEntity.ok("Email sent");
// }


//     /**
//      * =====================================
//      * TEST 2 – LẤY DANH SÁCH NOTIFICATION
//      * =====================================
//      */
//     @GetMapping("/user/{userId}")
// public ResponseEntity<?> getByUser(@PathVariable Long userId) {
//     return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
// }
    

//     /**
//      * =====================================
//      * TEST 3 – ĐÁNH DẤU ĐÃ ĐỌC
//      * =====================================
//      */
//     @PutMapping("/{id}/read")
// public ResponseEntity<?> markAsRead(@PathVariable Long id) {
//     notificationService.markAsRead(id);
//     return ResponseEntity.ok().build();
// }

//     /**
//      * =====================================
//      * TEST 4 – HEALTH CHECK
//      * =====================================
//      */
//     @GetMapping("/health")
//     public ResponseEntity<String> health() {
//         return new ResponseEntity<>("Notification Service is UP", HttpStatus.OK);
//     }
// }
