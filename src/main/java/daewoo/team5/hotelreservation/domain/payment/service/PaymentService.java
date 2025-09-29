package daewoo.team5.hotelreservation.domain.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.domain.coupon.entity.CouponEntity;
import daewoo.team5.hotelreservation.domain.coupon.entity.CouponHistoryEntity;
import daewoo.team5.hotelreservation.domain.coupon.entity.UserCouponEntity;
import daewoo.team5.hotelreservation.domain.coupon.repository.CouponHistoryRepository;
import daewoo.team5.hotelreservation.domain.coupon.repository.CouponRepository;
import daewoo.team5.hotelreservation.domain.coupon.repository.UserCouponRepository;
import daewoo.team5.hotelreservation.domain.coupon.service.CouponService;
import daewoo.team5.hotelreservation.domain.payment.dto.PaymentConfirmRequestDto;
import daewoo.team5.hotelreservation.domain.payment.dto.ReservationRequestDto;
import daewoo.team5.hotelreservation.domain.payment.dto.TossPaymentDto;
import daewoo.team5.hotelreservation.domain.payment.entity.*;
import daewoo.team5.hotelreservation.domain.payment.infrastructure.TossPayClient;
import daewoo.team5.hotelreservation.domain.payment.projection.PaymentDetailProjection;
import daewoo.team5.hotelreservation.domain.payment.projection.PaymentInfoProjection;
import daewoo.team5.hotelreservation.domain.payment.repository.GuestRepository;
import daewoo.team5.hotelreservation.domain.payment.repository.PaymentHistoryRepository;
import daewoo.team5.hotelreservation.domain.payment.repository.PointHistoryRepository;
import daewoo.team5.hotelreservation.domain.place.entity.DailyPlaceReservation;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.place.repository.*;
import daewoo.team5.hotelreservation.domain.place.repository.projection.PaymentSummaryProjection;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.MyInfoProjection;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import daewoo.team5.hotelreservation.global.exception.UserNotFoundException;
import feign.FeignException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final GuestRepository guestRepository;
    private final UsersRepository usersRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final TossPayClient tossPayClient;
    private final PaymentRepository paymentRepository;
    private final DailyPlaceReservationRepository dailyPlaceReservationRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final CouponService couponService;
    private final PlaceRepository placeRepository;
    private final CouponRepository couponRepository;
    private final CouponHistoryRepository couponHistoryRepository;
    private final UserCouponRepository userCouponRepository;
    private final PointService pointService;
    private final PointHistoryRepository pointHistoryRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public Page<PaymentSummaryProjection> getPaymentsByUser(UserProjection user, int page) {
        Users users = usersRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
        GuestEntity guestEntity = guestRepository.findByUsersId(users.getId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "투숙객 정보가 없습니다.", "투숙객 정보가 없습니다."));
        Page<PaymentSummaryProjection> summaries = paymentRepository.findPaymentSummariesByGuestId(guestEntity.getId(), PageRequest.of(page, 10));
        return summaries;
    }

    private Payment.PaymentStatus mapStatus(String status) {
        /**
         *   READY: 결제를 생성하면 가지게 되는 초기 상태입니다. 인증 전까지는 READY 상태를 유지합니다.
         * - IN_PROGRESS: 결제수단 정보와 해당 결제수단의 소유자가 맞는지 인증을 마친 상태입니다. 결제 승인 API를 호출하면 결제가 완료됩니다.
         * - WAITING_FOR_DEPOSIT: 가상계좌 결제 흐름에만 있는 상태입니다. 발급된 가상계좌에 구매자가 아직 입금하지 않은 상태입니다.
         * - DONE: 인증된 결제수단으로 요청한 결제가 승인된 상태입니다.
         * - CANCELED: 승인된 결제가 취소된 상태입니다.
         * - PARTIAL_CANCELED: 승인된 결제가 부분 취소된 상태입니다.
         * - ABORTED: 결제 승인이 실패한 상태입니다.
         * - EXPIRED: 결제 유효 시간 30분이 지나 거래가 취소된 상태입니다. IN_PROGRESS 상태에서 결제 승인 API를 호출하지 않으면 EXPIRED가 됩니다.
         */
        return switch (status) {
            case "DONE" -> Payment.PaymentStatus.paid;
            case "CANCELLED", "FAILED" -> Payment.PaymentStatus.cancelled;
            default -> throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "결제 상태 오류", "Unknown status: " + status);
        };
    }

    @Transactional
    public Payment confirmPayment(UserProjection user, PaymentConfirmRequestDto dto) {
        try {
            // TODO 결제 금액 유효성 검사

            TossPaymentDto tossPaymentDto = tossPayClient.confirmPayment(dto);
            String cleanText = tossPaymentDto
                    .getRequestedAt().substring(0, 19);
            LocalDateTime paymentTime = LocalDateTime.parse(cleanText, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Reservation reservation = reservationRepository
                    .findByOrderId(
                            dto.getOrderId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다.", "존재하지 않는 예약입니다.")
                    );
            if(user!=null){
                couponHistoryRepository.findByReservation_idWithPending(reservation.getReservationId()).ifPresent(couponHistory -> {
                    couponHistory.setStatus(CouponHistoryEntity.CouponStatus.used);
                    couponHistory.setUsed_at(paymentTime);
                    couponHistoryRepository.save(couponHistory);
                    UserCouponEntity userCouponEntity = userCouponRepository.findByUserIdAndCouponId(user.getId(), couponHistory.getUserCoupon().getCoupon().getId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 유저 쿠폰입니다.", "존재하지 않는 유저 쿠폰입니다."));
                    userCouponEntity.setUsed(true);
                });

            }
            // 로그인 한 유저이면서 포인트 사용 금액이 0원이 아닐경우 포인트 차감기록에 추가
            if (user != null && reservation.getPointDiscountAmount() != 0) {
                Users users = usersRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
                long balanceAfter = users.getPoint() - reservation.getPointDiscountAmount();
                pointHistoryRepository.save(
                        PointHistoryEntity.builder()
                                .type(PointHistoryEntity.PointType.USE)
                                .user(users)
                                .reservation(reservation)
                                .expireAt(null)
                                .createdAt(paymentTime)
                                .balanceAfter(balanceAfter)
                                .amount((long) reservation.getPointDiscountAmount())
                                .description("결제시 포인트 사용")
                                .build()
                );
                users.setPoint(balanceAfter);
            }

            Payment savePayment = paymentRepository.save(
                    Payment.builder()
                            .paymentKey(tossPaymentDto.getPaymentKey())
                            .orderId(tossPaymentDto.getOrderId())
                            .amount(tossPaymentDto.getTotalAmount())
                            .status(mapStatus(tossPaymentDto.getStatus()))
                            .method(Payment.PaymentMethod.card)
                            .methodType(tossPaymentDto.getMethod())
                            .transactionDate(LocalDateTime.now())
                            .reservation(reservation)
                            .build()
            );
            Payment payment = paymentRepository.findByOrderId(savePayment.getOrderId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "결제 정보가 존재하지 않습니다.", "결제 정보가 존재하지 않습니다."));
            ObjectMapper objectMapper = new ObjectMapper();

            paymentHistoryRepository.save(
                    PaymentHistoryEntity.builder()
                            .paymentInfo(objectMapper.writeValueAsString(tossPaymentDto))
                            .payment(payment)
                            .build()
            );
            // TODO : Room 양방향 제거후 영속성 유지
            entityManager.detach(payment);
            Reservation reservationUpdate = reservationRepository.findByOrderId(savePayment.getOrderId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "예약 정보가 존재하지 않습니다.", "예약 정보가 존재하지 않습니다."));
            reservationUpdate.setStatus(Reservation.ReservationStatus.confirmed);
            reservationUpdate.setPaymentStatus(Reservation.ReservationPaymentStatus.paid);
            payment.setReservation(null);
            return payment;
        } catch (FeignException e) {
            String errorMessage = "알 수 없는 오류";

            if (e.responseBody().isPresent()) {
                String response = StandardCharsets.UTF_8.decode(e.responseBody().get()).toString();
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(response);
                    errorMessage = node.has("message") ? node.get("message").asText() : response;
                } catch (Exception ex) {
                    errorMessage = response; // JSON 파싱 실패하면 그냥 raw
                }
            }
            throw new ApiException(HttpStatus.BAD_REQUEST, "결제 승인 실패", errorMessage);
        } catch (JsonProcessingException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 승인 실패", "결제 승인 중 내부 오류가 발생했습니다.");
        }

    }

    private GuestEntity getGuest(UserProjection user, String email, String firstName, String lastName, String phone) {
        // user 가 null 이면 비회원인 상황 -> GuestEntity 조회 후 생성
        if (user == null) {
            return guestRepository.findByEmailAndFirstNameAndLastNameAndPhone(
                    email, firstName, lastName, phone
            ).orElseGet(() -> guestRepository.save(
                    GuestEntity
                            .builder()
                            .email(email)
                            .firstName(firstName)
                            .lastName(lastName)
                            .phone(phone)
                            .build()
            ));
        } else {
            // 로그인한 유저의 경우 투숙객정보에 유저가 참조되있는지 확인
            Optional<GuestEntity> loginGuest = guestRepository.findByUsersId(user.getId());
            if (loginGuest.isEmpty()) {
                log.info("guest is empty");
                // 없을 경우 guest 에 유저 연결 후 저장
                Users users = usersRepository.findById(user.getId())
                        .orElseThrow(UserNotFoundException::new);
                GuestEntity guest = guestRepository.save(
                        GuestEntity.builder()
                                .email(email)
                                .firstName(firstName)
                                .lastName(lastName)
                                .phone(phone)
                                .users(users)
                                .build()
                );
                guestRepository.save(guest);
                return guest;
            }
            return loginGuest.get();
        }
    }

    @Transactional
    public Reservation reservationPlace(UserProjection user, ReservationRequestDto dto) {
        GuestEntity guest = getGuest(user, dto.getEmail(), dto.getFirstName(), dto.getLastName(), dto.getPhone());
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재 하지 않는 방입니다.", "존재하지 않는 방입니다."));
        Places places = placeRepository.findById(room.getPlace().getId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재 하지 않는 숙소입니다.", "존재하지 않는 숙소입니다."));
        LocalDate checkin = dto.getCheckIn();
        LocalDate checkout = dto.getCheckOut();

        // 선점형 예약 방식
        // 날짜별로 객실수가 0개가 있는지 확인 -> 있으면 예약 불가
        for (LocalDate date = checkin; !date.isAfter(checkout.minusDays(1)); date = date.plusDays(1)) {
            Optional<DailyPlaceReservation> byRoomIdAndDate = dailyPlaceReservationRepository.findByRoomIdAndDate(dto.getRoomId(), date);
            if (byRoomIdAndDate.isEmpty()) {
                if (dto.getRoomCount() > room.getCapacityRoom()) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "예약 불가", "선택하신 객실의 최대 예약 가능 인원을 초과하였습니다. 인원수를 확인해주세요.");
                }
                dailyPlaceReservationRepository.save(
                        DailyPlaceReservation.builder()
                                .room(room)
                                .date(date)
                                .availableRoom(room.getCapacityRoom() - dto.getRoomCount())
                                .build()
                );
                continue;
            }
            if (byRoomIdAndDate.get().getAvailableRoom() == 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "예약 불가", "이미 예약이 완료된 날짜가 포함되어 있습니다. 날짜를 확인해주세요.");
            } else {
                if (byRoomIdAndDate.get().getAvailableRoom() < dto.getRoomCount()) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "예약 불가", "선택하신 객실의 최대 예약 가능 인원을 초과하였습니다. 인원수를 확인해주세요.");
                }
                byRoomIdAndDate.get().setAvailableRoom(byRoomIdAndDate.get().getAvailableRoom() - dto.getRoomCount());
            }
        }
        // TODO: DISCOUNT 할인 적용
        BigDecimal baseAmount = room.getPrice()
                .multiply(BigDecimal.valueOf(dto.getNights()))
                .multiply(BigDecimal.valueOf(dto.getRoomCount()));     // 예약

        Reservation reservation = Reservation.builder()
                .guest(guest)
                .orderId(dto.getRoomId() + "_" + guest.getId() + "_" + System.currentTimeMillis())
                .paymentStatus(Reservation.ReservationPaymentStatus.unpaid)
                .status(Reservation.ReservationStatus.pending)
                .baseAmount(baseAmount)
                .finalAmount(baseAmount)
                .resevStart(dto.getCheckIn())
                .resevEnd(dto.getCheckOut())
                .request(dto.getRequest())
                .resevAmount(Long.valueOf(dto.getRoomCount()))
                .couponDiscountAmount(0)
                .fixedDiscountAmount(0)
                .pointDiscountAmount(0)
                .room(room)
                .build();
        Reservation save = reservationRepository.save(reservation);
        // 쿠폰 적용 로그인 한 사용자만 해당
        int discountAmount = 0;
        if (user != null && dto.getCouponId() != null) {
            CouponEntity couponEntity = couponRepository.findById(dto.getCouponId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 쿠폰입니다.", "couponId를 확인해주세요."));
            // 사용가능한 쿠폰인지 유효성 검사
            if (couponService.validateCouponWithPlace(guest.getUsers().getId(), couponEntity, places, baseAmount.toBigInteger().intValue())) {
                UserCouponEntity userCouponEntity = userCouponRepository.findByUserIdAndCouponId(guest.getUsers().getId(), couponEntity.getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "해당 유저가 발급받지 않은 쿠폰입니다.", "userId와 couponId를 확인해주세요."));
                Integer couponDiscount = couponService.calculateDiscountAmount(couponEntity, baseAmount.toBigInteger().intValue());
                // TODO : 이전에 사용 내역이 있다면 취소
                // 쿠폰 사용 내역 대기 상태로 저장
                couponHistoryRepository.save(
                        CouponHistoryEntity.builder()
                                .userCoupon(userCouponEntity)
                                .reservation_id(reservation)
                                .used_at(LocalDateTime.now())
                                .status(CouponHistoryEntity.CouponStatus.pending)
                                .discount_amount(couponDiscount)
                                .build()
                );
                save.setCouponDiscountAmount(couponDiscount);
                discountAmount += couponDiscount;
            }

        }
        // 포인트 유효성 검사
        // 로그인 한 유저만 포인트 차감 차감된 포인트만 기록하고 결제 완료시 실제 포인트 차감
        if (user != null && dto.getUsedPoints() != null && dto.getUsedPoints() > 0) {
            MyInfoProjection myInfo = usersRepository.findById(guest.getUsers().getId(), MyInfoProjection.class).orElseThrow(UserNotFoundException::new);
            if (dto.getUsedPoints() != null && dto.getUsedPoints() > myInfo.getPoint()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "사용자 포인트 부족", "사용자 포인트가 부족합니다.");
            }
            save.setPointDiscountAmount(Math.toIntExact(dto.getUsedPoints()));
            discountAmount += Math.toIntExact(dto.getUsedPoints());
        }
        BigDecimal finalAmount = baseAmount.subtract(BigDecimal.valueOf(discountAmount));
        save.setFinalAmount(finalAmount);
        return save;
    }

    public PaymentDetailProjection getPaymentDetail(Long id, Long userId) {
        // TODO : 결제아이도로 해당 유저가 결제한 내역인지 확인 하는 로직

        return paymentRepository.findPaymentDetailById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 결제 정보입니다.", "존재하지 않는 결제 정보입니다."));
    }

    public Reservation getReservationById(Long reservationId) {
        return reservationRepository
                .findByIdFetchJoin(reservationId)
                .orElseThrow(
                        () -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다.", "존재하지 않는 예약입니다.")
                );
    }

    public List<CouponEntity> getAvailableCoupon(UserProjection user, Long placeId) {
        return couponService.getAvailableCoupon(user, placeId);
    }

    public List<PaymentInfoProjection> getPaymentsByPlaceId(Long placeId) {
        return paymentRepository.findByReservation_Room_Place_Id(placeId);
    }

    public Payment getPaymentDetail(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow();
    }

    public List<PaymentHistoryEntity> getPaymentHistory(Long paymentId) {
        return paymentHistoryRepository.findByPaymentId(paymentId);
    }
}
