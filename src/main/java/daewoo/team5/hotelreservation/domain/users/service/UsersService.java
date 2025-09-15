package daewoo.team5.hotelreservation.domain.users.service;

import daewoo.team5.hotelreservation.domain.users.dto.request.CreateUserDto;
import daewoo.team5.hotelreservation.domain.users.dto.request.LogInUserDto;
import daewoo.team5.hotelreservation.domain.users.entity.UsersLegacy;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersLegacyRepository;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import daewoo.team5.hotelreservation.global.core.provider.JwtProvider;
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
    private final UsersLegacyRepository usersLegacyRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public Map<String, String> login(LogInUserDto dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );
        UsersLegacy loginUser = usersRepository.findByUsername(dto.getUsername()).orElseThrow(() -> new ApiException(400, "로그인 실패", "아이디 또는 비밀번호가 일치하지 않습니다."));
        String accessToken = jwtProvider.generateToken(loginUser, JwtProvider.TokenType.ACCESS);
        String refreshToken = jwtProvider.generateToken(loginUser.getId(), JwtProvider.TokenType.REFRESH);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );

    }

    public UsersLegacy registerUser(CreateUserDto dto) {
        UsersLegacy user = usersLegacyRepository.save(
                UsersLegacy
                        .builder()
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .username(dto.getUsername())
                        .role(dto.getRole())
                        .build()
        );
        return user;

    }

    public Page<UserProjection> getAllUserPage(int start, int size) {
        return usersLegacyRepository.findAllBy(UserProjection.class,PageRequest.of(start,size));
    }
}
