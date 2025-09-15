package daewoo.team5.hotelreservation.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "room1")
@Table(name = "rooms1")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 방 ID

    @Column(name = "place_id", nullable = false)
    private Long placeId; // 숙소 ID (FK: Place 테이블)

    @Column(name = "room_type", length = 50, nullable = false)
    private String roomType; // 방 유형

    @Column(name = "bed_type", length = 50, nullable = false)
    private String bedType; // 침대 유형

    @Column(name = "capacity_people", nullable = false)
    private Integer capacityPeople; // 수용 가능 인원

    @Column(name = "capacity_room", nullable = false)
    private Integer capacityRoom; // 수용 객실 수

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price; // 가격

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Status status; // 상태

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 생성일시

    public enum Status {
        AVAILABLE,
        RESERVED,
        CLEANING
    }
}