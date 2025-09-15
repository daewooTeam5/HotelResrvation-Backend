package daewoo.team5.hotelreservation.domain.hotel.publishing.entity;

import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.Publishing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {  //엔티티 ERD랑 맞추기

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId; //고유 아이디

    private String province;    //주,도

    private String city;      //시,군,구

    private String town;    //동,읍,면

    private String road;    //도로명

    private String roadNumber;  //숫자

    private String postNumber;  //우편번호

    private String detailPost;  //상세 주소

    @ManyToOne
    @JoinColumn(name = "publishing_id")
    private Publishing publishing;

}
