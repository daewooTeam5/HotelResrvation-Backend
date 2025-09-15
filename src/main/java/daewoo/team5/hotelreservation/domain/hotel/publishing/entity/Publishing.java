package daewoo.team5.hotelreservation.domain.hotel.publishing.entity;

import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.Room;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "publishing")
@Table(name = "publishing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publishing {// 엔티티 ERD랑 맞추기
    
    //리스트들을 전부 ManyToOne으로 맞춰주기 이 페이지들 전부 바꾸기

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long publishingId;

    @Column(nullable = false)
    private String hotelName;   //호텔 이름은 필수

    @ManyToOne
    private Address address;

    @OneToMany(mappedBy = "publishing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms;     //호실 가져오기

    @OneToMany(mappedBy = "publishing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses;    //주소 가져오기+매니 투 원으로 바꾸기

    @OneToMany(mappedBy = "publishing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageList> images;     //이미지 가져오기

    @OneToMany(mappedBy = "publishing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Amenities> amenities;  //편의 시설 가져오기


    private String introduction;    //설명



}
