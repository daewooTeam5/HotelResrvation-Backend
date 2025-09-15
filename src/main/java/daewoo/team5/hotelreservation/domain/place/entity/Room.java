package daewoo.team5.hotelreservation.domain.place.entity;

import daewoo.team5.hotelreservation.global.core.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "room")
@Table(name = "room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 방 ID

    @ManyToOne
    private Places place; // 숙소 ID (FK: Place 테이블)

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

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<RoomNo> roomNos;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Status status; // 상태

    public enum Status {
        AVAILABLE,
        RESERVED,
        CLEANING
    }
}