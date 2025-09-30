package daewoo.team5.hotelreservation.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
// 1. Corrected the table name to match the entity's concept.
// This is the main source of the problem.
@Table(name = "amenity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // Amenity ID

    @Column(name = "name", length = 50, nullable = false)
    private String name;  // Amenity name

    @Column(name = "icon", length = 100)
    private String icon;  // Icon name

    @Column
    @Enumerated(EnumType.STRING)
    private Type type;

    public enum Type{
        PLACE,ROOM
    }
}
