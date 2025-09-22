package daewoo.team5.hotelreservation.domain.shoppingcart.entity;

import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.global.core.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "shopping",   // 실제 DB 테이블명
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "place_id"})
        }
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "check_in")
    private LocalDate startDate;

    @Column(name = "check_out")
    private LocalDate endDate;

    @Column(name = "quantity")
    private int quantity;
}