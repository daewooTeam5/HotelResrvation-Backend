package daewoo.team5.hotelreservation.global.aop.aspect;

import daewoo.team5.hotelreservation.domain.users.entity.UsersLegacy;
import daewoo.team5.hotelreservation.domain.users.repository.UsersLegacyRepository;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthUserAspect {
    private final UsersRepository usersRepository;

    @Before("@annotation(daewoo.team5.hotelreservation.global.aop.annotation.AuthUser)")
    public void injectCurrentUser(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        // SecurityContext에서 userId 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return;

        String principal = (String)auth.getPrincipal();

        UsersLegacy currentUser = usersRepository.findByUsername(principal)
                .orElseThrow(() -> new ApiException(401, "401E001", "인증되지 않은 유저"));

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof UsersLegacy || args[i] == null) {
                args[i] = currentUser;
            }
        }
    }
}
