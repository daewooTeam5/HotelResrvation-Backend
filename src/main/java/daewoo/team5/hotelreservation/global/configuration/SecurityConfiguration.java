package daewoo.team5.hotelreservation.global.configuration;

import daewoo.team5.hotelreservation.global.core.security.CustomAccessDeniedHandler;
import daewoo.team5.hotelreservation.global.core.security.CustomAuthenticationEntryPoint;
import daewoo.team5.hotelreservation.global.core.security.CustomUserDetailsService;
import daewoo.team5.hotelreservation.global.core.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 적용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/uploads/*",
                                "/signup",
                                "/admin/login",
                                "/auth",
                                "/auth/code",
                                "/auth/token",
                                "/test",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/users",
                                "/api/v1/users/login",
                                "/api/v1/reservations/**",
                                "/api/v1/places/**",
                                "/api/v1/rooms/*",
                                "/images/**",
                                "/hotel/publishing/register",
                                "/api/v1/owner/rooms/**",
                                "/api/v1/autocomplete",
                                "/api/v1/statistics/**",
                                "/api/v1/payment/**",
                                "/api/v1/amenities/*",
                                "/api/v1/owner/coupons",
                                "/images/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/reservations/**","/api/v1/statistics/**","/api/v1/dashboard/**","/api/v1/owner/coupons/**", "/api/v1/owner/inventory/**", "/api/v1/owner/rooms/**")
                        .hasAnyRole("admin", "hotel_owner")
                        .requestMatchers("/api/v1/payment/dashboard/**")
                        .hasAnyRole("admin", "user_admin", "place_admin")
                        .requestMatchers("/api/v1/admin/places/**")
                        .hasAnyRole("admin", "place_admin")
                        .requestMatchers("/api/v1/admin/**")
                        .hasAnyRole("admin", "user_admin")
                        .requestMatchers("/api/v1/reservations/**","/api/v1/statistics/**","/api/v1/dashboard/**","/api/v1/owner/coupons/**", "/api/v1/owner/inventory/**", "/api/v1/owner/rooms/**")
                        .hasAnyRole("admin", "hotel_owner")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // CORS 설정 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("http://localhost:5173","https://127.0.0.1:5173")); // 모든 Origin 허용
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH")); // 모든 HTTP 메서드 허용
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // 쿠키/Authorization 헤더 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
