package daewoo.team5.hotelreservation.domain.users.service;

import daewoo.team5.hotelreservation.domain.coupon.repository.UserCouponRepository;
import daewoo.team5.hotelreservation.domain.payment.repository.PointHistoryRepository;
import daewoo.team5.hotelreservation.domain.place.entity.File;
import daewoo.team5.hotelreservation.domain.place.repository.PaymentRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.place.review.repository.ReviewCommentRepository;
import daewoo.team5.hotelreservation.domain.place.review.repository.ReviewImageRepository;
import daewoo.team5.hotelreservation.domain.place.review.repository.ReviewRepository;
import daewoo.team5.hotelreservation.domain.question.repository.QuestionRepository;
import daewoo.team5.hotelreservation.domain.users.dto.request.OwnerRequestDto;
import daewoo.team5.hotelreservation.domain.users.dto.request.UserAllDataDTO;
import daewoo.team5.hotelreservation.domain.users.entity.OwnerRequestEntity;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.repository.OwnerRequestRepository;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    private final OwnerRequestRepository ownerRequestRepository;

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

    public List<OwnerRequestDto> getAllOwnerRequests() {
        List<Object[]> results = usersRepository.findAllUsersWithOwnerRequestAndFiles();
        Map<Long, OwnerRequestDto> dtoMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            Users user = (Users) row[0];
            OwnerRequestEntity orq = (OwnerRequestEntity) row[1]; // 이제 null이 아님
            File file = (File) row[2];

            Long key = user.getId();
            OwnerRequestDto dto = dtoMap.computeIfAbsent(key, k -> new OwnerRequestDto(
                    user.getId(),
                    orq.getId(),
                    user.getUserId(),
                    user.getEmail(),
                    user.getName(),
                    user.getPhone(),
                    user.getRole(),
                    user.getStatus(),
                    orq.getStatus(), // null 체크 제거
                    orq.getRejectionReason(), // null 체크 제거
                    orq.getBusinessNumber(),
                    new ArrayList<>()
            ));

            if (file != null) {
                dto.getOwnerRequestFiles().add(file.getUrl());
            }
        }

        return new ArrayList<>(dtoMap.values());
    }

    public void approveOwnerRequest(Long requestId) {
        OwnerRequestEntity request = ownerRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 요청을 찾을 수 없습니다."));

        // 요청 상태 변경
        request.setStatus(OwnerRequestEntity.Status.APPROVED);
        request.setRejectionReason(null);

        // 유저 권한을 hotel_owner로 변경
        Users user = request.getUser();
        user.setRole(Users.Role.hotel_owner);
        usersRepository.save(user);

        ownerRequestRepository.save(request);
    }

    public void rejectOwnerRequest(Long requestId, String reason) {
        OwnerRequestEntity request = ownerRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 요청을 찾을 수 없습니다."));
        request.setStatus(OwnerRequestEntity.Status.REJECTED);
        request.setRejectionReason(reason);
        ownerRequestRepository.save(request);
    }

    public void updateUserStatus(Long userId, String newStatus) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(); // 유저 없으면 NoSuchElementException 발생

        Users.Status statusEnum = Users.Status.valueOf(newStatus);

        user.setStatus(statusEnum);
        usersRepository.save(user); // DB 반영
    }
}