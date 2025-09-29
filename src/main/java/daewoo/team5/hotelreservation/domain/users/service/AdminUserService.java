package daewoo.team5.hotelreservation.domain.users.service;

import daewoo.team5.hotelreservation.domain.coupon.repository.UserCouponRepository;
import daewoo.team5.hotelreservation.domain.payment.repository.PointHistoryRepository;
import daewoo.team5.hotelreservation.domain.place.repository.PaymentRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.place.review.repository.ReviewCommentRepository;
import daewoo.team5.hotelreservation.domain.place.review.repository.ReviewImageRepository;
import daewoo.team5.hotelreservation.domain.place.review.repository.ReviewRepository;
import daewoo.team5.hotelreservation.domain.question.repository.QuestionRepository;
import daewoo.team5.hotelreservation.domain.users.dto.request.UserAllDataDTO;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserCouponRepository userCouponRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final QuestionRepository questionRepository;
    private final UsersRepository usersRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public UserAllDataDTO getAllUserData(Long userId) {
        return UserAllDataDTO.builder()
                .user(usersRepository.findProjectedById(userId)
                        .orElseThrow(() -> new RuntimeException("유저 없음")))
                .coupons(userCouponRepository.findCouponsByUserId(userId))
                .points(pointHistoryRepository.findPointsByUserId(userId))
                .reservations(reservationRepository.findReservationsByUserId(userId))
                .payments(paymentRepository.findPaymentsByUserId(userId))
                .reviews(reviewRepository.findReviewsByUserId(userId))
                .reviewImages(reviewImageRepository.findReviewImagesByUserId(userId))
                .reviewComments(reviewCommentRepository.findReviewCommentsByUserId(userId))
                .questions(questionRepository.findQuestionsByUserId(userId))
                .build();
    }

}