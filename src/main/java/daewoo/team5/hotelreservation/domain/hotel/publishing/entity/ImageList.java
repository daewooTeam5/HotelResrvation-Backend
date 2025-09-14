package daewoo.team5.hotelreservation.domain.hotel.publishing.entity;

import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.Publishing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "image_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;   //고유 아이디

    private String imageUrl;    //이미지 url 전부 붙혀넣기

    @ManyToOne
    @JoinColumn(name = "publishing_id")
    private Publishing publishing;

}
