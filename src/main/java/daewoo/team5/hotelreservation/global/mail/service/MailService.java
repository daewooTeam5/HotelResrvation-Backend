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

import java.time.format.DateTimeFormatter; // DateTimeFormatter 임포트

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
    public void sendReservationConfirmation(String email, PaymentDetailProjection paymentDetail) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[My Hotel] " + paymentDetail.getPlaceName() + " 예약이 완료되었습니다."); // ✅ 이메일 제목에 숙소 이름 추가

            // ✅ 날짜/시간 포맷터 생성
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");

            // ✅ 프론트엔드 예약 상세 페이지 URL (실제 URL로 변경해야 합니다)
            String reservationDetailUrl = "http://localhost:5173/profile/payments/" + paymentDetail.getPaymentId();

            // ✅ HTML 본문 생성 (개선된 버전)
            String htmlContent = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4; }" +
                    ".container { background-color: #fff; border: 1px solid #ddd; border-radius: 8px; padding: 30px; max-width: 600px; margin: auto; }" +
                    "h1 { color: #0056b3; border-bottom: 2px solid #0056b3; padding-bottom: 10px; }" +
                    "h3 { color: #333; border-bottom: 1px solid #eee; padding-bottom: 8px; margin-top: 25px; }" +
                    "ul { list-style: none; padding: 0; }" +
                    "li { margin-bottom: 10px; font-size: 15px; }" +
                    "li b { display: inline-block; width: 120px; color: #555; }" +
                    ".button-container { text-align: center; margin-top: 30px; }" +
                    ".button { background-color: #007bff; color: #ffffff; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block; }" +
                    ".footer { margin-top: 30px; text-align: center; font-size: 12px; color: #888; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<h1>✅ 예약 확정 안내</h1>" +
                    "<p>안녕하세요! " + paymentDetail.getPlaceName() + " 예약이 성공적으로 완료되었습니다.</p>" +

                    // --- 예약 상세 정보 버튼 ---
                    "<div class='button-container'>" +
                    "<a href='" + reservationDetailUrl + "' class='button' style='color: #ffffff; text-decoration: none;'>예약 상세 보기</a>" +
                    "</div>" +

                    "<h3>예약 정보</h3>" +
                    "<ul>" +
                    "<li><b>예약 번호:</b> " + paymentDetail.getReservationId() + "</li>" +
                    "<li><b>숙소명:</b> " + paymentDetail.getPlaceName() + "</li>" +
                    // 💡 숙소 주소와 연락처는 PaymentDetailProjection에 추가해야 합니다.
                    // "<li><b>숙소 주소:</b> " + paymentDetail.getPlaceAddress() + " <a href='https://map.kakao.com/link/search/" + paymentDetail.getPlaceName() + "'>지도 보기</a></li>" +
                    // "<li><b>숙소 연락처:</b> " + paymentDetail.getPlacePhone() + "</li>" +
                    "<li><b>객실 타입:</b> " + paymentDetail.getRoomType() + "</li>" +
                    "<li><b>체크인:</b> " + paymentDetail.getResevStart().toString() + " " + paymentDetail.getCheckIn().toString() + "</li>" +
                    "<li><b>체크아웃:</b> " + paymentDetail.getResevEnd().toString() + "</li>" +
                    (paymentDetail.getRequest() != null && !paymentDetail.getRequest().isEmpty() ? "<li><b>요청사항:</b> " + paymentDetail.getRequest() + "</li>" : "") +
                    "</ul>" +

                    "<h3>상세 결제 내역</h3>" +
                    "<ul>" +
                    "<li><b>주문 번호:</b> " + paymentDetail.getOrderId() + "</li>" +
                    "<li><b>상품 금액:</b> " + paymentDetail.getBaseAmount().toBigInteger() + "원</li>" +
                    (paymentDetail.getCouponDiscountAmount() > 0 ? "<li><b>쿠폰 할인:</b> -" + paymentDetail.getCouponDiscountAmount() + "원</li>" : "") +
                    (paymentDetail.getPointDiscountAmount() > 0 ? "<li><b>포인트 사용:</b> -" + paymentDetail.getPointDiscountAmount() + "원</li>" : "") +
                    "<li><b>총 결제 금액:</b> <b>" + paymentDetail.getFinalAmount().toBigInteger() + "원</b></li>" +
                    "<li><b>결제 수단:</b> " + paymentDetail.getMethod() + "</li>" +
                    // ✅ 포맷팅된 결제 일시 사용
                    "<li><b>결제 일시:</b> " + paymentDetail.getTransactionDate().format(formatter) + "</li>" +
                    "</ul>" +

                    "<div class='footer'>" +
                    "<p>본 메일은 발신전용입니다. 궁금한 점은 고객센터로 문의해주세요.<br>" +
                    "My Hotel &copy; 2025 All Rights Reserved.</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            mimeMessageHelper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
            log.info("개선된 예약 확정 메일 전송 성공: {}", email);
        } catch (Exception e) {
            log.error("개선된 예약 확정 메일 전송 실패: {}", e.getMessage());
        }
    }
}