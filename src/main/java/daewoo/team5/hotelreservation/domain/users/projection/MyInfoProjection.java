package daewoo.team5.hotelreservation.domain.users.projection;


import daewoo.team5.hotelreservation.domain.users.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

public interface MyInfoProjection {
    String getName();
    String getEmail();
    String getUserId();
    Long getId();
    String getRole();
    String getPhone();
    String getStatus();
    LocalDateTime createdAt();
    Long getPoint();
}
