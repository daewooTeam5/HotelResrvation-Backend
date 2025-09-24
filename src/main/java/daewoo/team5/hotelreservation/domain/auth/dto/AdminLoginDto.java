package daewoo.team5.hotelreservation.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginDto {
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String adminId;
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String adminPassword;
}