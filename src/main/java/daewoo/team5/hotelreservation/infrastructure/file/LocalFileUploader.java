package daewoo.team5.hotelreservation.infrastructure.file;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Primary
public class LocalFileUploader implements FileUploader {
    // 파일 저장만 담당하므로 레포지토리 의존 제거

    @Override
    public UploadResult uploadFile(MultipartFile file, String fileName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 비어있습니다.");
        }

        // 사용자 홈 디렉토리 아래 hotelUploader 디렉토리 생성 (OS 무관)
        Path uploadDir = Paths.get(System.getProperty("user.home"), "hotelUploader");
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉토리 생성 실패", e);
        }

        // 파일명 및 확장자 처리
        String originalName = file.getOriginalFilename();
        String originalExt = extractExtension(originalName);
        String desiredName = (fileName != null && !fileName.isBlank()) ? fileName.trim() : removeExtensionSafe(originalName);
        if (desiredName == null || desiredName.isBlank()) desiredName = UUID.randomUUID().toString();

        // 충돌 방지를 위해 UUID suffix 부여
        String finalFileName = desiredName + "-" + UUID.randomUUID();
        String ext = (originalExt != null && !originalExt.isBlank()) ? originalExt : guessExtensionFromContentType(file.getContentType());
        if (ext != null && !ext.isBlank()) finalFileName = finalFileName + "." + ext;

        Path destination = uploadDir.resolve(finalFileName);
        try {
            Files.copy(file.getInputStream(), destination);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        String filetype = toGeneralFileType(file.getContentType());
        String url = destination.toUri().toString(); // file:// URL 형태

        return new UploadResult(url, finalFileName, ext != null ? ext : "", filetype);
    }

    private String extractExtension(String filename) {
        if (filename == null) return null;
        int idx = filename.lastIndexOf('.');
        if (idx == -1 || idx == filename.length() - 1) return null;
        return filename.substring(idx + 1).toLowerCase();
    }

    private String removeExtensionSafe(String filename) {
        if (filename == null) return null;
        int idx = filename.lastIndexOf('.');
        if (idx <= 0) return filename; // no dot or hidden file like .env
        return filename.substring(0, idx);
    }

    private String guessExtensionFromContentType(String contentType) {
        if (contentType == null) return null;
        switch (contentType) {
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/webp":
                return "webp";
            case "video/mp4":
                return "mp4";
            case "application/pdf":
                return "pdf";
            default:
                return null;
        }
    }

    private String toGeneralFileType(String contentType) {
        if (contentType == null) return "document";
        if (contentType.startsWith("image/")) return "image";
        if (contentType.startsWith("video/")) return "video";
        return "document";
    }
}
