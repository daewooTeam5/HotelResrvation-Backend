package daewoo.team5.hotelreservation.domain.place.projection;
import java.math.BigDecimal;

public interface AdminRoomInfoProjection {
    Long getId();   // PK는 id
    int getRoomNumber();
    String getRoomType();
    String getBedType();
    Integer getCapacityPeople();
    Integer getCapacityRoom();
    BigDecimal getPrice();
    String getStatus();
}