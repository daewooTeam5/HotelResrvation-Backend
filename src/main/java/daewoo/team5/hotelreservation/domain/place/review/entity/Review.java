package daewoo.team5.hotelreservation.domain.place.review.entity;

import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.global.core.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주석: 리뷰 정보를 담는 엔티티 클래스입니다.
@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    // 주석: 리뷰는 특정 장소(숙소)에 속합니다. (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Places place;

    // 주석: 리뷰는 사용자가 작성합니다. (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // 주석: 리뷰는 하나의 예약에 대해 작성됩니다. (일대일 관계)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Column(nullable = false)
    private Integer rating; // 평점 (1~5)

    @Lob // TEXT 타입 매핑
    private String comment; // 리뷰 내용

    public static Review createReview(Places place, Users user, Reservation reservation, Integer rating, String comment) {
        Review review = new Review();
        review.place = place;
        review.user = user;
        review.reservation = reservation;
        review.rating = rating;
        review.comment = comment;
        return review;
    }
}