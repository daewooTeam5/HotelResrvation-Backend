package daewoo.team5.hotelreservation.domain.users.dto.request;

import daewoo.team5.hotelreservation.domain.coupon.projection.CouponIssuedProjection;
import daewoo.team5.hotelreservation.domain.payment.projection.PaymentProjection;
import daewoo.team5.hotelreservation.domain.payment.projection.PointProjection;
import daewoo.team5.hotelreservation.domain.payment.projection.ReservationProjection;
import daewoo.team5.hotelreservation.domain.place.review.projection.ReviewCommentProjection;
import daewoo.team5.hotelreservation.domain.place.review.projection.ReviewImageProjection;
import daewoo.team5.hotelreservation.domain.place.review.projection.ReviewProjection;
import daewoo.team5.hotelreservation.domain.question.projection.QuestionProjection;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAllDataDTO {
    private UserProjection user;
    private List<CouponIssuedProjection> coupons;
    private List<ReservationProjection> reservations;
    private List<PaymentProjection> payments;
    private List<PointProjection> points;
    private List<ReviewProjection> reviews;
    private List<ReviewImageProjection> reviewImages;
    private List<ReviewCommentProjection> reviewComments;
    private List<QuestionProjection> questions;
}