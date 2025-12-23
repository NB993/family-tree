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

    @Column(name = "kakao_id")
    private String kakaoId;

    /**
     * OAuth2 로그인 기반의 회원 가입용 팩토리 메서드
     *
     * @param user User 도메인 객체
     * @return 생성된 UserJpaEntity 객체
     */
    public static UserJpaEntity ofOAuth2User(User user) {
        Objects.requireNonNull(user, "user must not be null");

        return new UserJpaEntity(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getProfileUrl(),
                user.getKakaoId(),
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
     * 조회용 생성자입니다.
     *
     * @param id 사용자 ID
     * @param email 이메일
     * @param name 이름
     * @param profileUrl 프로필 URL
     * @param kakaoId 카카오 ID
     * @param authenticationType 인증 유형
     * @param oAuth2Provider OAuth2 제공자
     * @param role 사용자 역할
     * @param deleted 삭제 여부
     * @param createdBy 생성자
     * @param createdAt 생성일시
     * @param modifiedBy 수정자
     * @param modifiedAt 수정일시
     */
    private UserJpaEntity(
        final Long id,
        final String email,
        final String name,
        final String profileUrl,
        final String kakaoId,
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
        this.kakaoId = kakaoId;
        this.authenticationType = authenticationType;
        this.oAuth2Provider = oAuth2Provider;
        this.role = role;
        this.deleted = deleted;
    }

    /**
     * UserJpaEntity를 User 도메인 객체로 변환합니다.
     *
     * @return 변환된 User 도메인 객체
     */
    public User toUser() {
        return User.withId(
                id,
                email,
                name,
                profileUrl,
                kakaoId,
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
