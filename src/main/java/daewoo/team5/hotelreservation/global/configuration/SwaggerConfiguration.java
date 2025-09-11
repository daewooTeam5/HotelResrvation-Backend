package daewoo.team5.hotelreservation.global.configuration;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI swaggerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Reservation API")
                        .description("Hotel Reservation API 명세서")
                        .version("v1.0.0")
                );
    }
}
