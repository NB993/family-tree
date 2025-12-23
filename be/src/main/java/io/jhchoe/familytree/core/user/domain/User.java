package io.jhchoe.familytree.core.user.domain;

import java.time.LocalDateTime;

import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.domain.UserRole;
import lombok.Getter;
import java.util.Objects;

/**
 * 사용자 도메인 모델
 */
@Getter
public class User {
    private final Long id;
    private final String email;
    private final String name;
    private final String profileUrl;
    private final String kakaoId;
    private final AuthenticationType authenticationType;
    private final OAuth2Provider oAuth2Provider;
    private final UserRole role;
    private final boolean deleted;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final Long modifiedBy;
    private final LocalDateTime modifiedAt;

    private User(
        Long id,
        String email,
        String name,
        String profileUrl,
        String kakaoId,
        AuthenticationType authenticationType,
        OAuth2Provider oAuth2Provider,
        UserRole role,
        boolean deleted,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.profileUrl = profileUrl;
        this.kakaoId = kakaoId;
        this.authenticationType = authenticationType;
        this.oAuth2Provider = oAuth2Provider;
        this.role = role;
        this.deleted = deleted;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }

    /**
     * 기존 사용자 정보로 User 객체를 생성합니다.
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
     * @param createdBy 생성한 사용자 ID
     * @param createdAt 생성일시
     * @param modifiedBy 수정한 사용자 ID
     * @param modifiedAt 수정일시
     * @return 생성된 User 객체
     */
    public static User withId(
        Long id,
        String email,
        String name,
        String profileUrl,
        String kakaoId,
        AuthenticationType authenticationType,
        OAuth2Provider oAuth2Provider,
        UserRole role,
        boolean deleted,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(oAuth2Provider, "oAuth2Provider must not be null");
        Objects.requireNonNull(authenticationType, "authenticationType must not be null");
        Objects.requireNonNull(role, "role must not be null");

        return new User(id, email, name, profileUrl, kakaoId, authenticationType, oAuth2Provider, role, deleted,
                createdBy, createdAt, modifiedBy, modifiedAt);
    }

    /**
     * 신규 사용자 정보로 User 객체를 생성합니다.
     *
     * @param email 이메일
     * @param name 이름
     * @param profileUrl 프로필 URL
     * @param kakaoId 카카오 ID
     * @param authenticationType 인증유형
     * @param oAuth2Provider OAuth2 제공자
     * @param role 사용자 역할
     * @param deleted 삭제 여부
     * @return 생성된 User 객체
     */
    public static User newUser(
        String email,
        String name,
        String profileUrl,
        String kakaoId,
        AuthenticationType authenticationType,
        OAuth2Provider oAuth2Provider,
        UserRole role,
        boolean deleted
    ) {
        return new User(null, email, name, profileUrl, kakaoId, authenticationType, oAuth2Provider, role, deleted, null, null, null, null);
    }

    /**
     * 사용자의 이름에 주어진 문자열이 포함되어 있는지 확인합니다.
     *
     * @param nameToCompare 사용자의 이름과 비교할 문자열
     * @return 사용자의 이름에 주어진 문자열이 포함되어 있으면 true, 그렇지 않으면 false
     */
    public boolean hasName(String nameToCompare) {
        return Objects.requireNonNullElse(this.name, "")
                .contains(Objects.requireNonNullElse(nameToCompare, ""));
    }
}
