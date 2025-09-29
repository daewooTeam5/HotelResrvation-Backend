package daewoo.team5.hotelreservation.infrastructure.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {
    // 파일을 로컬에 저장하고 결과(경로, 실제 저장 파일명, 확장자, 파일타입)를 반환
    UploadResult uploadFile(MultipartFile file, String fileName);
}
