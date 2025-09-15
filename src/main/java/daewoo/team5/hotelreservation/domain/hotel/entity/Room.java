package daewoo.team5.hotelreservation.domain.hotel.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rooms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_type")
    private String roomType;

    @Column(name = "bed_type")
    private String bedType;

    @Column(name = "capacity_room")
    private Integer capacityRoom;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "status")
    private String status;

    // ✅ Place 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    // ✅ RoomNo (사람이 보는 객실번호, 일대다)
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<RoomNo> roomNos;
}