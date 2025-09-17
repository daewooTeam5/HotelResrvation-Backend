package daewoo.team5.hotelreservation.global.aop.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthUserAspect {
    private final UsersRepository usersRepository;

    @Around("@annotation(daewoo.team5.hotelreservation.global.aop.annotation.AuthUser)")
    public Object injectCurrentUser(ProceedingJoinPoint joinPoint) throws JsonProcessingException, Throwable {
        Object[] args = joinPoint.getArgs();

        // SecurityContext에서 userId 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return args;

        Object principal = auth.getPrincipal();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(principal.toString());
        UserProjection currentUser = usersRepository.findById(Long.parseLong(node.get("id").toString()), UserProjection.class)
                .orElseThrow(() -> new ApiException(404, "존재하지 않는 유저", "존재 하지 않는 유저입니다."));
        System.out.println(currentUser);

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof UserProjection || args[i] == null) {
                args[i] = currentUser;
            }
        }
        return joinPoint.proceed(args);
    }
}
