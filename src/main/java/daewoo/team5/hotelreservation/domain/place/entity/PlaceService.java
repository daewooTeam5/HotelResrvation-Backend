package daewoo.team5.hotelreservation.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "place_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceService {// 엔티티 ERD랑 맞추기

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeKey; //고유 아이디

    @ManyToOne
    private Service service;

    @ManyToOne
    private Places place;

}
