package daewoo.team5.hotelreservation.domain.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.domain.payment.dto.PaymentConfirmRequestDto;
import daewoo.team5.hotelreservation.domain.payment.dto.ReservationRequestDto;
import daewoo.team5.hotelreservation.domain.payment.dto.TossPaymentDto;
import daewoo.team5.hotelreservation.domain.payment.entity.GuestEntity;
import daewoo.team5.hotelreservation.domain.payment.entity.Payment;
import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.payment.infrastructure.TossPayClient;
import daewoo.team5.hotelreservation.domain.payment.repository.GuestRepository;
import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.place.repository.PaymentRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.place.repository.RoomRepository;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import daewoo.team5.hotelreservation.global.exception.UserNotFoundException;
import feign.FeignException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
    @PersistenceContext
    private EntityManager entityManager;
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
    public Payment confirmPayment(PaymentConfirmRequestDto dto) {
        try {
            TossPaymentDto tossPaymentDto = tossPayClient.confirmPayment(dto);
            Reservation reservation = reservationRepository
                    .findByOrderId(
                            dto.getOrderId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다.", "존재하지 않는 예약입니다.")
                    );

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
            // TODO : Room 양방향 제거후 영속성 유지
            entityManager.detach(payment);
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
        }

    }

    private GuestEntity getGuest(UserProjection user, String email, String firstName, String lastName, String phone) {
        // user 가 null 이면 비회원인 상황 -> GuestEntity 조회 후 생성
        if (user == null) {
            return guestRepository.findByEmailAndFirstNameAndLastName(
                    email, firstName, lastName
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
    public Reservation payment(UserProjection user, ReservationRequestDto dto) {
        log.info("payment userproj" + user);
        GuestEntity guest = getGuest(user, dto.getEmail(), dto.getFirstName(), dto.getLastName(), dto.getPhone());
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재 하지 않는 방입니다.", "존재하지 않는 방입니다."));

        Reservation reservation = Reservation.builder()
                .guest(guest)
                .orderId(dto.getRoomId() + "_" + guest.getId() + "_" + System.currentTimeMillis())
                .paymentStatus(Reservation.ReservationPaymentStatus.unpaid)
                .status(Reservation.ReservationStatus.pending)
                .baseAmount(BigDecimal.valueOf(dto.getPaymentAmount()))
                .finalAmount(BigDecimal.valueOf(dto.getPaymentAmount()))
                .resevStart(dto.getCheckIn())
                .resevEnd(dto.getCheckOut())
                .request(dto.getRequest())
                .room(room)
                .build();
        return reservationRepository.save(reservation);


    }

    public Reservation getReservationById(Long reservationId) {
        return reservationRepository
                .findByIdFetchJoin(reservationId)
                .orElseThrow(
                        () -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다.", "존재하지 않는 예약입니다.")
                );
    }
}
