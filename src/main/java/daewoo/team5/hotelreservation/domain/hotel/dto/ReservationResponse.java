package daewoo.team5.hotelreservation.domain.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long reservationId,
        String roomId,
        Long userId,
        String status,
        BigDecimal amount,
        String resevStart,
        String resevEnd,
        LocalDateTime createdAt
) {}