package daewoo.team5.hotelreservation.domain.users.dto.request;

import daewoo.team5.hotelreservation.domain.users.entity.OwnerRequestEntity;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OwnerRequestDto {
    private Long id;
    private Long ownerRequestId;
    private String userId;
    private String email;
    private String name;
    private String phone;
    private Users.Role role;
    private Users.Status status;

    private OwnerRequestEntity.Status ownerRequestStatus;
    private String rejectionReason;
    private String businessNumber;

    private List<String> ownerRequestFiles;
}
