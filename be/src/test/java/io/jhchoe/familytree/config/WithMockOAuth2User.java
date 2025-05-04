package io.jhchoe.familytree.config;

import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OAuth2 인증된 사용자로 테스트하기 위한 애노테이션
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockOAuth2UserSecurityContextFactory.class)
public @interface WithMockOAuth2User {
    
    /**
     * 사용자 ID
     */
    long id() default 1L;

    /**
     * 사용자 이름
     */
    String name() default "테스트사용자";

    /**
     * 사용자 이메일
     */
    String email() default "test@example.com";

    /**
     * OAuth2 제공자
     */
    OAuth2Provider provider() default OAuth2Provider.GOOGLE;
}
