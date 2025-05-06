package io.jhchoe.familytree.config;

import io.jhchoe.familytree.common.auth.UserJpaEntity;
import io.jhchoe.familytree.common.auth.UserJpaRepository;
import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import java.util.List;

import io.jhchoe.familytree.core.user.domain.User;
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

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SecurityContext createSecurityContext(FTMockUser annotation) {
//        // User 도메인 객체 생성
//        User user = User.newUser(
//            annotation.email(),
//            annotation.name(),
//            null,
//            AuthenticationType.OAUTH2,
//            OAuth2Provider.GOOGLE
//        );
//
//        // UserJpaEntity로 변환 및 저장
//        UserJpaEntity userJpaEntity = UserJpaEntity.ofOAuth2User(user);
//        userJpaRepository.save(userJpaEntity);
//
//        // FTUser 생성 및 인증 토큰 설정
//        FTUser ftUser = FTUser.ofOAuth2User(
//            userJpaEntity.getId(),
//            userJpaEntity.getName(),
//            userJpaEntity.getEmail(),
//            userJpaEntity.getProfileUrl()
//        );
//
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//            ftUser,
//            null,
//            List.of(new SimpleGrantedAuthority("ROLE_USER"))
//        );
//
        SecurityContext context = SecurityContextHolder.createEmptyContext();
//        context.setAuthentication(token);

        return context;
    }
}
