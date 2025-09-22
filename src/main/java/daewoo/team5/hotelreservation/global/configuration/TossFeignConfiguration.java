package daewoo.team5.hotelreservation.global.configuration;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


@Configuration
public class TossFeignConfiguration {
    @Value("${TOSS_SECRET_KEY}")
    private String tossSecretKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // SecretKey:base64 인코딩
            String encodedAuth = Base64.getEncoder()
                    .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

            requestTemplate.header("Authorization", "Basic " + encodedAuth);
            requestTemplate.header("Content-Type", "application/json");
        };
    }
}
