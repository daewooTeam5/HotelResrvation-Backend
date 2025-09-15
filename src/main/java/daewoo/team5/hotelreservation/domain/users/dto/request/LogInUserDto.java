package daewoo.team5.hotelreservation.domain.users.dto.request;

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
public class LogInUserDto {
    private String username;
    private String password;
}
