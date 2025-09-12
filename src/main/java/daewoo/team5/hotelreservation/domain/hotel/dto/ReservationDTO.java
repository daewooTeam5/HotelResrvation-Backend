package daewoo.team5.hotelreservation.domain.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

    private Long reservationId;
    private int userId; // 변경: userId는 int 타입
    private String roomId;
    private String status;
    private BigDecimal amount;
    private String resevStart;
    private String resevEnd;
    private String paymentStatus;
    private LocalDateTime createdAt;
}
