package daewoo.team5.hotelreservation.domain.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    // FK: payments.reservation_id -> reservations.reservation_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethod method; // card/bank_transfer/points/coupon

    @Enumerated(EnumType.STRING)
    @Column(name = "status",
            columnDefinition = "ENUM('paid','cancelled','refunded') DEFAULT 'paid'")
    private PaymentStatus status;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    public enum PaymentMethod {
        card, bank_transfer, points, coupon
    }

    public enum PaymentStatus {
        paid, cancelled, refunded
    }
}
