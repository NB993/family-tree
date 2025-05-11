package io.jhchoe.familytree.config;

import io.jhchoe.familytree.common.auth.domain.FTUser;
import java.util.Optional;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@TestConfiguration
@EnableJpaAuditing
public class TestAuditConfig {

    @Bean
    public AuditorAware<Long> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof FTUser) {
                return Optional.of(((FTUser) authentication.getPrincipal()).getId());
            }
            return Optional.of(99L); // 시스템 사용자 ID
        };
    }
}
