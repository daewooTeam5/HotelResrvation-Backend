package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RoomStatusDTO {
    private long available;
    private long reserved;
    private long cleaning;

    private List<String> availableTypes;
    private List<String> reservedTypes;
    private List<String> cleaningTypes;
}
