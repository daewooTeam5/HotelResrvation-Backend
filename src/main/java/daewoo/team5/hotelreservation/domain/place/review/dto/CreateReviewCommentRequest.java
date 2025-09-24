// src/main/java/daewoo/team5/hotelreservation/domain/place/review/dto/CreateReviewCommentRequest.java
package daewoo.team5.hotelreservation.domain.place.review.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateReviewCommentRequest {
    @NotBlank(message = "댓글 내용은 비워둘 수 없습니다.")
    private String comment;
}