package daewoo.team5.hotelreservation.domain.coupon.entity;

import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "CouponHistory")
@Table(
        name = "coupon_history",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_coupon_history_user_coupon_used_at",
                        columnNames = {"user_coupon_id", "used_at"}
                )
        }
)
public class CouponHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 쿠폰 사용 내역 ID

    // Reservation 엔티티가 있다고 가정!
    @ManyToOne
    private Reservation reservation_id; // 예약 ID

    @ManyToOne()
    private UserCouponEntity userCoupon; // 발급 받은 유저 쿠폰 ID

    @Column(nullable = false)
    private Integer discount_amount; // 할인 금액

    @Column(nullable = false)
    private LocalDateTime used_at; // 쿠폰 사용 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status; // 쿠폰 상태 (used, canceled)

    public enum CouponStatus {
        used,
        canceled
    }
}
