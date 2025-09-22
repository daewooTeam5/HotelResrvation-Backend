package daewoo.team5.hotelreservation.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "regions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 시/도 (서울특별시, 부산광역시, 경기도 등)
    @Column(nullable = false, length = 50)
    private String sido;

    // 시군구 (강남구, 해운대구, 수원시 등)
    @Column(nullable = false, length = 50)
    private String sigungu;

    // 선택적으로 동/읍/면까지 확장 가능
    @Column(length = 50)
    private String dong;
}
