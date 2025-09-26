package daewoo.team5.hotelreservation.domain.question.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionSearchRequest {
    private Long userId; // 사용자의 기본 키 ID
    private String userLoginId; // 사용자의 로그인 ID
    private String keyword; // 제목 또는 내용 검색 키워드
}