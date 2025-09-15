package daewoo.team5.hotelreservation.domain.hotel.publishing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;



@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rooms {// 엔티티 ERD랑 맞추기

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    private String roomType;

    private String bedType;

    private int roomNumber; //호실 번호

    private int price;  //가격

    private int capacityPeople;   //인원 수

    private int capacityRoom;

    private LocalDateTime createdDat;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;
}