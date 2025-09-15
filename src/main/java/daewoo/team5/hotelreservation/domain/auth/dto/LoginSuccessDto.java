package daewoo.team5.hotelreservation.domain.auth.dto;

import daewoo.team5.hotelreservation.domain.users.entity.User;
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
public class LoginSuccessDto {
    private String accessToken;
    private User user;
}
