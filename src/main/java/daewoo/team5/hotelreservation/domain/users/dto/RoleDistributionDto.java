package daewoo.team5.hotelreservation.domain.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoleDistributionDto {
    private String role;
    private Long count;
}