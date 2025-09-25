package daewoo.team5.hotelreservation.domain.question.dto;

import daewoo.team5.hotelreservation.domain.question.entity.Question;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class QuestionResponse {
    private final Long questionId;
    private final String title;
    private final String content;
    private final String answer;
    private final String userName;
    private final LocalDateTime createdAt;

    public QuestionResponse(Question question) {
        this.questionId = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.answer = question.getAnswer();
        this.userName = question.getUser().getName();
        this.createdAt = question.getCreatedAt();
    }
}