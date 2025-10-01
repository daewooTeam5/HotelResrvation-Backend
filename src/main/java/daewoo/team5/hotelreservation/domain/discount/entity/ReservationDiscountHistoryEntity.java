package daewoo.team5.hotelreservation.domain.discount.entity;

import daewoo.team5.hotelreservation.domain.payment.entity.DiscountEntity;
import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "ReservationDiscountHistory")
@Table(name = "reservation_discount_history")
public class ReservationDiscountHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDate discountStartDate;

    @Column
    private LocalDate discountEndDate;

    @ManyToOne
    private Reservation reservation;

    @ManyToOne
    private DiscountEntity discount;


}
