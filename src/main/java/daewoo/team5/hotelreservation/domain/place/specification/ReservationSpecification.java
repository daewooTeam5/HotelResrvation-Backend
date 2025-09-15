package daewoo.team5.hotelreservation.domain.place.specification;

import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.place.dto.ReservationSearchRequest;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.place.entity.RoomNo;
import daewoo.team5.hotelreservation.domain.users.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ReservationSpecification {

    public static Specification<Reservation> filter(ReservationSearchRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join: User (reservations.user → users.id)
            Join<Reservation, User> user = root.join("user", JoinType.LEFT);

            // Join: RoomNo (reservations.roomNo → room_no.room_no)
            Join<Reservation, RoomNo> roomNo = root.join("roomNo", JoinType.LEFT);

            // Join: Room (roomNo.room → rooms.id)
            Join<RoomNo, Room> room = roomNo.join("room", JoinType.LEFT);

            // Join: Place (room.place → place.id)
            Join<Room, Places> place = room.join("place", JoinType.LEFT);

            // 예약자 이름
            if (req.getUserName() != null && !req.getUserName().isBlank()) {
                predicates.add(cb.like(user.get("name"), "%" + req.getUserName() + "%"));
            }

            // 객실 번호
            if (req.getRoomNo() != null && !req.getRoomNo().isBlank()) {
                predicates.add(cb.equal(roomNo.get("roomNo"), req.getRoomNo()));
            }

            // 호텔 이름
            if (req.getHotelName() != null && !req.getHotelName().isBlank()) {
                predicates.add(cb.like(place.get("name"), "%" + req.getHotelName() + "%"));
            }

            // 예약 상태
            if (req.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), req.getStatus()));
            }

            // 결제 상태
            if (req.getPaymentStatus() != null) {
                predicates.add(cb.equal(root.get("paymentStatus"), req.getPaymentStatus()));
            }

            // 체크인/체크아웃 날짜
            if (req.getStartDate() != null && req.getEndDate() != null) {
                predicates.add(cb.between(
                        root.get("resevStart"),
                        req.getStartDate().atStartOfDay(),
                        req.getEndDate().atTime(23, 59, 59)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}