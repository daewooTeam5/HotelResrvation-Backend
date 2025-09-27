package daewoo.team5.hotelreservation.domain.place.service; // 경로는 프로젝트에 맞게 조정

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

    public FileUploadService(@Value("${file.upload-dir}") String uploadDir) {
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
}