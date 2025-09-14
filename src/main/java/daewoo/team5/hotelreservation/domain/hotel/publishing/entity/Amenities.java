package daewoo.team5.hotelreservation.domain.hotel.publishing.entity;

import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.Publishing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "amenities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amenities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long amenitiesId;   //고유 아아디

    private String amenityName; //제공하는 고유 편의시설로 음...조식 제공, 바비큐그릴, 주차장 이용 가능 등

    @ManyToOne
    @JoinColumn(name = "publishing_id")
    private Publishing publishing;

}
