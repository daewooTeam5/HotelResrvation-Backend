package daewoo.team5.hotelreservation.domain.hotel.publishing.entity;

import daewoo.team5.hotelreservation.domain.hotel.publishing.dto.Publishing;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.parameters.P;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "Address")


public class Address {

    @Id
    private Long AddressId;


        @ManyToOne
        @JoinColumn(name = "publishing_id")
        private Publishing publishing;


    private String Province;    //주,도

    private String City;    //시,군,구

    private String town;    //동,읍,면

    private String road;    //도로명 주소

    private String roadNumber;  //도로명 주소(숫자)

    private String postNumber;  //우편 번호

    private String DetailPost; //상세 주소

}
