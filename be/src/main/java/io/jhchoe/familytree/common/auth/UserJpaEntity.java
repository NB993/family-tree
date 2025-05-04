package io.jhchoe.familytree.common.auth;

import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.exception.CommonExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.common.support.ModifierBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "users")
public class UserJpaEntity extends ModifierBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth2_provider", columnDefinition = "varchar(255)")
    private OAuth2Provider oAuth2Provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type", nullable = false, columnDefinition = "varchar(255)")
    private AuthenticationType authenticationType;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "name")
    private String name;

    @Column(name = "deleted")
    private boolean deleted;

    /**
     * OAuth2 로그인 기반의 회원 가입용 팩토리 메서드
     * @param email 회원 가입 사용자의 이메일 주소 (필수)
     * @param name 사용자 이름 (옵션)
     * @param profileUrl 사용자 프로필 이미지 URL (옵션)
     * @param oAuth2Provider OAuth2 제공자 (필수)
     * @return 생성된 UserJpaEntity 객체
     */
    public static UserJpaEntity ofOAuth2User(
        final String email,
        final String name,
        final String profileUrl,
        final OAuth2Provider oAuth2Provider
    ) {
        if (email == null) {
            throw new FTException(CommonExceptionCode.MISSING_PARAMETER, "email");
        }
        if (oAuth2Provider == null) {
            throw new FTException(CommonExceptionCode.MISSING_PARAMETER, "oAuth2Provider");
        }
        return new UserJpaEntity(null, email, null, oAuth2Provider, AuthenticationType.OAUTH2, profileUrl, name, false);
    }

    /**
     * 폼 로그인 기반의 회원 가입용 팩토리 메서드
     * @param email 회원 가입 사용자의 이메일 주소 (필수)
     * @param password 회원 가입 사용자의 비밀번호 (필수)
     * @return
     */
    public static UserJpaEntity ofFormLoginUser(
        final String email,
        final String password
    ) {
        if (email == null) {
            throw new FTException(CommonExceptionCode.MISSING_PARAMETER, "email");
        }
        if (password == null) {
            throw new FTException(CommonExceptionCode.MISSING_PARAMETER, "password");
        }
        return new UserJpaEntity(null, email, password, null, AuthenticationType.FORM_LOGIN, null, null, false);
    }

    /**
     * 조회용 생성자
     * @param id
     * @param email
     * @param password
     * @param oAuth2Provider
     * @param authenticationType
     * @param profileUrl
     * @param name
     * @param deleted
     */
    private UserJpaEntity(
        final Long id,
        final String email,
        final String password,
        final OAuth2Provider oAuth2Provider,
        final AuthenticationType authenticationType,
        final String profileUrl,
        final String name,
        final boolean deleted
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.oAuth2Provider = oAuth2Provider;
        this.authenticationType = authenticationType;
        this.profileUrl = profileUrl;
        this.name = name;
        this.deleted = deleted;
    }
}
