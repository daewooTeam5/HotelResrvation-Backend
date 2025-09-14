package daewoo.team5.hotelreservation.domain.hotel.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // rooms.id

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(name = "room_type")
    private String roomType;

    @Column(name = "bed_type")
    private String bedType;

    @Column(name = "capacity_room", nullable = false)
    private Integer capacityRoom;

    @Column(name = "price", nullable = false, precision = 38, scale = 2)
    private BigDecimal price;

    // DB는 ENUM('available','reserved','cleaning'); 문자열로 매핑
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
