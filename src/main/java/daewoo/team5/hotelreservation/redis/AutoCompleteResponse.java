package daewoo.team5.hotelreservation.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AutoCompleteResponse {
    private List<String> regions;
    private List<String> places;
}
