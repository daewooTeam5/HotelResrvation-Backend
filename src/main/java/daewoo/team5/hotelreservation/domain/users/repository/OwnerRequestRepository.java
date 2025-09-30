package daewoo.team5.hotelreservation.domain.users.repository;


import daewoo.team5.hotelreservation.domain.users.entity.OwnerRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRequestRepository extends JpaRepository<OwnerRequestEntity, Long> {
    Optional<OwnerRequestEntity> findTop1ByUserIdOrderByCreatedAtDesc(Long userId);
}
