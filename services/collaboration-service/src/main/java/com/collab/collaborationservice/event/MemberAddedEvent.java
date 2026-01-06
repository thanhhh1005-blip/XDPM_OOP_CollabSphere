@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberAddedEvent {
    private Long collaborationId;
    private String userId;
    private String role;
}
