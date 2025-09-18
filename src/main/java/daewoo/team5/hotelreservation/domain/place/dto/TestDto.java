package daewoo.team5.hotelreservation.domain.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TestDto", description = "테스트용 데이터 전송 객체")
public class TestDto {

    @Schema(description = "사용자 이름", example = "T1 Gumayusi")
    @NotBlank(message = "사용자 이름은 필수입니다")
    private String name;

    @Schema(description = "라인", example = "원딜")
    @NotBlank(message = "사용자 라인은 필수입니다")
    private String line;
}
