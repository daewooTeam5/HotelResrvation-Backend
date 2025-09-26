package daewoo.team5.hotelreservation.domain.coupon.entity;

import daewoo.team5.hotelreservation.domain.place.entity.Places;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "Coupon")
@Table(name = "coupon")
public class CouponEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // 쿠폰 ID

    @ManyToOne
    private Places place;  // 쿠폰 발급 숙소

    @Column(name = "coupon_name", length = 100, nullable = false)
    private String couponName; // 쿠폰명

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 발급 시간

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt; // 만료 시간

    @Column(name = "amount", nullable = false)
    private Integer amount; // 할인 액수

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_type", nullable = false)
    private CouponType couponType; // 할인 유형 (fixed / rate)

    @Column(name = "coupon_code", length = 100, nullable = false, unique = true)
    private String couponCode; // 쿠폰 번호

    @Column
    private int minOrderAmount;

    @Column
    private int maxOrderAmount;

    public enum CouponType {
        fixed,   // 고정 금액 할인
        rate     // 비율 할인
    }

    public boolean isIssuable(){
        return LocalDateTime.now().isBefore(this.expiredAt);
    }
}
