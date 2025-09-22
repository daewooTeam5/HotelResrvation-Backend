package daewoo.team5.hotelreservation.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "place_amenity")  // 2. Renamed table for clarity (best practice). "place_service" is also fine if you prefer.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceAmenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Places place;

    @ManyToOne(fetch = FetchType.LAZY)
    // 3. Updated the JoinColumn name to reflect the new table name.
    // This makes the relationship clear and unambiguous for Hibernate.
    @JoinColumn(name = "amenity_id", nullable = false)
    private Amenity amenity;
}
