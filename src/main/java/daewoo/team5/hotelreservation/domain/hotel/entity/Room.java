package daewoo.team5.hotelreservation.domain.hotel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long placeId;

    private String roomType;

    private String bedType;

    private Integer capacityRoom;

    private Double price;

    private String status;  // 'available', 'reserved', 'cleaning'

    @Column(nullable = false)
    private String createdAt;
}
