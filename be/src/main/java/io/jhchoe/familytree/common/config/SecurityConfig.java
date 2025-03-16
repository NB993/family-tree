package io.jhchoe.familytree.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final FTSpringSecurityExceptionHandler ftSpringSecurityExceptionHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic(basic -> basic.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/signup").permitAll()
                .requestMatchers("/", "/login").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**") // H2-console에 대한 CSRF 비활성화
            )
            .exceptionHandling(customizer -> {
                customizer.authenticationEntryPoint(ftSpringSecurityExceptionHandler);
                customizer.accessDeniedHandler(ftSpringSecurityExceptionHandler);
            })
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin()) // H2-console의 iframe 화면 허용
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/") // 로그인 성공 후 기본 이동 경로
                .failureUrl("/login?error=true")
                .permitAll() // 로그인 요청은 인증 없이 접근 가능
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // 로그아웃 경로
                .logoutSuccessUrl("/") // 로그아웃 성공 시 이동 경로
                .invalidateHttpSession(true) // 세션 무효화
                .deleteCookies("JSESSIONID") // "JSESSIONID" 쿠키 삭제
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1) // 하나의 계정으로 최대 1개의 세션만 허용
                .maxSessionsPreventsLogin(false) // 새로운 로그인 시 기존 세션 종료
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
