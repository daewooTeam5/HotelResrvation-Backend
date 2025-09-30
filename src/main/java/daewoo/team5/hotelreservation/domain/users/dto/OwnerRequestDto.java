package daewoo.team5.hotelreservation.domain.users.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OwnerRequestDto {
    private String hotelName;
    private String businessNumber;
    private String email;
    private String phone;
}
