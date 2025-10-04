package daewoo.team5.hotelreservation.domain.place.service; // 경로는 프로젝트에 맞게 조정

import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.domain.place.entity.File;
import daewoo.team5.hotelreservation.domain.place.repository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {

    private final Path fileStorageLocation;
    private final FileRepository fileRepository;

    public FileUploadService(@Value("${file.upload-dir}") String uploadDir, FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("파일을 업로드할 디렉토리를 생성할 수 없습니다.", ex);
        }
    }


    public String storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        try {
            if(fileName.contains("..")) {
                throw new RuntimeException("파일 이름에 부적합한 문자가 포함되어 있습니다. " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 중요: DB에 저장하고 클라이언트에게 돌려줄 경로
            // 예: "/uploads/" + fileName
            // 실제 서비스에서는 도메인까지 포함된 전체 URL을 반환하는 것이 좋습니다.
            return "/uploads/" + fileName;

        } catch (IOException ex) {
            throw new RuntimeException(fileName + " 파일을 저장할 수 없습니다. 다시 시도해 주세요.", ex);
        }
    }


    public File storeProfileImage(MultipartFile file, Long userId) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("잘못된 파일명: " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            File savedFile = File.builder()
                    .userId(userId)
                    .filename(originalFileName)
                    .extension(extension.replace(".", ""))
                    .filetype("image")
                    .domain("profile")              // ✅ 프로필 도메인
                    .domainFileId(userId)           // ✅ 유저 id 매핑
                    .url("/uploads/" + fileName)    // 클라이언트에서 접근 가능하도록
                    .build();

            return fileRepository.save(savedFile);

        } catch (IOException ex) {
            throw new RuntimeException(fileName + " 파일 저장 실패", ex);
        }
    }
}