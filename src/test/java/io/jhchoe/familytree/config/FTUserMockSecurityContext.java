package io.jhchoe.familytree.config;

import io.jhchoe.familytree.common.auth.UserJpaEntity;
import io.jhchoe.familytree.common.auth.UserRepository;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@Configuration
@RequiredArgsConstructor
public class FTUserMockSecurityContext implements WithSecurityContextFactory<FTMockUser> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SecurityContext createSecurityContext(FTMockUser annotation) {
        UserJpaEntity user = UserJpaEntity.ofFormLoginUser(annotation.email(), passwordEncoder.encode(annotation.password()));
        userRepository.save(user);

        FTUser ftUser = FTUser.ofFormLoginUser(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPassword(),
            user.getEmail()
        );
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            ftUser,
            ftUser.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        //todo principal 보면 authorities에 ROLE_USER이 등록됨. 확인해야 함.

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(token);

        return context;
    }
}
