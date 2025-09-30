package daewoo.team5.hotelreservation.domain.payment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.domain.auth.service.AuthService;
import daewoo.team5.hotelreservation.domain.payment.dto.PaymentConfirmRequestDto;
import daewoo.team5.hotelreservation.domain.payment.dto.ReservationRequestDto;
import daewoo.team5.hotelreservation.domain.payment.entity.Payment;
import daewoo.team5.hotelreservation.domain.payment.entity.PaymentHistoryEntity;
import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.payment.projection.PaymentDetailProjection;
import daewoo.team5.hotelreservation.domain.payment.service.DashboardService;
import daewoo.team5.hotelreservation.domain.payment.service.PaymentService;
import daewoo.team5.hotelreservation.domain.payment.service.PointService;
import daewoo.team5.hotelreservation.domain.payment.service.TossPaymentService;
import daewoo.team5.hotelreservation.domain.place.repository.PaymentRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.place.service.ReservationService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import daewoo.team5.hotelreservation.global.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final UsersRepository usersRepository;
    private final PaymentService paymentService;
    private final DashboardService dashboardService;
    private final TossPaymentService tossPaymentService;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final PointService pointService;
    private final AuthService authService;
    private final MailService mailService;

    @GetMapping("/reservation/{id}")
    public ApiResult<Reservation> getReservationById(
            @PathVariable("id") Long reservationId
    ) {
        return ApiResult.ok(paymentService.getReservationById(reservationId), "예약 정보 조회 성공");
    }

    @PostMapping("/confirm")
    public ApiResult<Payment> paymentConfirm(
            @RequestBody
            PaymentConfirmRequestDto dto,
            Authentication auth
    ) {
        UserProjection user = authService.isAuthUser(auth);
        Payment payment = paymentService.confirmPayment(user, dto);

        // 결제 상세 정보 조회 (회원/비회원 공통)
        // 회원이면 user.getId()를, 비회원이면 null을 넘겨주어 처리합니다.
        Long userIdForDetail = (user != null) ? user.getId() : null;
        PaymentDetailProjection paymentDetail = paymentService.getPaymentDetail(payment.getId(), userIdForDetail);

        // 예약자 이메일 정보 가져오기
        String guestEmail = paymentService.getReservationById(payment.getReservation().getReservationId()).getGuest().getEmail();


        // 이메일 발송 (회원/비회원 공통)
        mailService.sendReservationConfirmation(guestEmail, paymentDetail);


        // 포인트 적립 (회원 전용)
        if (user != null) {
            pointService.earnPoint(user.getId(), payment.getAmount(), dto.getOrderId());
        }

        return ApiResult.created(payment, "결제 완료");
    }


    // cancel
    @PostMapping("/{id}/cancel")
    @AuthUser
    public ApiResult<Boolean> cancelPayment(
            @PathVariable("id") String paymentKey,
            UserProjection user
    ) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey).orElseThrow();
        reservationService.cancel(payment.getReservation());
        return ApiResult.ok(true, "결제 취소 완료");
    }

    @PostMapping("/process")
    public ApiResult<Reservation> processPayment(
            @RequestBody
            ReservationRequestDto dto,
            Authentication auth
    )  {
        UserProjection currentUser = authService.isAuthUser(auth);
        return ApiResult.ok(paymentService.reservationPlace(currentUser, dto), "예약 성공");
    }

    @GetMapping("/dashboard")
    public ApiResult<?> getFullDashboard() {
        Map<String, Object> result = new HashMap<>();
        result.put("summary", dashboardService.getDashboardSummary());
        result.put("monthlyRevenue", dashboardService.getMonthlyRevenueTrend());
        result.put("topRevenueHotels", dashboardService.getTop5HotelsByRevenue());
        result.put("topReservationHotels", dashboardService.getTop5HotelsByReservations());
        result.put("occupancyRates", dashboardService.getRegionReservationDistribution());
        return ApiResult.ok(result, "대시보드 전체 데이터 조회 성공");
    }

    @GetMapping("/all")
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // 결제 상세
    @GetMapping("/{paymentId}")
    public Payment getPaymentDetail(@PathVariable Long paymentId) {
        return paymentService.getPaymentDetail(paymentId);
    }

    // 결제 상세 내역 (히스토리)
    @GetMapping("/{paymentId}/history")
    public PaymentHistoryEntity getPaymentHistory(@PathVariable Long paymentId) {
        return paymentService.getPaymentHistory(paymentId);
    }
}