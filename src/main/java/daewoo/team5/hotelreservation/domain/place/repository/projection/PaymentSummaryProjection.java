package daewoo.team5.hotelreservation.domain.place.repository.projection;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface PaymentSummaryProjection {
    // Payment
    Long getPaymentId();
    String getPaymentKey();
    String getOrderId();
    String getStatus(); // enum -> String 매핑
    String getMethod(); // enum -> String 매핑
    Long getAmount()    ;
    LocalDateTime getTransactionDate();

    // Reservation
    Long getReservationId();
    LocalDate getResevStart();
    LocalDate getResevEnd();

    // Guest
    Long getGuestId();
    String getGuestFirstName();
    String getGuestLastName();

    // Place
    Long getPlaceId();
    String getPlaceName();

    // Room
    Long getRoomId();
    Integer getRoomNumber();
    String getRoomType();

    // First image (for place/room card)
    String getFirstImageUrl();
}
