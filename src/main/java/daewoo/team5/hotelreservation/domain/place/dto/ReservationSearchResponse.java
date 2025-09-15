package daewoo.team5.hotelreservation.domain.hotel.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ReservationSearchResponse {
    private Long reservationId;
    private String userName;
    private String email;
    private String roomNo;
    private String hotelName;
    private String resevStart;
    private String resevEnd;
    private String status;
    private String paymentStatus;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
