package daewoo.team5.hotelreservation.domain.payment.entity;

import daewoo.team5.hotelreservation.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "PointHistory")
@Table(name = "point_history")
public class PointHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 포인트 내역 ID

    @ManyToOne
    private Users user; // 지급받은 유저 ID

    @ManyToOne
    private Reservation reservation; // 예약 ID (null 가능)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointType type; // 'earn' 또는 'use'

    @Column(nullable = false)
    private Long amount; // 변환된 포인트

    @Column(name = "balance_after", nullable = false)
    private Long balanceAfter; // 거래 후 포인트

    @Column
    private String description; // 설명

    @Column(name = "expire_at")
    private LocalDate expireAt; // 만료일

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 지급일

    public enum PointType {
        EARN, USE
    }
}