package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReservationUpdateRequest {
    private String roomId;          // optional
    private String status;          // optional: "pending","confirmed","cancelled","checked_in","checked_out"
    private BigDecimal amount;      // optional
    private String resevStart;      // optional
    private String resevEnd;        // optional
    private String paymentStatus;   // optional: "unpaid","paid","refunded"
}