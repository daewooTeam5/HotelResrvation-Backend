package daewoo.team5.hotelreservation.domain.hotel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_no")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoomNo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // room_no.id

    // rooms.id (숫자 PK)
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    // 사람이 보는 객실 번호/코드 (예: "101", "A101")
    @Column(name = "room_no")
    private String roomNo;
}
