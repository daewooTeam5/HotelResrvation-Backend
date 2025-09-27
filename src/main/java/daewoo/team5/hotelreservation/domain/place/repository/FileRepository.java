package daewoo.team5.hotelreservation.domain.place.repository;

import aj.org.objectweb.asm.commons.Remapper;
import daewoo.team5.hotelreservation.domain.place.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByDomainAndDomainFileId(String domain, Long domainFileId);
    List<File> findByDomainAndDomainFileIdIn(String domain, List<Long> domainFileIds);

    void deleteByDomainAndDomainFileId(String domain, Long domainFileId);
    void deleteByDomainAndDomainFileIdIn(String domain, List<Long> domainFileIds);

    Optional<File> findFirstByDomainAndDomainFileId(String domain, Long domainFileId);
}