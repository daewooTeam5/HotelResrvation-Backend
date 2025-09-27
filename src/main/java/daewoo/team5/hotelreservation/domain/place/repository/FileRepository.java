package daewoo.team5.hotelreservation.domain.place.repository;


import daewoo.team5.hotelreservation.domain.place.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByDomainAndDomainFileId(String domain, Long domainFileId);
    List<File> findByDomainAndDomainFileIdIn(String domain, List<Long> domainFileIds);
}
