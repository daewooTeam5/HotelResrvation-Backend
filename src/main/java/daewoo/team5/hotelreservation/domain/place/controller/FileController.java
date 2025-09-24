// src/main/java/daewoo/team5/hotelreservation/domain/place/controller/FileController.java
package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    // 실제 서비스에서는 @Value 등을 통해 외부 설정 파일에서 경로를 관리하는 것이 좋습니다.
    private final String uploadDir = Paths.get(System.getProperty("user.dir"), "uploads").toString();

    /**
     * 주석: 여러 이미지 파일을 서버에 업로드하고, 각 파일에 접근할 수 있는 URL 목록을 반환합니다.
     * @param files 클라이언트에서 전송한 MultipartFile 배열
     * @return 서버에서 접근 가능한 이미지 URL 목록
     */
    @PostMapping("/upload")
    public ApiResult<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {

        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs(); // 업로드 디렉토리가 없으면 생성
        }

        List<String> imageUrls = Arrays.stream(files).map(file -> {
            String originalName = file.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String savedName = UUID.randomUUID().toString() + extension;
            File target = new File(uploadDir, savedName);

            try {
                file.transferTo(target); // 파일을 실제 디스크에 저장
                // 클라이언트가 접근할 수 있는 URL을 반환해야 합니다.
                // 예: "http://localhost:8080/uploads/" + savedName
                // 이를 위해 WebMvcConfigurer 설정이 필요할 수 있습니다.
                // 여기서는 임시로 파일 이름만 반환하고, 프론트에서 경로를 조합합니다.
                return "/uploads/" + savedName; // 서버의 정적 리소스 경로
            } catch (IOException e) {
                // 실제로는 로깅 및 예외 처리를 더 견고하게 해야 합니다.
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        return ApiResult.ok(imageUrls, "이미지 업로드 성공");
    }
}