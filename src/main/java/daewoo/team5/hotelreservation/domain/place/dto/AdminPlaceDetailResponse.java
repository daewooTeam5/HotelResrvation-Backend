package daewoo.team5.hotelreservation.domain.place.dto;

import daewoo.team5.hotelreservation.domain.payment.projection.PaymentInfoProjection;
import daewoo.team5.hotelreservation.domain.payment.projection.ReservationInfoProjection;
import daewoo.team5.hotelreservation.domain.place.projection.AdminRoomInfoProjection;
import daewoo.team5.hotelreservation.domain.place.review.dto.ReviewDto;

import java.util.List;

public record AdminPlaceDetailResponse (
    PlaceInfoProjection place,
    List<AdminRoomInfoProjection> rooms,
    List<ReservationInfoProjection> reservations,
    List<PaymentInfoProjection> payments,
    List<ReviewDto> reviews
){}
