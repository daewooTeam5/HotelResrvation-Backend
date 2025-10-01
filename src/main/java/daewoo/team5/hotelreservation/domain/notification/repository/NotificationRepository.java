package daewoo.team5.hotelreservation.domain.notification.repository;

import daewoo.team5.hotelreservation.domain.notification.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    Page<NotificationEntity> findByUserIdOrUserIdIsNull (Long userId, Pageable pageable);
    @Query("""
    select count(*) 
    from Notification n 
    where n.user.id = :userId or n.user.id is null
    """)
    long countByUserId(Long userId);
}
