package io.jhchoe.familytree.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic(basic -> basic.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // ADMIN만 접근 가능
                .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN") // USER 또는 ADMIN 가능
                .requestMatchers("/", "/login/**").permitAll() // 모두 접근 가능
                .anyRequest().authenticated() // 나머지 요청은 인증 필요
            )
            .formLogin(form -> form
//                .loginPage("/login") // 커스텀 로그인 페이지 경로
                .defaultSuccessUrl("/") // 로그인 성공 후 기본 이동 경로
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

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        // In-memory 방식으로 사용자 인증 정보를 관리
        UserDetails admin = User.withUsername("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("ADMIN")
            .build();

        UserDetails user = User.withUsername("user")
            .password(passwordEncoder().encode("user123"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
}
