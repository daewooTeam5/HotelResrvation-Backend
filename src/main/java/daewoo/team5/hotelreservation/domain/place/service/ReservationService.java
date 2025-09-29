package daewoo.team5.hotelreservation.domain.place.service;


import daewoo.team5.hotelreservation.domain.auth.service.AuthService;
import daewoo.team5.hotelreservation.domain.coupon.entity.CouponHistoryEntity;
import daewoo.team5.hotelreservation.domain.coupon.entity.UserCouponEntity;
import daewoo.team5.hotelreservation.domain.coupon.repository.CouponHistoryRepository;
import daewoo.team5.hotelreservation.domain.coupon.repository.CouponRepository;
import daewoo.team5.hotelreservation.domain.coupon.repository.UserCouponRepository;

import daewoo.team5.hotelreservation.domain.notification.entity.NotificationEntity;
import daewoo.team5.hotelreservation.domain.notification.repository.NotificationRepository;

import daewoo.team5.hotelreservation.domain.payment.dto.TossCancelResponse;
import daewoo.team5.hotelreservation.domain.payment.entity.GuestEntity;
import daewoo.team5.hotelreservation.domain.payment.entity.Payment;
import daewoo.team5.hotelreservation.domain.payment.projection.PaymentProjection;
import daewoo.team5.hotelreservation.domain.payment.entity.PointHistoryEntity;
import daewoo.team5.hotelreservation.domain.payment.projection.ReservationInfoProjection;
import daewoo.team5.hotelreservation.domain.payment.projection.ReservationProjection;
import daewoo.team5.hotelreservation.domain.payment.repository.GuestRepository;
import daewoo.team5.hotelreservation.domain.payment.repository.PointHistoryRepository;
import daewoo.team5.hotelreservation.domain.payment.service.TossPaymentService;
import daewoo.team5.hotelreservation.domain.place.dto.*;
import daewoo.team5.hotelreservation.domain.place.entity.DailyPlaceReservation;
import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.place.repository.DailyPlaceReservationRepository;
import daewoo.team5.hotelreservation.domain.place.repository.PaymentRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.place.repository.RoomRepository;
import daewoo.team5.hotelreservation.domain.place.specification.ReservationSpecification;
import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;
    private final DailyPlaceReservationRepository dailyPlaceReservationRepository;
    private final TossPaymentService tossPaymentService;
    private final GuestRepository guestRepository;
    private final CouponHistoryRepository couponHistoryRepository;
    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final UsersRepository usersRepository;
    private final AuthService authService;

    private final NotificationRepository notificationRepository;

    /**
     * ✅ [추가] 리뷰 작성 가능한 예약 목록을 조회하는 서비스 로직
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<ReviewableReservationResponse> getReviewableReservations(Long placeId, UserProjection user) {
        if (user == null) {
            return List.of(); // 비로그인 시 빈 목록 반환
        }
        GuestEntity guest = guestRepository.findByUsersId(user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "투숙객 정보를 찾을 수 없습니다.","투숙객 정보를 찾을 수 없습니다."));

        return reservationRepository.findReviewableReservations(guest.getId(), placeId);
    }
    // ===================== 변환 메서드 =====================

    private ReservationListDTO toListDTO(Reservation r) {
        return ReservationListDTO.builder()
                .reservationId(r.getReservationId())
                .orderId(r.getOrderId())
                .guestName(r.getGuest() != null
                        ? (r.getGuest().getUsers() != null
                        ? r.getGuest().getUsers().getName()
                        : r.getGuest().getFirstName() + " " + r.getGuest().getLastName())
                        : null)
                .roomType(r.getRoom() != null ? r.getRoom().getRoomType() : null)
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .paymentStatus(r.getPaymentStatus() != null ? r.getPaymentStatus().name() : null)
                .resevStart(r.getResevStart())
                .resevEnd(r.getResevEnd())
                .finalAmount(r.getFinalAmount())
                .createdAt(r.getCreatedAt())
                .member(r.getGuest() != null && r.getGuest().getUsers() != null)
                .build();
    }

    private ReservationDetailDTO toDetailDTO(Reservation r) {
        Optional<Payment> paymentOpt =
                paymentRepository.findTop1ByReservation_ReservationIdOrderByTransactionDateDesc(r.getReservationId());

        return ReservationDetailDTO.builder()
                .reservationId(r.getReservationId())
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .paymentStatus(r.getPaymentStatus() != null ? r.getPaymentStatus().name() : null)
                .createdAt(r.getCreatedAt())
                .request(r.getRequest())

                // 예약자 정보
                .userId(r.getGuest() != null && r.getGuest().getUsers() != null
                        ? r.getGuest().getUsers().getId()
                        : null)
                .guestId(r.getGuest() != null ? r.getGuest().getId() : null)
                .guestName(r.getGuest() != null
                        ? (r.getGuest().getUsers() != null
                        ? r.getGuest().getUsers().getName()
                        : r.getGuest().getFirstName() + " " + r.getGuest().getLastName())
                        : null)
                .email(r.getGuest() != null
                        ? (r.getGuest().getUsers() != null
                        ? r.getGuest().getUsers().getEmail()
                        : r.getGuest().getEmail())
                        : null)
                .phone(r.getGuest() != null
                        ? (r.getGuest().getUsers() != null
                        ? r.getGuest().getUsers().getPhone()
                        : r.getGuest().getPhone())
                        : null)
                .member(r.getGuest() != null && r.getGuest().getUsers() != null)

                // 객실 정보
                .roomId(r.getRoom() != null ? r.getRoom().getId() : null)
                .roomType(r.getRoom() != null ? r.getRoom().getRoomType() : null)
                .capacityPeople(r.getRoom() != null ? r.getRoom().getCapacityPeople() : null)
                .price(r.getRoom() != null ? r.getRoom().getPrice() : null)

                // 예약 기간 및 금액
                .resevStart(r.getResevStart())
                .resevEnd(r.getResevEnd())
                .resevAmount(r.getResevAmount())
                .baseAmount(r.getBaseAmount())
                .finalAmount(r.getFinalAmount())

                // 결제 정보
                .paymentId(paymentOpt.map(Payment::getId).orElse(null))
                .method(paymentOpt.map(p -> p.getMethod().name()).orElse(null))
                .paymentStatusDetail(paymentOpt.map(p -> p.getStatus().name()).orElse(null))
                .paymentAmount(paymentOpt.map(Payment::getAmount).orElse(null))
                .transactionDate(paymentOpt.map(Payment::getTransactionDate).orElse(null))

                .build();
    }

    // ===================== 서비스 메서드 =====================

    // 소유자 기반 예약 목록 조회
    public Page<ReservationListDTO> getAllReservations(Long ownerId, Pageable pageable) {
        return reservationRepository.findAllByOwnerId(ownerId, pageable).map(this::toListDTO);
    }

    // 소유자 기반 예약 상세 조회
    public Optional<ReservationDetailDTO> getReservationById(Long reservationId, Long ownerId) {
        return reservationRepository.findByIdAndOwnerId(reservationId, ownerId).map(this::toDetailDTO);
    }

    // 소유자 기반 예약 수정
    @Transactional
    public ReservationDetailDTO updateReservation(Long reservationId, Long ownerId, ReservationRequestDTO dto) {
        return reservationRepository.findByIdAndOwnerId(reservationId, ownerId).map(reservation -> {
            if (dto.getStatus() != null) {
                reservation.setStatus(Reservation.ReservationStatus.valueOf(dto.getStatus()));
            }
            if (dto.getPaymentStatus() != null) {
                reservation.setPaymentStatus(Reservation.ReservationPaymentStatus.valueOf(dto.getPaymentStatus()));
            }
            if (dto.getResevStart() != null) {
                reservation.setResevStart(dto.getResevStart());
            }
            if (dto.getResevEnd() != null) {
                reservation.setResevEnd(dto.getResevEnd());
            }
            Reservation saved = reservationRepository.save(reservation);
            return toDetailDTO(saved);
        }).orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Not Found",
                "해당 소유자의 예약을 찾을 수 없습니다."
        ));
    }

    @Transactional
    public ReservationDetailDTO cancel(Reservation r) {
        // ✅ 결제 정보 확인
        Payment payment = paymentRepository
                .findTop1ByReservation_ReservationIdOrderByTransactionDateDesc(r.getReservationId())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "결제 정보 없음",
                        "해당 예약의 결제 내역을 찾을 수 없습니다."
                ));

        // ✅ 토스 환불 API 호출
        TossCancelResponse response = tossPaymentService.cancelPayment(payment.getPaymentKey(), "고객 예약 취소");

        // ✅ DB 업데이트
        r.setStatus(Reservation.ReservationStatus.cancelled);
        r.setPaymentStatus(Reservation.ReservationPaymentStatus.refunded);

        payment.setStatus(Payment.PaymentStatus.cancelled);
        if (response != null && response.getCancels() != null && !response.getCancels().isEmpty()) {
            TossCancelResponse.CancelHistory lastCancel = response.getCancels().get(response.getCancels().size() - 1);
            payment.setAmount(lastCancel.getCancelAmount()); // 환불 금액 반영
            payment.setTransactionDate(lastCancel.getCanceledAt().toLocalDateTime()); // 환불 시각 반영
        }
        paymentRepository.save(payment);

        // TODO : 취소 로직 확인을 위한 임시 주석
//        // ✅ 재고 복구
//        if (r.getRoom() != null && r.getResevStart() != null && r.getResevEnd() != null) {
//            adjustInventory(r.getRoom().getId(), r.getResevStart(), r.getResevEnd(), +1);
//        }
//
        //
        // 쿠폰 조회후 쿠폰 상태 복구
        if(r.getGuest().getUsers()!=null) {
log.info("Finding coupon history for reservation: {}", r.getReservationId());
            couponHistoryRepository.findByReservation_idWithUsed(r.getReservationId()).ifPresent(ch -> {
                log.info("Cancelling coupon history: {}", ch.getId());
                ch.setStatus(CouponHistoryEntity.CouponStatus.refunded);
                UserCouponEntity userCouponEntity = userCouponRepository.findByUserIdAndCouponId(r.getGuest().getUsers().getId(), ch.getUserCoupon().getCoupon().getId()).orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "UserCoupon Not Found",
                        "해당 쿠폰을 찾을 수 없습니다."
                ));
                userCouponEntity.setUsed(false);
            });
        }
        Reservation saved = reservationRepository.save(r);
        backupPoint(r, r.getGuest() != null && r.getGuest().getUsers() != null ? r.getGuest().getUsers() : null);


        // ✅ 알림 생성 (회원일 경우에만)
        if (r.getGuest() != null && r.getGuest().getUsers() != null) {
            NotificationEntity notification = NotificationEntity.builder()
                    .title("예약이 취소되었습니다")
                    .content("예약번호 " + r.getReservationId() + "번이 취소 및 환불 처리되었습니다.")
                    .notificationType(NotificationEntity.NotificationType.RESERVATION)
                    .user(r.getGuest().getUsers())
                    .build();
            notificationRepository.save(notification);
        }
        return toDetailDTO(saved);

    }
    public void backupPoint(Reservation r,Users users){
        // 로그인 안한 유저면 패스
        // 포인트 적립된값 차감
        PointHistoryEntity pointHistory = pointHistoryRepository.findByReservationAndType(r,PointHistoryEntity.PointType.EARN).orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Point History Not Found",
                "해당 예약의 포인트 내역이 존재하지 않습니다."
        ));
        long balanceAfter = users.getPoint() - pointHistory.getAmount();
        pointHistoryRepository.save(
                PointHistoryEntity.builder()
                        .user(users)
                        .type(PointHistoryEntity.PointType.USE)
                        .amount(pointHistory.getAmount())
                        .balanceAfter(balanceAfter)
                        .description("예약 취소로 인한 포인트 차감 주문 번호 :"+r.getOrderId())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        users.setPoint(balanceAfter);
        // 예약 에 사용한 포인트는 다시 적립
        if(r.getPointDiscountAmount()!=null && r.getPointDiscountAmount()>0){
            long pointAfter = users.getPoint() + r.getPointDiscountAmount();
            pointHistoryRepository.save(
                    PointHistoryEntity.builder()
                            .user(users)
                            .type(PointHistoryEntity.PointType.EARN)
                            .amount(r.getPointDiscountAmount().longValue())
                            .balanceAfter(pointAfter)
                            .description("예약 취소로 인한 사용포인트 복원 주문 번호 :"+r.getOrderId())
                            .createdAt(LocalDateTime.now())
                            .build()
            );
            users.setPoint(pointAfter);
        }

    }

    // 소유자 기반 예약 취소
    @Transactional
    public ReservationDetailDTO cancelOwner(Long reservationId, Long ownerId) {
        Reservation r = reservationRepository.findByIdAndOwnerId(reservationId, ownerId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Not Found",
                        "해당 소유자의 예약을 찾을 수 없습니다."
                ));
        return cancel(r);

    }


    // 소유자 기반 검색
    public Page<ReservationListDTO> searchReservations(ReservationSearchRequest req, Long ownerId, Pageable pageable) {
        return reservationRepository.findAll(
                ReservationSpecification.filter(req, ownerId), pageable
        ).map(this::toListDTO);
    }

    // ===================== 재고 관리 연동 =====================

    /**
     * 재고 조정 유틸 메서드
     * @param roomId 객실 ID
     * @param start 예약 시작일
     * @param end 예약 종료일
     * @param delta 변경 수량 (+1 복구, -1 차감)
     */
    private void adjustInventory(Long roomId, LocalDate start, LocalDate end, int delta) {
        LocalDate date = start;
        while (!date.isAfter(end)) {
            LocalDate currentDate = date; // 🔑 새 변수로 캡처

            DailyPlaceReservation dpr = dailyPlaceReservationRepository
                    .findByRoomIdAndDateForUpdate(roomId, currentDate)
                    .orElseThrow(() -> new ApiException(
                            HttpStatus.NOT_FOUND,
                            "재고 없음",
                            "해당 날짜(" + currentDate + ")에 재고가 존재하지 않습니다."
                    ));

            int updated = dpr.getAvailableRoom() + delta;
            if (updated < 0) {
                throw new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "재고 부족",
                        "선택한 날짜(" + currentDate + ")에 재고가 부족합니다."
                );
            }
            dpr.setAvailableRoom(updated);
            dailyPlaceReservationRepository.save(dpr);

            date = date.plusDays(1);
        }
    }

    /**
     * 주석: 사용자가 특정 숙소에 대해 리뷰를 작성할 수 있는지 확인합니다.
     * @param placeId 확인할 숙소 ID
     * @param user 현재 로그인한 사용자 정보
     * @return 리뷰 작성 가능 여부 (true/false)
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public boolean canUserWriteReview(Long placeId, UserProjection user) {
        if (user == null) {
            return false;
        }
        GuestEntity guestEntity = guestRepository.findByUsersId(user.getId()).orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Guest Not Found",
                "해당 유저의 투숙객 정보를 찾을 수 없습니다."
        ));
        // 체크아웃(checked_out) 상태의 예약이 존재하는지 확인
        return reservationRepository.existsByUsersIdAndRoomPlaceIdAndStatus(
                guestEntity.getId(),
                placeId,
                Reservation.ReservationStatus.checked_out
        );
    }

    public List<ReservationInfoProjection> getReservationsByPlaceId(Long placeId) {
        return reservationRepository.findByRoom_Place_Id(placeId);
    }

    public List<ReservationProjection> getReservationsByUser(Long userId) {
        return reservationRepository.findReservationsByUserId(userId);
    }

    public List<PaymentProjection> getPaymentsByUser(Long userId) {
        return paymentRepository.findPaymentsByUserId(userId);
    }


}