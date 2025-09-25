package daewoo.team5.hotelreservation.domain.question.specification;

import daewoo.team5.hotelreservation.domain.question.dto.QuestionSearchRequest;
import daewoo.team5.hotelreservation.domain.question.entity.Question;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class QuestionSpecification {
    public static Specification<Question> filterBy(Long placeId, QuestionSearchRequest searchRequest) { // placeId 파라미터 추가
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ===== 👇 [추가된 부분] =====
            // 1. 항상 현재 숙소 ID를 기준으로 필터링
            predicates.add(criteriaBuilder.equal(root.get("place").get("id"), placeId));
            // ===== 👆 [추가된 부분] =====

            Join<Question, Users> userJoin = root.join("user");

            if (searchRequest.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(userJoin.get("id"), searchRequest.getUserId()));
            }
            if (searchRequest.getUserLoginId() != null && !searchRequest.getUserLoginId().isBlank()) {
                predicates.add(criteriaBuilder.like(userJoin.get("userId"), "%" + searchRequest.getUserLoginId() + "%"));
            }
            if (searchRequest.getKeyword() != null && !searchRequest.getKeyword().isBlank()) {
                Predicate titleLike = criteriaBuilder.like(root.get("title"), "%" + searchRequest.getKeyword() + "%");
                Predicate contentLike = criteriaBuilder.like(root.get("content"), "%" + searchRequest.getKeyword() + "%");
                predicates.add(criteriaBuilder.or(titleLike, contentLike));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}