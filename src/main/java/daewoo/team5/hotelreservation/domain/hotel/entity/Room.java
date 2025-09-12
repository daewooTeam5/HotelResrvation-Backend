package daewoo.team5.hotelreservation.domain.hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(length = 50)
    private String roomType;

    @Column(length = 50)
    private String bedType;

    @Column(nullable = false)
    private Integer capacityRoom;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('available', 'reserved', 'cleaning') DEFAULT 'available'")
    private RoomStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public enum RoomStatus {
        available,
        reserved,
        cleaning
    }
}
