package daewoo.team5.hotelreservation.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "place_service")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Service {  //
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    private String serviceName;

    private String serviceIcon;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Places place;
}