package daewoo.team5.hotelreservation.domain.place.review.service;

import daewoo.team5.hotelreservation.domain.payment.entity.GuestEntity;
import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.payment.repository.GuestRepository;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.place.review.dto.CreateReviewRequest;
import daewoo.team5.hotelreservation.domain.place.review.dto.ReviewResponse;
import daewoo.team5.hotelreservation.domain.place.review.entity.Review;
import daewoo.team5.hotelreservation.domain.place.review.repository.ReviewRepository;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// 주석: 리뷰 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final UsersRepository usersRepository; // Users 엔티티 조회를 위해 추가
    private final GuestRepository guestRepository;
    private final PlaceRepository placeRepository;

    /**
     * 주석: 새로운 리뷰를 생성합니다.
     * @param placeId 숙소 ID
     * @param request 리뷰 생성 요청 DTO
     * @param userProjection 리뷰를 작성하는 사용자 정보
     * @return 생성된 리뷰 정보
     */
    public ReviewResponse createReview(Long placeId, CreateReviewRequest request, UserProjection userProjection) {
        // 주석: UserProjection에서 Users 엔티티를 조회합니다.
        Users user = usersRepository.findById(userProjection.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", "존재하지 않는 사용자입니다."));
        GuestEntity currentGuest = guestRepository.findByUsersId(user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "게스트 정보를 찾을 수 없습니다.", "게스트 정보가 존재하지 않습니다."));

        Reservation reservation = reservationRepository.findTop1ByRoom_Place_IdAndGuest_IdOrderByCreatedAtDesc(placeId, currentGuest.getId())
                .orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND, "예약 정보를 찾을 수 없습니다.", "해당 숙소에 대한 예약 정보가 존재하지 않습니다."));
        // 주석: 요청된 예약 ID로 예약 정보를 조회합니다.
//        Reservation reservation = reservationRepository.findById(request.getReservationId())
//                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "예약 정보를 찾을 수 없습니다.", "존재하지 않는 예약입니다."));
//
        // === 리뷰 작성 권한 검증 ===
        // 주석: 예약한 사용자와 현재 로그인한 사용자가 동일한지 확인합니다.
        if (!reservation.getGuest().getId().equals(currentGuest.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "리뷰 작성 권한이 없습니다.", "예약자 본인만 리뷰를 작성할 수 있습니다.");
        }
        // 주석: 리뷰를 작성하려는 숙소와 실제 예약한 숙소가 동일한지 확인합니다.
        if (!reservation.getRoom().getPlace().getId().equals(placeId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 접근입니다.", "예약한 숙소와 다른 숙소에 리뷰를 작성할 수 없습니다.");
        }

        // TODO: 체크아웃 상태를 확인하는 로직을 추가하면 더 좋습니다.
//         if (reservation.getStatus() != Reservation.ReservationStatus.checked_out) {
//             throw new ApiException(HttpStatus.BAD_REQUEST, "리뷰 작성 조건이 아닙니다.", "리뷰는 체크아웃이 완료된 예약에 대해서만 작성할 수 있습니다.");
//         }
        Places places = placeRepository.findById(placeId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "숙소를 찾을 수 없습니다.", "존재하지 않는 숙소입니다."));

        // 주석: Review 엔티티를 생성하고 데이터베이스에 저장합니다.
        Review review = Review.createReview(places, user, reservation, request.getRating(), request.getComment());
        reviewRepository.save(review);

        // 주석: 생성된 리뷰 정보를 DTO로 변환하여 반환합니다.
        return new ReviewResponse(review);
    }

    /**
     * 주석: 특정 숙소의 모든 리뷰를 조회합니다.
     * @param placeId 조회할 숙소의 ID
     * @return 해당 숙소의 모든 리뷰 목록
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByPlace(Long placeId) {
        return reviewRepository.findByPlaceIdOrderByCreatedAtDesc(placeId)
                .stream()
                .map(ReviewResponse::new)
                .collect(Collectors.toList());
    }
}