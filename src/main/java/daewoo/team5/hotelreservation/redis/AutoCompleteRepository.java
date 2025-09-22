package daewoo.team5.hotelreservation.redis;

import daewoo.team5.hotelreservation.domain.place.entity.Places;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoCompleteRepository extends JpaRepository<Places, Long> {

    @Query("SELECT DISTINCT r.sido FROM Region r WHERE r.sido LIKE CONCAT(:prefix, '%')")
    List<String> findRegions(@Param("prefix") String prefix);

    @Query("SELECT DISTINCT p.name FROM Places p WHERE p.name LIKE CONCAT(:prefix, '%')")
    List<String> findPlaces(@Param("prefix") String prefix);
}
