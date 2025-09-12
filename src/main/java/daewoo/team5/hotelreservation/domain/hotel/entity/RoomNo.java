package daewoo.team5.hotelreservation.domain.hotel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_no")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomNo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String roomNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}