package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.payment.service.PaymentService;
import daewoo.team5.hotelreservation.domain.place.dto.AdminPlaceDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceDetailService {

    private final PlaceService placeService;
    private final RoomService roomService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public AdminPlaceDetailResponse getPlaceDetail(Long placeId) {
        return new AdminPlaceDetailResponse(
                placeService.getPlaceInfo(placeId),
                roomService.getRoomsByPlaceId(placeId),
                reservationService.getReservationsByPlaceId(placeId),
                paymentService.getPaymentsByPlaceId(placeId)
        );
    }
}