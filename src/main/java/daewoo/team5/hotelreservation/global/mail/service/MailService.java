package daewoo.team5.hotelreservation.global.mail.service;

import daewoo.team5.hotelreservation.domain.payment.projection.PaymentDetailProjection;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender javaMailSender;

    @Async
    public void sendOtpCode(String email, String code) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("호텔 예약 시스템 인증 코드");
            String htmlContent = "<p>안녕하세요,</p>" +
                    "<p>호텔 예약 시스템을 이용해 주셔서 감사합니다.</p>" +
                    "<p>인증 코드는 다음과 같습니다:</p>" +
                    "<h2>" + code + "</h2>" +
                    "<p>이 코드는 10분 동안 유효합니다. 다른 사람이 이 코드를 요청한 경우, 이 이메일을 무시해 주세요.</p>" +
                    "<p>감사합니다.</p>";
            mimeMessageHelper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
            log.info("메일 전송 성공: {}", email);
        } catch (Exception e) {
            log.error("메일 전송 실패: {}", e.getMessage());
            throw new ApiException(HttpStatus.FAILED_DEPENDENCY, "메일 전송 실패", "메일 전송 중 오류가 발생했습니다. 문제가 지속되면 고객센터로 문의해주세요.");
        }
    }

    @Async
    public void sendReservationConfirmation(String email, PaymentDetailProjection paymentDetail){

    }

}
