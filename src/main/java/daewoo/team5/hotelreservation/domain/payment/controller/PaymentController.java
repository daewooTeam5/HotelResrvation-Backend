package daewoo.team5.hotelreservation.domain.payment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.domain.payment.dto.PaymentConfirmRequestDto;
import daewoo.team5.hotelreservation.domain.payment.dto.ReservationRequestDto;
import daewoo.team5.hotelreservation.domain.payment.entity.Payment;
import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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


    @GetMapping("/reservation/{id}")
    public ApiResult<Reservation> getReservationById(
            @PathVariable("id") Long reservationId
    ) {
        return ApiResult.ok(paymentService.getReservationById(reservationId), "예약 정보 조회 성공");
    }

    @PostMapping("/confirm")
    @AuthUser
    public ApiResult<Payment> paymentConfirm(
            @RequestBody
            PaymentConfirmRequestDto dto,
            Authentication authentication,
            UserProjection user
    ) {
        Payment payment = paymentService.confirmPayment(user,dto);
        if (authentication.isAuthenticated()) {
            pointService.earnPoint(user.getId(), payment.getAmount(),dto.getOrderId());
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
            ReservationRequestDto dto
    ) throws JsonProcessingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserProjection currentUser = null;
        System.out.println(auth);
        if (auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(principal.toString());
            currentUser = usersRepository.findById(Long.parseLong(node.toString()), UserProjection.class)
                    .orElseThrow(() -> new ApiException(404, "존재하지 않는 유저", "존재 하지 않는 유저입니다."));
        }
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
}
