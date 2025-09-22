package daewoo.team5.hotelreservation.domain.shoppingcart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequest {
    private Long placeId;      // 숙소/방 ID
    private String startDate;    // 체크인 날짜 (yyyy-MM-dd)
    private String endDate;   // 체크아웃 날짜 (yyyy-MM-dd)
    private int quantity;      // 수량
}
