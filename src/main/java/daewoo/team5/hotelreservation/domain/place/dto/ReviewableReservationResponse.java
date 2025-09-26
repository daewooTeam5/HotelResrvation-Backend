// src/main/java/daewoo/team5/hotelreservation/domain/place/dto/ReviewableReservationResponse.java
package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReviewableReservationResponse {
    private Long reservationId;
    private String roomType;
    private LocalDate resevStart; // resevEnd 대신 resevStart로 변경 (체크인 기준)
}