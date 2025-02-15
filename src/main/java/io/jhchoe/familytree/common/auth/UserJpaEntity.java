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
@Entity(name = "user")
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
     * 폼 로그인 기반의 회원 가입용 팩토리 메서드
     * @param email 회원 가입 사용자의 이메일 주소 (필수)
     * @param password 회원 가입 사용자의 비밀번호 (필수)
     * @return
     */
    public static UserJpaEntity ofFormLoginUser(
        final String email,
        final String password
    ) {
        if (email == null || password == null) {
            throw new FTException(CommonExceptionCode.MISSING_PARAMETER, "email");
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
