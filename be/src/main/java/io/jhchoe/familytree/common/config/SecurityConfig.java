package io.jhchoe.familytree.common.config;

import io.jhchoe.familytree.common.auth.filter.JwtAuthenticationFilter;
import io.jhchoe.familytree.common.auth.handler.OAuth2JwtSuccessHandler;
import io.jhchoe.familytree.common.auth.service.OAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final FTSpringSecurityExceptionHandler ftSpringSecurityExceptionHandler;
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2JwtSuccessHandler oAuth2JwtSuccessHandler;
    private final CorsProperties corsProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic(basic -> basic.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/**") // API 엔드포인트에 대한 CSRF 비활성화 (JWT 사용)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT 사용으로 세션을 Stateless로 설정
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers("/docs/**").permitAll() // API 문서 접근 허용
                .requestMatchers("/api/auth/refresh").permitAll() // 토큰 갱신은 인증 없이 허용
                .requestMatchers(HttpMethod.GET, "/api/invites/my").hasAnyRole("USER", "ADMIN") // 내 초대 목록은 인증 필요
                .requestMatchers(HttpMethod.GET, "/api/invites/*").permitAll() // GET 초대 코드 조회만 익명 접근 허용
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/signup").permitAll()
                .requestMatchers("/", "/login").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(customizer -> {
                customizer.authenticationEntryPoint(ftSpringSecurityExceptionHandler);
                customizer.accessDeniedHandler(ftSpringSecurityExceptionHandler);
            })
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin()) // H2-console의 iframe 화면 허용
            )
            .oauth2Login(oauth2 -> oauth2 // OAuth2 로그인 설정 추가
                .loginPage("/login")
                .successHandler(oAuth2JwtSuccessHandler) // JWT 토큰 발급 핸들러 적용
                .failureUrl("/login?error=true")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oAuth2UserService)
                )
            )
            // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 추가
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
