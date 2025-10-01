package daewoo.team5.hotelreservation.domain.discount.entity;

import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class DiscountHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double discountAmount;

    @ManyToOne
    private Reservation reservation;
}
