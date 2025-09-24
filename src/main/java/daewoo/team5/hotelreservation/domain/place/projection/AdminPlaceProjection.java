package daewoo.team5.hotelreservation.domain.place.projection;

import java.math.BigDecimal;
import java.time.LocalTime;

public interface AdminPlaceProjection {
    Long getId();
    String getName();
    Long getOwnerId();
    String getOwnerName();
    String getSido();
    String getSigungu();
    String getCategoryName();
    String getStatus();
}
