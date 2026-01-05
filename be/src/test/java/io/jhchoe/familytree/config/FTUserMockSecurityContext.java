package io.jhchoe.familytree.config;

import io.jhchoe.familytree.common.auth.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@Configuration
@RequiredArgsConstructor
public class FTUserMockSecurityContext implements WithSecurityContextFactory<FTMockUser> {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SecurityContext createSecurityContext(FTMockUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        return context;
    }
}
