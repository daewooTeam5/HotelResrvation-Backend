package daewoo.team5.hotelreservation.domain.place.specification;

import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.place.dto.ReservationSearchRequest;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.payment.entity.GuestEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ReservationSpecification {

    public static Specification<Reservation> filter(ReservationSearchRequest req, Long ownerId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join: Guest (예약자)
            Join<Reservation, GuestEntity> guest = root.join("guest", JoinType.LEFT);

            // Join: Room
            Join<Reservation, Room> room = root.join("room", JoinType.LEFT);

            // Join: Place (room → place)
            Join<Room, Places> place = room.join("place", JoinType.LEFT);

            // 소유자(ownerId)
            predicates.add(cb.equal(place.get("owner").get("id"), ownerId));

            // 예약자 이름 (firstName + lastName)
            if (req.getUserName() != null && !req.getUserName().isBlank()) {
                String keyword = "%" + req.getUserName() + "%";
                predicates.add(cb.or(
                        cb.like(guest.get("firstName"), keyword),
                        cb.like(guest.get("lastName"), keyword),
                        cb.like(cb.concat(guest.get("lastName"), guest.get("firstName")), keyword),
                        cb.like(cb.concat(guest.get("firstName"), guest.get("lastName")), keyword)
                ));
            }

            // 이메일
            if (req.getEmail() != null && !req.getEmail().isBlank()) {
                predicates.add(cb.like(guest.get("email"), "%" + req.getEmail() + "%"));
            }

            // 전화번호
            if (req.getPhone() != null && !req.getPhone().isBlank()) {
                predicates.add(cb.like(guest.get("phone"), "%" + req.getPhone() + "%"));
            }

            // 예약 ID
            if (req.getReservationId() != null) {
                predicates.add(cb.equal(root.get("reservationId"), req.getReservationId()));
            }

            // 객실 유형
            if (req.getRoomType() != null && !req.getRoomType().isBlank()) {
                predicates.add(cb.like(room.get("roomType"), "%" + req.getRoomType() + "%"));
            }

            // 호텔 이름
            if (req.getHotelName() != null && !req.getHotelName().isBlank()) {
                predicates.add(cb.like(place.get("name"), "%" + req.getHotelName() + "%"));
            }

            // 예약 상태
            if (req.getStatus() != null && !req.getStatus().isBlank()) {
                predicates.add(cb.equal(root.get("status"), req.getStatus()));
            }

            // 결제 상태
            if (req.getPaymentStatus() != null && !req.getPaymentStatus().isBlank()) {
                predicates.add(cb.equal(root.get("paymentStatus"), req.getPaymentStatus()));
            }

            // 체크인/체크아웃 날짜
            if (req.getStartDate() != null && req.getEndDate() != null) {
                predicates.add(cb.between(
                        root.get("resevStart"),
                        req.getStartDate().atStartOfDay(),
                        req.getEndDate().atTime(23, 59, 59)
                ));
            } else if (req.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("resevStart"), req.getStartDate().atStartOfDay()));
            } else if (req.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("resevStart"), req.getEndDate().atTime(23, 59, 59)));
            }

            // 금액 범위
            if (req.getMinAmount() != null && req.getMaxAmount() != null) {
                predicates.add(cb.between(root.get("finalAmount"), req.getMinAmount(), req.getMaxAmount()));
            } else if (req.getMinAmount() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("finalAmount"), req.getMinAmount()));
            } else if (req.getMaxAmount() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("finalAmount"), req.getMaxAmount()));
            }

            // 예약 생성일(createdAt)
            if (req.getCreatedStartDate() != null && req.getCreatedEndDate() != null) {
                predicates.add(cb.between(
                        root.get("createdAt"),
                        req.getCreatedStartDate().atStartOfDay(),
                        req.getCreatedEndDate().atTime(23, 59, 59)
                ));
            } else if (req.getCreatedStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), req.getCreatedStartDate().atStartOfDay()));
            } else if (req.getCreatedEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), req.getCreatedEndDate().atTime(23, 59, 59)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
