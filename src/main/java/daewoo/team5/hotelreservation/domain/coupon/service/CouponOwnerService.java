package daewoo.team5.hotelreservation.domain.coupon.service;

import daewoo.team5.hotelreservation.domain.coupon.dto.*;
import daewoo.team5.hotelreservation.domain.coupon.entity.CouponEntity;
import daewoo.team5.hotelreservation.domain.coupon.entity.CouponHistoryEntity;
import daewoo.team5.hotelreservation.domain.coupon.repository.CouponHistoryRepository;
import daewoo.team5.hotelreservation.domain.coupon.repository.CouponRepository;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponOwnerService {

    private final CouponRepository couponRepository;
    private final CouponHistoryRepository couponHistoryRepository;
    private final PlaceRepository placeRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**  쿠폰 목록 조회 */
    public Page<CouponListDto> getCoupons(Long ownerId, Pageable pageable) {
        Places place = placeRepository.findByOwner_Id(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관리자의 숙소를 찾을 수 없습니다."));

        boolean sortByUsedCount = pageable.getSort().stream()
                .anyMatch(order -> order.getProperty().equals("usedCount"));

        Page<CouponEntity> page;

        if (sortByUsedCount) {
            //  usedCount는 Entity에 없으니, DB 정렬 조건 제거
            Pageable noSortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            page = couponRepository.findAllByPlace_Id(place.getId(), noSortPageable);
        } else {
            page = couponRepository.findAllByPlace_Id(place.getId(), pageable);
        }

        // Entity -> DTO 변환
        List<CouponListDto> dtoList = page.stream()
                .map(c -> CouponListDto.builder()
                        .id(c.getId())
                        .couponName(c.getCouponName())
                        .couponCode(c.getCouponCode())
                        .couponType(c.getCouponType().name())
                        .amount(c.getAmount())
                        .minOrderAmount(c.getMinOrderAmount())
                        .maxOrderAmount(c.getMaxOrderAmount())
                        .createdAt(formatDate(c.getCreatedAt()))
                        .expiredAt(formatDate(c.getExpiredAt()))
                        .usedCount(couponRepository.countUsedByCouponId(c.getId()))
                        .build()
                )
                .collect(Collectors.toList());

        //  usedCount 정렬 수동 처리
        if (sortByUsedCount) {
            for (Sort.Order order : pageable.getSort()) {
                if (order.getProperty().equals("usedCount")) {
                    Comparator<CouponListDto> comparator =
                            Comparator.comparing(CouponListDto::getUsedCount, Comparator.nullsLast(Long::compareTo));

                    if (order.getDirection().isDescending()) {
                        comparator = comparator.reversed();
                    }
                    dtoList = dtoList.stream().sorted(comparator).toList();
                }
            }
        }

        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    /**  쿠폰 상세 조회 */
    public CouponDetailDto getCouponDetail(Long couponId, Pageable pageable) {
        CouponEntity coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        Page<CouponHistoryEntity> histories =
                couponHistoryRepository.findAllByUserCoupon_Coupon_Id(couponId, pageable);

        Long usedCount = couponRepository.countUsedByCouponId(couponId); //사용횟수 조회

        return CouponDetailDto.builder()
                .id(coupon.getId())
                .couponName(coupon.getCouponName())
                .couponCode(coupon.getCouponCode())
                .couponType(coupon.getCouponType().name())
                .amount(coupon.getAmount())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxOrderAmount(coupon.getMaxOrderAmount())
                .createdAt(formatDate(coupon.getCreatedAt()))
                .expiredAt(formatDate(coupon.getExpiredAt()))
                .usedCount(usedCount)
                .history(
                        histories.getContent().stream()
                                .map(h -> CouponHistoryDto.builder()
                                        .id(h.getId())
                                        .userCouponId(h.getUserCoupon().getId())
                                        .reservationId(h.getReservation().getReservationId())
                                        .userName(h.getUserCoupon().getUser().getName())
                                        .discountAmount(h.getDiscountAmount())
                                        .usedAt(formatDate(h.getUsedAt()))
                                        .status(h.getStatus().name())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }


    /**  쿠폰 생성 */
    @Transactional
    public Long createCoupon(Long ownerId, CouponCreateDto dto) {
        Places place = placeRepository.findByOwner_Id(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관리자의 숙소를 찾을 수 없습니다."));

        LocalDate expiredDate = LocalDate.parse(dto.getExpiredAt());
        LocalDateTime expiredAt = expiredDate.atTime(LocalTime.MAX);

        CouponEntity coupon = CouponEntity.builder()
                .couponName(dto.getCouponName())
                .couponCode(generateCouponCode())
                .couponType(CouponEntity.CouponType.valueOf(dto.getCouponType()))
                .amount(dto.getAmount())
                .minOrderAmount(dto.getMinOrderAmount() != null ? dto.getMinOrderAmount() : 0)
                .maxOrderAmount(dto.getMaxOrderAmount() != null ? dto.getMaxOrderAmount() : -1)
                .createdAt(LocalDateTime.now())
                .expiredAt(expiredAt)
                .place(place)
                .build();

        return couponRepository.save(coupon).getId();
    }

    // helper
    private String formatDate(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(FORMATTER) : null;
    }

    private String generateCouponCode() {
        return java.util.UUID.randomUUID().toString();
    }

    @Transactional(readOnly = true)
    public Page<CouponHistoryDto> getCouponHistory(Long couponId, Pageable pageable) {
        Page<CouponHistoryEntity> histories =
                couponHistoryRepository.findByUserCoupon_Coupon_Id(couponId, pageable);

        return histories.map(h -> CouponHistoryDto.builder()
                .id(h.getId())
                .userCouponId(h.getUserCoupon().getId())
                .reservationId(h.getReservation().getReservationId())
                .userName(h.getUserCoupon().getUser().getName())
                .discountAmount(h.getDiscountAmount())
                .usedAt(formatDate(h.getUsedAt()))
                .status(h.getStatus().name())
                .build()
        );
    }
}
