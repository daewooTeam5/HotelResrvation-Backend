package daewoo.team5.hotelreservation.domain.users.entity;


import daewoo.team5.hotelreservation.global.core.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "OwnerRequest")
@Table(name = "owner_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerRequestEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 오너 승인 ID (PK)

    private String rejectionReason; // 반려 사유

    private String businessNumber;

    private LocalDateTime decisionAt; // 승인/거절 일시

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
