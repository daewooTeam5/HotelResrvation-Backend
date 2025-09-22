package daewoo.team5.hotelreservation.domain.shoppingcart.projection;

import java.time.LocalDate;
import java.time.LocalTime;

public interface CartProjection {
    Long getCartId();
    String getPlaceName();
    String getRoomName();

    LocalDate getStartDate();
    LocalDate getEndDate(); //shoppingcart의 check_in, check_out 컬럼
    LocalTime getCheckIn(); //places의 check_in 컬럼
    LocalTime getCheckOut(); //places의 check_out 컬럼

    Double getPrice();
    Integer getQuantity();
    Integer getCapacityPeople();

    String getFileUrl();

    String getSido();
    String getSigungu();
    String getRoadName();
    String getDetailAddress();
}

