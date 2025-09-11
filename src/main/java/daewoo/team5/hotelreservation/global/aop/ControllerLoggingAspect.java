package daewoo.team5.hotelreservation.global.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {
    @Autowired
    private HttpServletRequest request;

    // 컨트롤러 패키지 안에 있는 모든 메서드
    @Pointcut("within(daewoo.team5.hotelreservation.domain..controller..*)")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object logExecutionTimeAndRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // 요청 정보
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();

        String params = Arrays.toString(joinPoint.getArgs());

        log.info("[REQUEST] {} {}?{} | 메서드: {} | 파라미터: {}", method, uri,
                query == null ? "" : query, joinPoint.getSignature().toShortString(), params);

        Object result;
        try {
            result = joinPoint.proceed(); // 원래 컨트롤러 메서드 실행
        } catch (Throwable ex) {
            log.error("[ERROR] {} | 예외: {}", joinPoint.getSignature().toShortString(), ex.getMessage());
            throw ex;
        }

        long duration = System.currentTimeMillis() - start;
        log.info("[RESPONSE] {} | 처리시간: {}ms", joinPoint.getSignature().toShortString(), duration);

        return result;
    }
}
