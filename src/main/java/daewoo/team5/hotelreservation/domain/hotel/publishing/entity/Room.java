package daewoo.team5.hotelreservation.domain.hotel.publishing.entity;

import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.Publishing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {// 엔티티 ERD랑 맞추기

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    private int roomNumber; //호실 번호

    private int price;  //가격

    private int maxCount;   //인원 수

    @ManyToOne
    @JoinColumn(name = "publishing_id")
    private Publishing publishing;
}