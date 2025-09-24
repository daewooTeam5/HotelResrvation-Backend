package daewoo.team5.hotelreservation.domain.place.review.repository;

import daewoo.team5.hotelreservation.domain.place.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// 주석: 리뷰 데이터에 접근하기 위한 Repository 인터페이스입니다.
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 주석: 특정 숙소(place) ID에 해당하는 모든 리뷰를 생성 시간 내림차순으로 조회합니다.
    List<Review> findByPlaceIdOrderByCreatedAtDesc(Long placeId);

    // 특정 숙소 주인의 최근 리뷰 3개 (reviews → places → owner_id)
    List<Review> findTop3ByPlace_OwnerIdOrderByCreatedAtDesc(Long ownerId);
}