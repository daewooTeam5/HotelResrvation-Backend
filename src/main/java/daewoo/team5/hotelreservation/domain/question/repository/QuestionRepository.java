package daewoo.team5.hotelreservation.domain.question.repository;

import daewoo.team5.hotelreservation.domain.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // 👈 추가
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> { // 👈 JpaSpecificationExecutor 상속 추가

    // 사용자의 문의를 먼저, 그 다음 다른 사람의 문의를 최신순으로 정렬
    @Query("SELECT q FROM Question q WHERE q.place.id = :placeId ORDER BY CASE WHEN q.user.id = :userId THEN 0 ELSE 1 END, q.createdAt DESC")
    List<Question> findByPlaceIdOrderByUserAndCreatedAtDesc(@Param("placeId") Long placeId, @Param("userId") Long userId);

    List<Question> findByPlaceId(Long placeId);

    @Query("SELECT q FROM Question q JOIN FETCH q.place p JOIN FETCH p.owner WHERE q.id = :id")
    Optional<Question> findByIdWithOwner(@Param("id") Long id);
}