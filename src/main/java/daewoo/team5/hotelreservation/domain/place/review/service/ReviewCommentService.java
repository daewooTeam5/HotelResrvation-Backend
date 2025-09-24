// src/main/java/daewoo/team5/hotelreservation/domain/place/review/service/ReviewCommentService.java
package daewoo.team5.hotelreservation.domain.place.review.service;

import daewoo.team5.hotelreservation.domain.place.review.dto.CreateReviewCommentRequest;
import daewoo.team5.hotelreservation.domain.place.review.entity.Review;
import daewoo.team5.hotelreservation.domain.place.review.entity.ReviewComment;
import daewoo.team5.hotelreservation.domain.place.review.repository.ReviewCommentRepository;
import daewoo.team5.hotelreservation.domain.place.review.repository.ReviewRepository;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommentService {

    private final ReviewRepository reviewRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final UsersRepository usersRepository;

    public void createComment(Long reviewId, CreateReviewCommentRequest request, UserProjection userProjection) {
        // ✅ [수정] findById 대신 새로 만든 메서드를 사용하여 Review를 조회합니다.
        Review review = reviewRepository.findByIdWithPlaceAndOwner(reviewId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다.", "존재하지 않는 리뷰입니다."));

        Users manager = usersRepository.findById(userProjection.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", "존재하지 않는 사용자입니다."));

        // 주석: 이제 review.getPlace().getOwner()는 절대 null이 아닙니다.
        if (!review.getPlace().getOwner().getId().equals(manager.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "권한 없음", "숙소 관리자만 댓글을 작성할 수 있습니다.");
        }

        if (review.getCommentByOwner() != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "중복 작성 불가", "이미 관리자 댓글이 등록된 리뷰입니다.");
        }

        ReviewComment comment = ReviewComment.builder()
                .comment(request.getComment())
                .review(review)
                .user(manager)
                .build();

        reviewCommentRepository.save(comment);
    }
}