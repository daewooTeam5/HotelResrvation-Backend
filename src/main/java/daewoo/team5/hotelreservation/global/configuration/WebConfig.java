// src/main/java/daewoo/team5/hotelreservation/global/configuration/WebConfig.java
package daewoo.team5.hotelreservation.global.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadPath = "file:///" + Paths.get(System.getProperty("user.dir"), "uploads").toString() + "/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 주석: /uploads/** URL 요청이 오면, 실제 디스크의 uploadPath 경로에서 파일을 찾아 제공합니다.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);

    }
}