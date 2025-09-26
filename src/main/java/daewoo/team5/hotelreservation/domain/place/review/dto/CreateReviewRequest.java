// src/main/java/daewoo/team5/hotelreservation/domain/place/review/dto/CreateReviewRequest.java
package daewoo.team5.hotelreservation.domain.place.review.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import java.util.List;

@Getter
public class CreateReviewRequest {

    // ✅ [추가] 리뷰를 작성할 예약 ID
    @NotNull(message = "예약 ID는 필수입니다.")
    private Long reservationId;

    @NotNull(message = "평점은 필수입니다.")
    @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5 이하여야 합니다.")
    private Integer rating;

    @NotBlank(message = "리뷰 내용은 비워둘 수 없습니다.")
    private String comment;

    private List<String> imageUrls;
}