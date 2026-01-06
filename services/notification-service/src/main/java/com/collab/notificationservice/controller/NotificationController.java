@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping("/user/{userId}")
    public ApiResponse<?> getMyNotifications(@PathVariable Long userId) {
        return new ApiResponse<>(
                true,
                "Get notifications success",
                service.getByUser(userId)
        );
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<?> markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
        return new ApiResponse<>(true, "Marked as read", null);
    }
}
