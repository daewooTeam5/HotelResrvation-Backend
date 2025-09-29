package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FileDTO {

    private String filename;

    private String extension;

    private String url;

}
