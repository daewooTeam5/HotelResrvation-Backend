package daewoo.team5.hotelreservation.domain.coupon.entity;

import daewoo.team5.hotelreservation.domain.users.entity.Users;
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
@Entity(name = "UserCoupon")
@Table(
        name = "user_coupon"
)
public class UserCouponEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean isUsed; // 사용여부

    @Column(nullable = false)
    private LocalDateTime issuedAt; // 발급 받은 날짜

    @ManyToOne()
    private Users user; // 발급받은 유저 ID

    @ManyToOne()
    private CouponEntity coupon; // 쿠폰 ID

}
