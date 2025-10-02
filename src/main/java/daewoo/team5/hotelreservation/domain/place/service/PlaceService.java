package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.auth.repository.UserFcmRepository;
import daewoo.team5.hotelreservation.domain.notification.entity.NotificationEntity;
import daewoo.team5.hotelreservation.domain.notification.repository.NotificationRepository;
import daewoo.team5.hotelreservation.domain.place.dto.AmenityDto;
import daewoo.team5.hotelreservation.domain.place.dto.PlaceDetailResponse;
import daewoo.team5.hotelreservation.domain.place.dto.PlaceInfoProjection;
import daewoo.team5.hotelreservation.domain.place.dto.RoomInfoDto;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.projection.*;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import daewoo.team5.hotelreservation.infrastructure.firebasefcm.FcmService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final UserFcmRepository userFcmRepository;
    private final FcmService fcmService;
    private final NotificationRepository notificationRepository;
    private final PlaceRepository placeRepository;

    public Page<PlaceItemInfomation> AllSearchPlaces(
            int start, String name, String checkIn, String checkOut,
            int people, int rooms,
            String placeCategory, Double minRating, Double minPrice, Double maxPrice,
            String address,Long userId
    ) {
        return placeRepository.findAllSearchPlaceInfo(
                name, checkIn, checkOut, people, rooms,
                placeCategory, minRating, minPrice, maxPrice,
                userId,address,
                PageRequest.of(start - 1, 10)
        );
    }


    public PlaceDetailResponse getPlaceDetail(Long placeId, LocalDate startDate, LocalDate endDate, Integer adult, Integer children, Integer roomNum) {
        // [수정됨] 파라미터 null 체크 및 기본값 설정
        int adults = (adult == null) ? 1 : adult;
        int childs = (children == null) ? 0 : children;
        int rooms = (roomNum == null || roomNum == 0) ? 1 : roomNum;
        int totalPeople = adults + childs;

        if (startDate == null || endDate == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "날짜 정보가 필요합니다.", "체크인/체크아웃 날짜를 입력해주세요.");
        }

        PlaceDetailProjection detail = placeRepository.findPlaceDetail(placeId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 숙소입니다.", "ID: " + placeId));

        List<String> images = placeRepository.findPlaceImages(placeId);

        List<RoomInfo> roomEntities = placeRepository.findRoomsByPlace(placeId, startDate, endDate, totalPeople, rooms);

        List<RoomInfoDto> roomDtos = roomEntities.stream()
                .map(r -> new RoomInfoDto(
                        r.getRoomId(),
                        r.getRoomType(),
                        r.getBedType(),
                        r.getCapacityPeople(),
                        r.getCapacityRoom(),
                        r.getPrice(),
                        r.getStatus(),
                        r.getAvailableRoom(),
                        r.getArea(),
                        splitImages(r.getImages()),
                        r.getIsAvailable(),
                        mapAmenities(r.getAmenities()),
                        r.getDiscountValue(),
                        r.getFinalPrice()
                ))
                .toList();

        List<PlaceServiceProjection> services = placeRepository.findPlaceServices(placeId);

        return new PlaceDetailResponse(detail, images, roomDtos, services);
    }

    public Page<AdminPlaceProjection> getAdminPlace(
            String sido,
            String sigungu,
            String approvalStatus,
            String ownerName,
            String placeName,
            int start
    ) {
        Pageable pageable = PageRequest.of(start, 20);

        Places.Status status = null;
        if (approvalStatus != null && !approvalStatus.isEmpty()) {
            status = Places.Status.valueOf(approvalStatus.toUpperCase());
        }

        return placeRepository.searchAdminPlaces(sido, sigungu, status, ownerName, placeName, pageable);
    }

    @Transactional
    public void updatePlaceStatus(Long placeId, Places.Status status) {
        Places place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다. ID=" + placeId));

        place.setStatus(status);
        placeRepository.save(place);

        // === 알림 발송 및 저장 ===
        Users owner = place.getOwner(); // 숙소 주인
        if (owner != null) {
            userFcmRepository.findByUserId(owner.getId()).ifPresent(userFcm -> {
                String token = userFcm.getToken();
                if (token != null && !token.isEmpty()) {
                    try {
                        String title = "숙소 승인 상태 변경";
                        String body = "숙소 [" + place.getName() + "] 의 상태가 [" + status.name() + "]로 변경되었습니다.";

                        // FCM 푸시 전송
                        fcmService.sendToToken(token, title, body, null);

                        // Notification 저장
                        NotificationEntity notification = NotificationEntity.builder()
                                .title(title)
                                .content(body)
                                .notificationType(NotificationEntity.NotificationType.ADMIN)
                                .user(owner)
                                .build();
                        notificationRepository.save(notification);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    public PlaceInfoProjection getPlaceInfo(Long placeId) {
        return placeRepository.findPlaceInfo(placeId);
    }

    private List<String> splitImages(String images) {
        if (images == null || images.isEmpty()) return List.of();
        return Arrays.asList(images.split(","));
    }

    private List<AmenityDto> mapAmenities(String amenities) {
        if (amenities == null || amenities.isEmpty()) return List.of();

        return Arrays.stream(amenities.split(","))
                .map(item -> {
                    String[] parts = item.split(":", 2);
                    return new AmenityDto(
                            parts[0],
                            (parts.length > 1 && !parts[1].isBlank()) ? parts[1] : null
                    );
                })
                .toList();
    }
}

