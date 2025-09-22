package daewoo.team5.hotelreservation.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "places")   // 실제 DB 테이블명
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discount {

    @ManyToOne
    private Room room;

    @Id
    private Long id;

    private int person;      // 인원

    private int discount;   //할인율
}
