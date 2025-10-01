package daewoo.team5.hotelreservation.domain.users.service;

import daewoo.team5.hotelreservation.domain.auth.repository.UserFcmRepository;
import daewoo.team5.hotelreservation.domain.coupon.repository.UserCouponRepository;
import daewoo.team5.hotelreservation.domain.notification.entity.NotificationEntity;
import daewoo.team5.hotelreservation.domain.notification.repository.NotificationRepository;
import daewoo.team5.hotelreservation.domain.payment.entity.PointHistoryEntity;
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
import daewoo.team5.hotelreservation.infrastructure.firebasefcm.FcmService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserFcmRepository userFcmRepository;
    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;
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

    @Transactional
    public void addPoints(Long userId, long amount, String reason) {
        Users user = usersRepository.findById(userId)
                .orElseThrow();

        long currentPoints = user.getPoint() != null ? user.getPoint() : 0L;
        long newBalance = currentPoints + amount;
        user.setPoint(newBalance);

        PointHistoryEntity history = PointHistoryEntity.builder()
                .user(user)
                .reservation(null) // 관리자 지급
                .type(PointHistoryEntity.PointType.EARN)
                .amount(amount)
                .balanceAfter(newBalance)
                .description(reason)
                .expireAt(LocalDate.now().plusYears(1))
                .createdAt(LocalDateTime.now())
                .build();

        pointHistoryRepository.save(history);

        userFcmRepository.findByUserId(userId).ifPresent(userFcm -> {
            String token = userFcm.getToken();
            if (token != null && !token.isEmpty()) {
                try {
                    String title = "포인트 지급";
                    String body = reason + "으로 인해 " + amount + " 포인트가 지급되었습니다.";

                    fcmService.sendToToken(token, title, body, null);

                    NotificationEntity notification = NotificationEntity.builder()
                            .title(title)
                            .content(body)
                            .notificationType(NotificationEntity.NotificationType.ADMIN)
                            .user(user)
                            .build();
                    notificationRepository.save(notification);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Transactional
    public void deductPoints(Long userId, long amount, String reason) {
        if (amount <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "", "차감할 포인트는 0보다 커야 합니다.");
        }

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "", "유저를 찾을 수 없습니다."));

        long currentPoints = user.getPoint() != null ? user.getPoint() : 0L;
        if (currentPoints < amount) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "", "보유 포인트 부족");
        }

        long newBalance = currentPoints - amount;
        user.setPoint(newBalance); // 사용자 포인트 업데이트

        // 🔹 포인트 히스토리 저장
        PointHistoryEntity history = PointHistoryEntity.builder()
                .user(user)
                .reservation(null)
                .type(PointHistoryEntity.PointType.USE) // 차감
                .amount(amount)
                .balanceAfter(newBalance)
                .description(reason)
                .createdAt(LocalDateTime.now())
                .build();

        pointHistoryRepository.save(history);

        // 🔹 알림 발송 및 저장
        userFcmRepository.findByUserId(userId).ifPresent(userFcm -> {
            String token = userFcm.getToken();
            if (token != null && !token.isEmpty()) {
                try {
                    String title = "포인트 차감";
                    String body = reason + "으로 인해 " + amount + " 포인트가 차감되었습니다.";

                    // FCM 푸시 알림 전송
                    fcmService.sendToToken(token, title, body, null);

                    // Notification 엔티티 저장
                    NotificationEntity notification = NotificationEntity.builder()
                            .title(title)
                            .content(body)
                            .notificationType(NotificationEntity.NotificationType.ADMIN)
                            .user(user)
                            .build();
                    notificationRepository.save(notification);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}