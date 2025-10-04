package daewoo.team5.hotelreservation.domain.users.dto.request;

import daewoo.team5.hotelreservation.domain.place.entity.File;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String userId;
    private String email;
    private String name;
    private String phone;
    private Users.Role role;
    private Users.Status status;
    private Long point;
    private File profileImageUrl; //이미지


}