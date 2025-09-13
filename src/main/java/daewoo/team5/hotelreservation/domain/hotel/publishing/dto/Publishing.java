package daewoo.team5.hotelreservation.domain.hotel.publishing.dto;

import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.Address;
import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.Amenities;
import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.ImageList;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "publishing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publishing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hotelName;   //호텔 이름은 필수

    @OneToMany(mappedBy = "publishing", cascade = CascadeType.ALL)
    private List<Address> address;      //대량의 데이터를 가져와야 되니 리스트로 주소

    @OneToMany(mappedBy = "publishing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageList> images;     //이미지 리스트 만들어 가져오기

    @Column(nullable = false)
    private int price;      //1박 당 가격은 필수

    @Column(nullable = false)
    private int maxCount;       //호실 하나에 들어갈 최대 수용 가능 인원

    @Column(nullable = false)
    private int roomNumber;     //호실 번호

    @OneToMany(mappedBy = "publishing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Amenities> amenities;      //편의시설 리스트로 검색 조건으로 필터링 할 것

    private String introduction;    //설명



}
