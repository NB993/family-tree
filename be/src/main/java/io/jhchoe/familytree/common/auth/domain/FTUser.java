package io.jhchoe.familytree.common.auth.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * 인증된 사용자 정보
 */
public class FTUser extends User implements OAuth2User {

    private final Long id;
    private final String name;
    private final String email;
    private final AuthenticationType authType;
    private final Map<String, Object> attributes;
    private final OAuth2Provider oAuth2Provider;

    public static FTUser ofOAuth2User(
        final Long id,
        final String name,
        final String email,
        final OAuth2Provider oAuth2Provider,
        final Map<String, Object> attributes
    ) {
        return new FTUser(
            id,
            name,
            email, // username으로 email 사용
            "", // OAuth2 사용자는 비밀번호 없음
            email,
            AuthenticationType.OAUTH2,
            oAuth2Provider,
            List.of(new SimpleGrantedAuthority(UserRole.USER.getValue())),
            attributes
        );
    }

    public static FTUser ofFormLoginUser(
        final Long id,
        final String name,
        final String username,
        final String password,
        final String email
    ) {
        return new FTUser(
            id,
            name,
            username,
            password,
            email,
            AuthenticationType.FORM_LOGIN,
            null,
            List.of(new SimpleGrantedAuthority(UserRole.USER.getValue())),
            Collections.emptyMap() // 빈 attributes
        );
    }

    private FTUser(
        final Long id,
        final String name,
        final String username,
        final String password,
        final String email,
        final AuthenticationType authType,
        final OAuth2Provider oAuth2Provider,
        final Collection<? extends GrantedAuthority> authorities,
        final Map<String, Object> attributes
    ) {
        super(username, password, authorities);
        this.id = id;
        this.name = name;
        this.email = email;
        this.authType = authType;
        this.oAuth2Provider = oAuth2Provider;
        this.attributes = attributes;
    }

    public Long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        //todo OAuth2 유저의 경우 name속성,
        // firstName, lastName
        // name
        return this.name;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    public String getEmail() {
        return this.email;
    }

    public AuthenticationType getAuthType() {
        return this.authType;
    }

    public OAuth2Provider getOAuth2Provider() {
        return this.oAuth2Provider;
    }
}
