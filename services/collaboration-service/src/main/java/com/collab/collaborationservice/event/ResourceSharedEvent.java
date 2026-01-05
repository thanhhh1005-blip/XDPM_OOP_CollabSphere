@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceSharedEvent {
    private Long collaborationId;
    private Long resourceId;
    private String sharedBy;
}
