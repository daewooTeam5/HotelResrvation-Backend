package daewoo.team5.hotelreservation.domain.payment.entity;

import daewoo.team5.hotelreservation.domain.hotel.dto.ReservationDTO;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column(nullable = false)
    private String roomId;

    // 변경된 부분: userId를 int로 변경
    @Column(name = "user_id", nullable = false)
    private int userId; // userId를 int로 설정

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('pending','confirmed','cancelled','checked_in','checked_out') DEFAULT 'pending'")
    private Status status;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private String resevStart;

    @Column
    private String resevEnd;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('unpaid','paid','refunded') DEFAULT 'unpaid'")
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public enum Status {
        pending,
        confirmed,
        cancelled,
        checked_in,
        checked_out
    }

    public enum PaymentStatus {
        unpaid,
        paid,
        refunded
    }

    // DTO로 변환하는 메소드 추가
    public ReservationDTO toDTO() {
        return new ReservationDTO(
                this.reservationId,
                this.userId, // userId를 int로 변환
                this.roomId,
                this.status != null ? this.status.name() : null,
                this.amount,
                this.resevStart,
                this.resevEnd,
                this.paymentStatus != null ? this.paymentStatus.name() : null,
                this.createdAt
        );
    }
}
