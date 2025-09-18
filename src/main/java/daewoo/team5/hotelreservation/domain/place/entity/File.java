package daewoo.team5.hotelreservation.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 파일 ID

    @Column(name = "user_id", nullable = false)
    private Long userId; // 업로더 ID

    @Column(name = "filename", length = 100, nullable = false)
    private String filename; // 파일명

    @Column(name = "extension", length = 10, nullable = false)
    private String extension; // 확장자

    @Column(name = "filetype", length = 10, nullable = false)
    private String filetype; // 파일 유형 (image, video, document)

    @Column(name = "domain_file_id", nullable = false)
    private Long domainFileId; // 도메인 파일 ID (place, room, chat 등에 해당하는 ID)

    @Column(name = "domain", length = 20, nullable = false)
    private String domain; // 도메인명 (place, room, chat)

    @Column(name = "url", length = 200, nullable = false)
    private String url; // 파일 URL
}
