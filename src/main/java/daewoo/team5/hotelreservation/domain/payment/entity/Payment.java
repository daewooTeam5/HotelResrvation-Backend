package daewoo.team5.hotelreservation.domain.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('paid','cancelled','refunded') DEFAULT 'paid'")
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    public enum PaymentMethod {
        card,
        bank_transfer,
        points,
        coupon
    }

    public enum PaymentStatus {
        paid,
        cancelled,
        refunded
    }
}

