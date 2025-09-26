package daewoo.team5.hotelreservation.domain.users.service;

import daewoo.team5.hotelreservation.domain.users.dto.request.CreateUserDto;
import daewoo.team5.hotelreservation.domain.users.dto.request.LogInUserDto;
import daewoo.team5.hotelreservation.domain.users.dto.request.UserResponse;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import daewoo.team5.hotelreservation.global.core.provider.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService {
    private final UsersRepository usersRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public Map<String, String> login(LogInUserDto dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );
        Users loginUsers = usersRepository.findByName(dto.getUsername()).orElseThrow(() -> new ApiException(400, "로그인 실패", "아이디 또는 비밀번호가 일치하지 않습니다."));
        String accessToken = jwtProvider.generateToken(loginUsers, JwtProvider.TokenType.ACCESS);
        String refreshToken = jwtProvider.generateToken(loginUsers.getId(), JwtProvider.TokenType.REFRESH);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );

    }

    public Users registerUser(CreateUserDto dto) {
        Users users = usersRepository.save(
                Users
                        .builder()
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .name(dto.getUsername())
                        .role(Users.Role.valueOf(dto.getRole()))
                        .build()
        );
        return users;

    }

    public Page<UserProjection> getAllUserPage(int start, int size) {
        return usersRepository.findAllBy(UserProjection.class,PageRequest.of(start,size));
    }

    public Page<UserResponse> getAllUsers(int start, int size) {
        Page<Users> usersPage = usersRepository.findAll(PageRequest.of(start, size));
        return usersPage.map(u ->
                new UserResponse(
                        u.getId(),
                        u.getUserId(),
                        u.getEmail(),
                        u.getName(),
                        u.getPhone(),
                        u.getRole(),
                        u.getStatus(),
                        u.getPoint()
                ));
    }

    @Transactional
    public void allowUser(Long id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID: " + id));
        if (user.getStatus() == Users.Status.inactive) {
            user.setStatus(Users.Status.active);
            usersRepository.save(user);
        }
    }

    // 취소
    @Transactional
    public void cancelUser(Long id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID: " + id));
        if (user.getStatus() == Users.Status.inactive) {
            user.setStatus(Users.Status.banned);
            usersRepository.save(user);
        }
    }
}
