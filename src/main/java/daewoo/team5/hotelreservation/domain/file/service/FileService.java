package daewoo.team5.hotelreservation.domain.file.service;

import daewoo.team5.hotelreservation.domain.place.entity.File;
import daewoo.team5.hotelreservation.domain.place.repository.FileRepository;
import daewoo.team5.hotelreservation.infrastructure.file.FileUploader;
import daewoo.team5.hotelreservation.infrastructure.file.UploadResult;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final FileUploader fileUploader;
    private final FileRepository fileRepository;

    // 파일을 로컬에 저장 후 DB 메타데이터 저장. 저장된 파일 URL 반환
    public String uploadAndSave(MultipartFile file,Long userId, Long domainId, String fileDomain, String fileName) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("업로드할 파일이 비어있습니다.");
        if (domainId == null) throw new IllegalArgumentException("domainId는 필수입니다.");
        if (fileDomain == null || fileDomain.isBlank()) throw new IllegalArgumentException("fileDomain은 필수입니다.");

        UploadResult result = fileUploader.uploadFile(file, fileName);

        File entity = File.builder()
                .userId(userId)
                .filename(result.getStoredName())
                .extension(result.getExtension())
                .filetype(result.getFiletype())
                .domainFileId(domainId)
                .domain(fileDomain)
                .url("http://localhost:8080/uploads/"+result.getUrl())
                .build();
        fileRepository.save(entity);
        return entity.getUrl();
    }

}
