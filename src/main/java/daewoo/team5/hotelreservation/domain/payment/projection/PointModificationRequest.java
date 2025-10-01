package daewoo.team5.hotelreservation.domain.payment.projection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PointModificationRequest {
    private long amount; // 지급 또는 차감할 포인트 금액 (long으로 변경)
    private String reason; // 사유 (엔티티의 description 필드에 매핑됨)
}