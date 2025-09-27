package daewoo.team5.hotelreservation.domain.notification.repository;

import daewoo.team5.hotelreservation.domain.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
}
