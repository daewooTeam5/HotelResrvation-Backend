package daewoo.team5.hotelreservation.domain.hotel.publishing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "place")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;   //여기가 pk

    @Column(nullable = false)
    private String hotelName;   // 호텔 이름은 필수

    @ManyToOne
    private Rooms room;   // 호실 가져오기

    @ManyToOne
    private PlaceService placeService;  // 편의 시설 가져오기

    private String description;    // 설명

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HotelAddress> addresses = new ArrayList<>();


}
