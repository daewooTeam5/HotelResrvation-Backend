package daewoo.team5.hotelreservation.domain.place.review.dto;

import daewoo.team5.hotelreservation.domain.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewResponseDto {
    private Long reviewId;      // 리뷰 아이디
    private String userName;    // 작성자 이름
    private Users.Role userRole;
    private String comment;     // 리뷰 코멘트
    private String placeName;   // 숙소 이름
    private String ownerReply;  // 사장 답글
}