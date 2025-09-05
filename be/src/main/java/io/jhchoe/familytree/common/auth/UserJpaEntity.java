package io.jhchoe.familytree.common.auth;

import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.domain.UserRole;
import io.jhchoe.familytree.common.exception.CommonExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.common.support.ModifierBaseEntity;
import io.jhchoe.familytree.core.user.domain.User;
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

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "users")
public class UserJpaEntity extends ModifierBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "profile_url")
    private String profileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type", nullable = false, length = 255)
    private AuthenticationType authenticationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth2_provider", length = 255)
    private OAuth2Provider oAuth2Provider;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 255)
    private UserRole role;

    @Column(name = "deleted")
    private boolean deleted;

    /**
     * OAuth2 로그인 기반의 회원 가입용 팩토리 메서드
     * @return 생성된 UserJpaEntity 객체
     */
    public static UserJpaEntity ofOAuth2User(User user) {
        Objects.requireNonNull(user, "user must not be null");

        return new UserJpaEntity(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getProfileUrl(),
                user.getAuthenticationType(),
                user.getOAuth2Provider(),
                user.getRole(),
                user.isDeleted(),
                user.getCreatedBy(),
                user.getCreatedAt(),
                user.getModifiedBy(),
                user.getModifiedAt()
        );
    }

    /**
     * 조회용 생성자
     * @param id
     * @param email
     * @param name
     * @param profileUrl
     * @param authenticationType
     * @param oAuth2Provider
     * @param role
     * @param deleted
     * @param createdBy
     * @param createdAt
     * @param modifiedBy
     * @param modifiedAt
     */
    private UserJpaEntity(
        final Long id,
        final String email,
        final String name,
        final String profileUrl,
        final AuthenticationType authenticationType,
        final OAuth2Provider oAuth2Provider,
        final UserRole role,
        final boolean deleted,
        final Long createdBy,
        final LocalDateTime createdAt,
        final Long modifiedBy,
        final LocalDateTime modifiedAt
    ) {
        super(createdBy, createdAt, modifiedBy, modifiedAt);
        this.id = id;
        this.email = email;
        this.name = name;
        this.profileUrl = profileUrl;
        this.authenticationType = authenticationType;
        this.oAuth2Provider = oAuth2Provider;
        this.role = role;
        this.deleted = deleted;
    }

    /**
     * UserJpaEntity를 User 도메인 객체로 변환
     *
     * @return 변환된 User 도메인 객체
     */
    public User toUser() {
        return User.withId(
                id,
                email,
                name,
                profileUrl,
                authenticationType,
                oAuth2Provider,
                role,
                deleted,
                getCreatedBy(),
                getCreatedAt(),
                getModifiedBy(),
                getModifiedAt()
        );
    }
}
