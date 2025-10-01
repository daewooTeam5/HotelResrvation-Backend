package daewoo.team5.hotelreservation.domain.discount.service;

import daewoo.team5.hotelreservation.domain.discount.dto.DiscountCreateDto;
import daewoo.team5.hotelreservation.domain.discount.dto.DiscountDetailDto;
import daewoo.team5.hotelreservation.domain.discount.dto.DiscountResponseDto;
import daewoo.team5.hotelreservation.domain.discount.repository.DiscountHistoryRepository;
import daewoo.team5.hotelreservation.domain.discount.repository.DiscountRepository;
import daewoo.team5.hotelreservation.domain.discount.repository.ReservationDiscountHistoryRepository;
import daewoo.team5.hotelreservation.domain.payment.entity.DiscountEntity;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountService {

    private static final Logger log = LoggerFactory.getLogger(DiscountService.class);
    private final DiscountRepository discountRepository;
    private final PlaceRepository placeRepository;
    private final ReservationDiscountHistoryRepository reservationDiscountHistoryRepository;
    private final DiscountHistoryRepository discountHistoryRepository;

    public Integer calculateDiscountAmount(Room room, LocalDate checkIn, LocalDate checkOut) {
        List<DiscountEntity> discounts =
                discountRepository.findByPlaceIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        room.getPlace().getId(),
                        checkOut.minusDays(1),
                        checkIn
                );

        int days = checkIn.until(checkOut).getDays(); // 숙박일수
        if (days <= 0) return 0;

        int totalDiscountValue = 0;

        // 체크인일부터 체크아웃 전날까지 하루씩 체크
        for (LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1)) {
            // 이 날짜에 적용되는 할인 찾기
            LocalDate finalDate = date;
            DiscountEntity applicable = discounts.stream()
                    .filter(d -> !d.getStartDate().isAfter(finalDate) && !d.getEndDate().isBefore(finalDate))
                    .findFirst()
                    .orElse(null);

            if (applicable != null) {
                totalDiscountValue += applicable.getDiscountValue();
            }
        }

        return totalDiscountValue / days;
    }

    public DiscountResponseDto createDiscount(Long ownerId, DiscountCreateDto dto) {
        Places place = placeRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "해당 관리자의 숙소를 찾을 수 없습니다.", "ID: " + ownerId));

        DiscountEntity discount = DiscountEntity.builder()
                .name(dto.getName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .discountValue(dto.getDiscountValue())
                .maxDiscountAmount(dto.getMaxDiscountAmount())
                .place(place)
                .build();

        DiscountEntity savedDiscount = discountRepository.save(discount);
        return new DiscountResponseDto(savedDiscount);
    }
    @Transactional(readOnly = true) // [!code ++]
    public DiscountDetailDto getDiscountDetail(Long ownerId, Long discountId) { // [!code ++]
        Places place = placeRepository.findByOwnerId(ownerId) // [!code ++]
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "해당 관리자의 숙소를 찾을 수 없습니다.", "ID: " + ownerId)); // [!code ++]
        // [!code ++]
        DiscountEntity discount = discountRepository.findById(discountId) // [!code ++]
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "할인 정보를 찾을 수 없습니다.", "ID: " + discountId)); // [!code ++]
        // [!code ++]
        if (!discount.getPlace().getId().equals(place.getId())) { // [!code ++]
            throw new ApiException(HttpStatus.FORBIDDEN, "권한 없음", "자신의 숙소에 등록된 할인만 조회할 수 있습니다."); // [!code ++]
        } // [!code ++]
        // [!code ++]
        List<Object[]> usageData = discountRepository.findDiscountUsageByDate(place.getId(), discount.getStartDate(), discount.getEndDate()); // [!code ++]
        // [!code ++]
        long usageCount = usageData.stream().mapToLong(row -> (long) row[1]).sum(); // [!code ++]
        long totalDiscountAmount = usageCount * discount.getDiscountValue(); // 단순 계산, 실제로는 예약별 할인액 합산 필요 // [!code ++]
        // [!code ++]
        Map<LocalDate, Long> dailyUsage = usageData.stream() // [!code ++]
                .collect(Collectors.toMap(row -> (LocalDate) row[0], row -> (long) row[1])); // [!code ++]
        // [!code ++]
        return DiscountDetailDto.builder() // [!code ++]
                .id(discount.getId()) // [!code ++]
                .name(discount.getName()) // [!code ++]
                .startDate(discount.getStartDate()) // [!code ++]
                .endDate(discount.getEndDate()) // [!code ++]
                .discountValue(discount.getDiscountValue()) // [!code ++]
                .maxDiscountAmount(discount.getMaxDiscountAmount()) // [!code ++]
                .usageCount(usageCount) // [!code ++]
                .totalDiscountAmount(totalDiscountAmount) // [!code ++]
                .dailyUsage(dailyUsage) // [!code ++]
                .build(); // [!code ++]
    } // [!code ++]

    @Transactional(readOnly = true)
    public List<DiscountResponseDto> getDiscounts(Long ownerId) {
        Places place = placeRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "해당 관리자의 숙소를 찾을 수 없습니다.", "ID: " + ownerId));
        return discountRepository.findByPlaceId(place.getId()).stream()
                .map(DiscountResponseDto::new)
                .collect(Collectors.toList());
    }

    public void deleteDiscount(Long ownerId, Long discountId) {
        Places place = placeRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "해당 관리자의 숙소를 찾을 수 없습니다.", "ID: " + ownerId));

        DiscountEntity discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "할인 정보를 찾을 수 없습니다.", "ID: " + discountId));

        if (!discount.getPlace().getId().equals(place.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "권한 없음", "자신의 숙소에 등록된 할인만 삭제할 수 있습니다.");
        }

        discountRepository.delete(discount);
    }
}