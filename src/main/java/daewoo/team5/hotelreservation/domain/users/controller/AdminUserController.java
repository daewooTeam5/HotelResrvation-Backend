package daewoo.team5.hotelreservation.domain.users.controller;

import daewoo.team5.hotelreservation.domain.users.dto.request.UserAllDataDTO;
import daewoo.team5.hotelreservation.domain.users.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/{userId}/all-data")
    public ResponseEntity<UserAllDataDTO> getAllUserData(@PathVariable Long userId) {
        UserAllDataDTO userData = adminUserService.getAllUserData(userId);
        return ResponseEntity.ok(userData);
    }
}