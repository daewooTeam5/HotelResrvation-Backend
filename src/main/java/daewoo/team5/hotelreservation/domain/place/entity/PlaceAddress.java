package daewoo.team5.hotelreservation.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "place_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // 주소 ID

    @Column(name = "place_id", insertable = false, updatable = false)
    private Long placeId; // 숙소 아이디 (FK, Place 테이블 참조 예정)

    @Column(name = "postal_code", length = 5, nullable = false)
    private String postalCode; // 우편번호

    @Column(name = "sido", length = 50, nullable = false)
    private String sido; // 시도

    @Column(name = "sigungu", length = 50, nullable = false)
    private String sigungu; // 시/군/구

    @Column(name = "road_name", length = 100, nullable = false)
    private String roadName; // 도로명

    @Column(name = "detail_address", length = 200)
    private String detailAddress; // 상세 주소

    @Column(name = "lat", precision = 10, scale = 7)
    private BigDecimal lat; // 위도

    @Column(name = "lng", precision = 10, scale = 7)
    private BigDecimal lng; // 경도

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false)
    private Places place;

}
