package daewoo.team5.hotelreservation.domain.question.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateAnswerRequest {
    @NotBlank(message = "답변 내용을 입력해주세요.")
    private String answer;
}