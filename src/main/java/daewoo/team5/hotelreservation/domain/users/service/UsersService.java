package daewoo.team5.hotelreservation.domain.users.service;

import daewoo.team5.hotelreservation.domain.file.service.FileService;
import daewoo.team5.hotelreservation.domain.payment.entity.GuestEntity;
import daewoo.team5.hotelreservation.domain.payment.repository.GuestRepository;
import daewoo.team5.hotelreservation.domain.users.dto.OwnerRequestDto;
import daewoo.team5.hotelreservation.domain.users.dto.request.CreateUserDto;
import daewoo.team5.hotelreservation.domain.users.dto.request.LogInUserDto;
import daewoo.team5.hotelreservation.domain.users.dto.request.UserResponse;
import daewoo.team5.hotelreservation.domain.users.dto.request.UserUpdateDTO;
import daewoo.team5.hotelreservation.domain.users.entity.OwnerRequestEntity;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.MyInfoProjection;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.OwnerRequestRepository;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import daewoo.team5.hotelreservation.global.core.provider.JwtProvider;
import daewoo.team5.hotelreservation.global.exception.UserNotFoundException;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService {
    private final UsersRepository usersRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final GuestRepository guestRepository;
    private final FileService fileService;
    private final OwnerRequestRepository ownerRequestRepository;

    public GuestEntity getGuestByUser(UserProjection user) {
        return guestRepository.findByUsersId(user.getId()).orElseThrow(() -> new ApiException(404, "사용자 게스트 정보 없음", "해당 사용자의 게스트 정보가 존재하지 않습니다."));
    }

    public MyInfoProjection getUserById(Long id){
        return usersRepository.findById(id, MyInfoProjection.class).orElseThrow(() -> new ApiException(404, "사용자 정보 없음", "해당 사용자의 정보가 존재하지 않습니다."));

    }

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

    @Transactional
    public OwnerRequestEntity createOwnerRequest(Long userId, OwnerRequestDto requestDto, List<MultipartFile> documents) {
        Users users = usersRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        users.setPhone(requestDto.getPhone());
        users.setName(requestDto.getHotelName());

        OwnerRequestEntity saveOwnerRequest = ownerRequestRepository.save(
                OwnerRequestEntity.builder()
                        .user(users)
                        .businessNumber(requestDto.getBusinessNumber())
                        .status(OwnerRequestEntity.Status.PENDING)
                        .build()
        );
        for (MultipartFile file : documents) {
            fileService.uploadAndSave(file,userId,saveOwnerRequest.getId(),"owner_request",file.getName());
        }
        return null;
    }

    public OwnerRequestEntity getHotelOwnerStatus(Long userId) {
        // 유저 조회시 UserProjection 으로 민감정보 제외하고 조회
        return ownerRequestRepository.findTop1ByUserIdOrderByCreatedAtDesc(userId).orElse(null);
    }

    public Users updateUser(Long userId, UserUpdateDTO dto) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // DTO 값으로 엔티티 업데이트
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());

        return usersRepository.save(user); // 변경 사항 DB 반영
    }



}
