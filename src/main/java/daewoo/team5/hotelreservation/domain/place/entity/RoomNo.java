package daewoo.team5.hotelreservation.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_no")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoomNo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // room_no PK (숫자)

    @Column(name = "room_id", nullable = false)
    private Long roomId; // rooms.id (숫자 PK)

    @Column(name = "room_no", nullable = false, unique = true)
    private String roomNo; // 사람이 보는 객실 번호 ("A101")

    // Room 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Room room;
}
