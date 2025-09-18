package daewoo.team5.hotelreservation.domain.payment.controller;

import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/payment")
public class PaymentController {

    @AuthUser
    @PostMapping("/process")
    public ApiResult<String> processPayment(
            UserProjection user
    ){
        return ApiResult.ok("Payment processed successfully", "결제 처리 성공");

    }
}
