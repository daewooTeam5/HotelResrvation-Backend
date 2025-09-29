// src/main/java/daewoo/team5/hotelreservation/domain/place/review/service/ReviewService.java
package daewoo.team5.hotelreservation.domain.place.review.service;

import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.payment.repository.GuestRepository;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.place.review.dto.CreateReviewRequest;
import daewoo.team5.hotelreservation.domain.place.review.dto.ReviewDto;
import daewoo.team5.hotelreservation.domain.place.review.dto.ReviewResponse;
import daewoo.team5.hotelreservation.domain.place.review.dto.ReviewResponseDto;
import daewoo.team5.hotelreservation.domain.place.review.entity.Review;
import daewoo.team5.hotelreservation.domain.place.review.entity.ReviewImage;
import daewoo.team5.hotelreservation.domain.place.review.projection.ReviewCommentProjection;
import daewoo.team5.hotelreservation.domain.place.review.projection.ReviewImageProjection;
import daewoo.team5.hotelreservation.domain.place.review.projection.ReviewProjection;
import daewoo.team5.hotelreservation.domain.place.review.repository.ReviewImageRepository;
import daewoo.team5.hotelreservation.domain.place.review.repository.ReviewRepository;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final UsersRepository usersRepository;
    private final GuestRepository guestRepository;
    private final PlaceRepository placeRepository;
    private final ReviewImageRepository reviewImageRepository;

    public ReviewResponse createReview(Long placeId, CreateReviewRequest request, UserProjection userProjection) {
        Users user = usersRepository.findById(userProjection.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", "존재하지 않는 사용자입니다."));

        var currentGuest = guestRepository.findByUsersId(user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "게스트 정보를 찾을 수 없습니다.", "게스트 정보가 존재하지 않습니다."));

        // ✅ [수정] 요청받은 reservationId로 예약 정보를 직접 조회
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "예약 정보를 찾을 수 없습니다.","예약 정보를 찾을 수 없습니다."));

        // === 리뷰 작성 권한 검증 ===
        if (!reservation.getGuest().getUsers().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "리뷰 작성 권한이 없습니다.", "본인의 예약에 대해서만 리뷰를 작성할 수 있습니다.");
        }
        if (!reservation.getRoom().getPlace().getId().equals(placeId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 접근입니다.", "리뷰를 작성하려는 숙소와 예약된 숙소가 다릅니다.");
        }
        if (reviewRepository.existsByReservationReservationId(reservation.getReservationId())) {
            throw new ApiException(HttpStatus.CONFLICT, "리뷰 중복", "이미 해당 예약에 대한 리뷰를 작성하셨습니다.");
        }
        if (reservation.getStatus() != Reservation.ReservationStatus.checked_out) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "리뷰 작성 불가", "체크아웃이 완료된 예약에 대해서만 리뷰를 작성할 수 있습니다.");
        }

        Places places = reservation.getRoom().getPlace();
        Review review = Review.createReview(places, user, reservation, request.getRating(), request.getComment());

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<ReviewImage> reviewImages = request.getImageUrls().stream()
                    .map(url -> ReviewImage.builder().imageUrl(url).review(review).build())
                    .collect(Collectors.toList());
            review.getImages().addAll(reviewImages);
        }

        reviewRepository.save(review);
        return new ReviewResponse(review);
    }



    // getReviewsByPlace, deleteReview, parseSortParameter 메서드는 이전과 동일
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByPlace(Long placeId, String sortBy) {
        Sort sort = parseSortParameter(sortBy);
        return reviewRepository.findByPlaceId(placeId, sort)
                .stream()
                .map(ReviewResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteReview(Long reviewId, UserProjection userProjection) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다.", "존재하지 않는 리뷰입니다."));
        if (!review.getUser().getId().equals(userProjection.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.", "본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }
        reviewRepository.delete(review);
    }

    private Sort parseSortParameter(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sortBy.split(",");
        String property = parts[0].trim();
        Sort.Direction direction = parts.length > 1 ? Sort.Direction.fromString(parts[1].trim()) : Sort.Direction.DESC;

        if (!List.of("createdAt", "rating").contains(property)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 정렬 기준입니다.", "허용되지 않은 정렬 속성입니다: " + property);
        }
        return Sort.by(direction, property);
    }

    public List<ReviewResponseDto> getAllReviews() {
        return reviewRepository.findAllReviewsWithDetails();
    }

    public List<ReviewDto> getReviewsByPlaceId(Long placeId) {
        return reviewRepository.findAllByPlaceIdWithDetails(placeId)
                .stream()
                .map(ReviewDto::fromEntity)
                .toList();
    }

    public List<ReviewProjection> getUserReviews(Long userId) {
        return reviewRepository.findReviewsByUserId(userId);
    }

    public List<ReviewImageProjection> getUserReviewImages(Long userId) {
        return reviewImageRepository.findReviewImagesByUserId(userId);
    }

}