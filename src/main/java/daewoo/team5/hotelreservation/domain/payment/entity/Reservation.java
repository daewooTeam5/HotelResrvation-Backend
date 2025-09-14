package daewoo.team5.hotelreservation.domain.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    // 문자열 룸코드 (예: "A101" 또는 숫자가 들어올 수도 있음)
    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    // 예약 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status",
            columnDefinition = "ENUM('pending','confirmed','cancelled','checked_in','checked_out') DEFAULT 'pending'")
    private ReservationStatus status;

    @Column(name = "amount", nullable = false, precision = 38, scale = 2)
    private BigDecimal amount;

    // 스키마가 varchar(255)라 문자열로 유지
    @Column(name = "resev_start")
    private String resevStart;

    @Column(name = "resev_end")
    private String resevEnd;

    // 결제 상태 (캐시 컬럼: B안) - reservations.payment_status
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status",
            columnDefinition = "ENUM('unpaid','paid','refunded') DEFAULT 'unpaid'")
    private ReservationPaymentStatus paymentStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 예약 상태 Enum
    public enum ReservationStatus {
        pending, confirmed, cancelled, checked_in, checked_out
    }

    // 예약의 결제 진행 상태(캐시)
    public enum ReservationPaymentStatus {
        unpaid, paid, refunded
    }
}
