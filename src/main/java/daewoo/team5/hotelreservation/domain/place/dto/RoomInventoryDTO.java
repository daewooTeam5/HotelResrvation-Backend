package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomInventoryDTO {
    private Long roomId;
    private LocalDate date;
    private Integer availableRoom;
}
