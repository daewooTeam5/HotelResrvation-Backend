package daewoo.team5.hotelreservation.domain.users.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity(name = "OwnerRequest")
@Table(name = "owner_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 오너 승인 ID (PK)

    private String rejectionReason; // 반려 사유

    private String businessNumber;

    @ManyToOne
    private Users user; // 요청자 ID (FK)

    @Enumerated(EnumType.STRING)
    @Column
    private Status status; // 승인 여부

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }
}
