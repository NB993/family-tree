package io.jhchoe.familytree.config;

import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * WithMockOAuth2User 애노테이션을 처리하는 팩토리 클래스
 * JWT 인증 시스템과 호환되도록 UsernamePasswordAuthenticationToken을 생성합니다.
 */
public class WithMockOAuth2UserSecurityContextFactory implements WithSecurityContextFactory<WithMockOAuth2User> {

    @Override
    public SecurityContext createSecurityContext(WithMockOAuth2User annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        
        // OAuth2 사용자 속성 생성 - 제공자별로 속성 구성 다르게 처리
        Map<String, Object> attributes = createAttributes(
            annotation.id(), 
            annotation.name(), 
            annotation.email(),
            annotation.provider()
        );
        
        // FTUser 객체 생성 (OAuth2 방식으로 생성하되 JWT 인증과 동일한 토큰 타입 사용)
        FTUser principal = FTUser.ofOAuth2User(
            annotation.id(),
            annotation.name(),
            annotation.email(),
            annotation.provider(),
            attributes
        );
        
        // 실제 JWT 인증과 동일한 UsernamePasswordAuthenticationToken 생성
        Authentication auth = new UsernamePasswordAuthenticationToken(
            principal, 
            null, // credentials
            principal.getAuthorities()
        );
        
        // 웹 인증 세부 정보 설정 (실제 필터와 동일한 방식)
        // 테스트 환경에서는 WebAuthenticationDetailsSource 없이 진행
        
        context.setAuthentication(auth);
        return context;
    }
    
    /**
     * 제공자별로 적절한 속성 맵 생성
     */
    private Map<String, Object> createAttributes(Long id, String name, String email, OAuth2Provider provider) {
        Map<String, Object> attributes = new HashMap<>();
        
        switch (provider) {
            case GOOGLE:
                attributes.put("sub", String.valueOf(id));
                attributes.put("name", name);
                attributes.put("email", email);
                attributes.put("picture", "https://example.com/test-profile.jpg");
                break;
                
            case KAKAO:
                attributes.put("id", id);
                
                // 카카오 계정 정보 추가
                Map<String, Object> kakaoAccount = new HashMap<>();
                kakaoAccount.put("email", email);
                
                // 프로필 정보 추가
                Map<String, Object> profile = new HashMap<>();
                profile.put("nickname", name);
                profile.put("profile_image_url", "https://example.com/kakao-profile.jpg");
                kakaoAccount.put("profile", profile);
                
                attributes.put("kakao_account", kakaoAccount);
                
                // 속성 정보도 추가 (레거시 지원)
                Map<String, Object> properties = new HashMap<>();
                properties.put("nickname", name);
                properties.put("profile_image", "https://example.com/kakao-profile.jpg");
                attributes.put("properties", properties);
                break;
                
            default:
                // 기본 속성
                attributes.put("sub", String.valueOf(id));
                attributes.put("name", name);
                attributes.put("email", email);
                break;
        }
        
        return attributes;
    }
}
