// src/main/java/daewoo/team5/hotelreservation/domain/place/review/entity/ReviewComment.java
package daewoo.team5.hotelreservation.domain.place.review.entity;

import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.global.core.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String comment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false, unique = true)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user; // 댓글 작성자 (호텔 관리자)

    @Builder
    public ReviewComment(String comment, Review review, Users user) {
        this.comment = comment;
        this.review = review;
        this.user = user;
    }
}