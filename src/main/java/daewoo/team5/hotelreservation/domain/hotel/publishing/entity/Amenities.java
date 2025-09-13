package daewoo.team5.hotelreservation.domain.hotel.publishing.entity;

import daewoo.team5.hotelreservation.domain.hotel.publishing.dto.Publishing;
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
    private Long id;

    private String amenityName;

    @ManyToOne
    @JoinColumn(name = "publishing_id")
    private Publishing publishing;
}
