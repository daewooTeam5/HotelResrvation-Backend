package daewoo.team5.hotelreservation.domain.payment.entity;

import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.place.entity.RoomNo;
import daewoo.team5.hotelreservation.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    // 예약자 (User 연관관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // 객실번호(RoomNo 연관관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private RoomNo roomNo;

    // 예약 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status",
            columnDefinition = "ENUM('pending','confirmed','cancelled','checked_in','checked_out') DEFAULT 'pending'")
    private ReservationStatus status;

    @Column(name = "amount", nullable = false, precision = 38, scale = 2)
    private BigDecimal amount;

    @Column(name = "resev_start")
    private LocalDateTime resevStart;

    @Column(name = "resev_end")
    private LocalDateTime resevEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status",
            columnDefinition = "ENUM('unpaid','paid','refunded') DEFAULT 'unpaid'")
    private ReservationPaymentStatus paymentStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    public Room getRoom() {
        return this.roomNo != null ? this.roomNo.getRoom() : null;
    }

    @Transient
    public String getRoomNoString() {
        return this.roomNo != null ? this.roomNo.getRoomNo() : null;
    }

    // 예약 상태 Enum
    public enum ReservationStatus {
        pending, confirmed, cancelled, checked_in, checked_out
    }

    // 결제 상태 Enum
    public enum ReservationPaymentStatus {
        unpaid, paid, refunded
    }
}