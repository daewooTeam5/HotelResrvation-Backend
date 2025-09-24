// src/main/java/daewoo/team5/hotelreservation/domain/place/review/entity/Review.java
package daewoo.team5.hotelreservation.domain.place.review.entity;

import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.global.core.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Places place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Column(nullable = false)
    private Integer rating; // 평점 (1~5)

    @Lob
    private String comment; // 리뷰 내용

    // 주석: 리뷰에 달린 이미지 목록입니다. (일대다 관계)
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> images = new ArrayList<>();

    // 주석: 리뷰에 달린 관리자 댓글입니다. (일대일 관계)
    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ReviewComment commentByOwner;

    public static Review createReview(Places place, Users user, Reservation reservation, Integer rating, String comment) {
        Review review = new Review();
        review.place = place;
        review.user = user;
        review.reservation = reservation;
        review.rating = rating;
        review.comment = comment;
        return review;
    }

    // 주석: 연관관계 편의 메서드입니다. 리뷰에 이미지를 추가합니다.
    public void addImage(ReviewImage image) {
        images.add(image);
    }
}