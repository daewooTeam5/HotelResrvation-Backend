package daewoo.team5.hotelreservation.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TossPaymentDto {
    // 기본 결제 정보
    private String paymentKey;
    private String type; // NORMAL, BILLING, BRANDPAY
    private String orderId;
    private String orderName;
    private String mId;
    private String currency;
    private String method; // 카드, 가상계좌, 간편결제, 휴대폰, 계좌이체, 문화/도서/게임상품권

    private Long totalAmount;
    private Long balanceAmount;

    private String status; // READY, IN_PROGRESS, WAITING_FOR_DEPOSIT, DONE, CANCELED, PARTIAL_CANCELED, ABORTED, EXPIRED

    private String requestedAt; // ISO 8601
    private String approvedAt; // ISO 8601

    private Boolean useEscrow;
    private String lastTransactionKey;

    // 금액 관련
    private Long suppliedAmount;
    private Long vat;
    private Boolean cultureExpense; // 계좌이체/가상계좌에만 적용
    private Long taxFreeAmount;
    private Long taxExemptionAmount;

    // 취소 이력
    private List<Cancel> cancels;

    // 결제수단별 상세
    private Card card;
    private VirtualAccount virtualAccount;

    private String secret; // 웹훅 검증용

    private MobilePhone mobilePhone;
    private GiftCertificate giftCertificate;
    private Transfer transfer;

    // 임의 메타데이터 (최대 5쌍)
    private Map<String, String> metadata;

    // 영수증 및 결제창
    private Receipt receipt;
    private Checkout checkout;

    // 간편결제
    private EasyPay easyPay;

    // 국가, 실패 정보
    private String country; // ISO-3166-1 alpha-2
    private Failure failure;

    // 현금영수증(단건) 및 이력
    private CashReceipt cashReceipt;
    private List<CashReceiptHistory> cashReceipts;

    // 즉시 할인 프로모션 정보(카드/퀵계좌이체)
    private Discount discount;

    // ----- Nested DTOs -----

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cancel {
        private Long cancelAmount;
        private String cancelReason;
        private Long taxFreeAmount;
        private Long taxExemptionAmount;
        private Long refundableAmount;
        private Long cardDiscountAmount;
        private Long transferDiscountAmount;
        private Long easyPayDiscountAmount;
        private String canceledAt; // ISO 8601
        private String transactionKey;
        private String receiptKey;
        private String cancelStatus; // DONE 등
        private String cancelRequestId; // 비동기 결제에 한함
        @JsonProperty("isPartialCancelable")
        private Boolean partialCancelable;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Card {
        private Long amount; // 카드사에 요청한 금액(즉시할인 포함)
        private String issuerCode; // 발급사 코드
        private String acquirerCode; // 매입사 코드
        private String number; // 마스킹된 카드번호
        private Integer installmentPlanMonths; // 0이면 일시불
        private String approveNo; // 카드사 승인번호
        private Boolean useCardPoint; // 카드사 포인트 사용 여부
        private String cardType; // 신용/체크/기프트/미확인
        private String ownerType; // 개인/법인/미확인
        private String acquireStatus; // READY/REQUESTED/COMPLETED/CANCEL_REQUESTED/CANCELED
        @JsonProperty("isInterestFree")
        private Boolean interestFree; // 무이자 여부
        private String interestPayer; // BUYER/CARD_COMPANY/MERCHANT (v1.4)
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VirtualAccount {
        private String accountType; // 일반/고정
        private String accountNumber;
        private String bankCode; // 은행 코드
        private String customerName; // 발급한 구매자명
        private String depositorName; // 입금자명
        private String dueDate; // ISO 8601
        private String refundStatus; // NONE/PENDING/FAILED/PARTIAL_FAILED/COMPLETED
        private Boolean expired;
        private String settlementStatus; // INCOMPLETED/COMPLETED
        private RefundReceiveAccount refundReceiveAccount; // 결제위젯 환불계좌
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundReceiveAccount {
        private String bankCode;
        private String accountNumber;
        private String holderName;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MobilePhone {
        private String customerMobilePhone; // 숫자만, 8~15자
        private String settlementStatus; // INCOMPLETED/COMPLETED
        private String receiptUrl;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GiftCertificate {
        private String approveNo;
        private String settlementStatus; // INCOMPLETED/COMPLETED
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Transfer {
        private String bankCode;
        private String settlementStatus; // INCOMPLETED/COMPLETED
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Receipt {
        private String url; // 결제수단별 영수증 URL
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Checkout {
        private String url; // 결제창 URL
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EasyPay {
        private String provider; // 간편결제사 코드
        private Long amount; // 계좌/포인트 등 현금성 결제액
        private Long discountAmount; // 간편결제 즉시할인
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Failure {
        private String code;
        private String message;
    }

    // 단건 현금영수증 정보
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CashReceipt {
        private String type; // 소득공제/지출증빙
        private String receiptKey;
        private String issueNumber; // 최대 9자
        private String receiptUrl; // 테스트 환경은 샘플
        private Long amount; // 처리된 금액
        private Long taxFreeAmount; // 면세 금액
    }

    // 현금영수증 이력 항목
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CashReceiptHistory {
        private String receiptKey;
        private String orderId;
        private String orderName;
        private String type; // 소득공제/지출증빙
        private String issueNumber;
        private String receiptUrl;
        private String businessNumber; // 10자
        private String transactionType; // CONFIRM/CANCEL
        private Long amount; // 처리 금액
        private Long taxFreeAmount; // 면세 금액
        private String issueStatus; // IN_PROGRESS/COMPLETED/FAILED
        private Failure failure; // 실패 정보
        private String customerIdentityNumber; // 소비자 인증수단
        private String requestedAt; // ISO 8601
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Discount {
        private Long amount; // 즉시 할인 적용 결제 금액
    }
}

