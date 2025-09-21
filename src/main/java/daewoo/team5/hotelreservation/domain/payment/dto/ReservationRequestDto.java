package daewoo.team5.hotelreservation.domain.payment.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Long paymentAmount;
    private String request;
    private Long roomId;
}
