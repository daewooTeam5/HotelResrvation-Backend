package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonMemberReservationRequest {
    private Long reservationId;
    private String lastName; // guestName -> lastName
    private String firstName;
    private String email;
}