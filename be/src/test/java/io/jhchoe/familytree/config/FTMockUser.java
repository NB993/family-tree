package io.jhchoe.familytree.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = FTUserMockSecurityContext.class) //회원가입을 여기서 진행하고, FTUser를 스프링 시큐리티 컨텍스트 안에 미리 넣어줄 수 있다.
public @interface FTMockUser {

    String email() default "ftuser@email.com";

    String password() default "password";
}
