package daewoo.team5.hotelreservation.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    private String adminId;
    private String adminPassword;
    private String adminName;
    private String adminRole;
}
