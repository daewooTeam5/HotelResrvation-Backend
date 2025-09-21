package daewoo.team5.hotelreservation.domain.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column
    private String orderId;

    @Column(name = "payment_key", nullable = false, unique = true)
    private String paymentKey; // 결제 번호


    @Column
    private Long amount; // paid/cancelled/refunded

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    //
    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethod method;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('paid','cancelled','refunded') DEFAULT 'paid'")
    private PaymentStatus status;

    @Column
    private String methodType;


    public enum PaymentStatus {paid, cancelled, refunded}

    public enum PaymentMethod {card, bank_transfer, points, coupon}
}
