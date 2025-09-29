package daewoo.team5.hotelreservation.domain.payment.entity;

import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.global.core.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    // 예약자 (User 연관관계)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private GuestEntity guest;

    @ManyToOne(fetch = FetchType.EAGER)
    private Room room;

    @Column
    private Long resevAmount;

    @Column
    private String orderId;

    // 예약 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status",
            columnDefinition = "ENUM('pending','confirmed','cancelled','checked_in','checked_out') DEFAULT 'pending'")
    private ReservationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status",
            columnDefinition = "ENUM('unpaid','paid','refunded','rejected','cancelled') DEFAULT 'unpaid'")
    private ReservationPaymentStatus paymentStatus;


    @Column(name = "amount", nullable = false, precision = 38, scale = 2, columnDefinition = "DECIMAL(38, 2) DEFAULT 0")
    private BigDecimal baseAmount;

    @Column(name = "final_amount", nullable = false, precision = 38, scale = 2)
    private BigDecimal finalAmount;

    // 고정 할인 가격 DISCOUNT 테이블의 평균 할인가
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer fixedDiscountAmount;

    // 쿠폰 할인 가격 COUPON 테이블의 할인 가격
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer couponDiscountAmount;

    // 포인트 할인 가격 GUEST 테이블의 보유 포인트
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer pointDiscountAmount;


    @Column(name = "resev_start")
    private LocalDate resevStart;

    @Column(name = "resev_end")
    private LocalDate resevEnd;



    @Column
    private String request;

    // 예약 상태 Enum
    public enum ReservationStatus {
        pending, confirmed, cancelled, checked_in, checked_out
    }

    // 결제 상태 Enum
    public enum ReservationPaymentStatus {
        unpaid, paid, refunded
    }
}