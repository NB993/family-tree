package io.jhchoe.familytree.core.user.domain;

import java.time.LocalDateTime;

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
    private final OAuth2Provider oAuth2Provider;
    private final UserRole role;
    private final boolean deleted;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final Long modifiedBy;
    private final LocalDateTime modifiedAt;
    private final LocalDateTime birthday;

    private User(
        Long id,
        String email,
        String name,
        String profileUrl,
        String kakaoId,
        OAuth2Provider oAuth2Provider,
        UserRole role,
        boolean deleted,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt,
        LocalDateTime birthday
    ) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.profileUrl = profileUrl;
        this.kakaoId = kakaoId;
        this.oAuth2Provider = oAuth2Provider;
        this.role = role;
        this.deleted = deleted;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
        this.birthday = birthday;
    }

    /**
     * 기존 사용자 정보로 User 객체를 생성합니다.
     *
     * @param id 사용자 ID
     * @param email 이메일 (nullable - 수동 등록 사용자)
     * @param name 이름
     * @param profileUrl 프로필 URL
     * @param kakaoId 카카오 ID
     * @param oAuth2Provider OAuth2 제공자 (nullable - 수동 등록 사용자)
     * @param role 사용자 역할
     * @param deleted 삭제 여부
     * @param createdBy 생성한 사용자 ID
     * @param createdAt 생성일시
     * @param modifiedBy 수정한 사용자 ID
     * @param modifiedAt 수정일시
     * @param birthday 생년월일
     * @return 생성된 User 객체
     */
    public static User withId(
        Long id,
        String email,
        String name,
        String profileUrl,
        String kakaoId,
        OAuth2Provider oAuth2Provider,
        UserRole role,
        boolean deleted,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt,
        LocalDateTime birthday
    ) {
        Objects.requireNonNull(id, "id는 null일 수 없습니다");
        Objects.requireNonNull(role, "role은 null일 수 없습니다");

        return new User(id, email, name, profileUrl, kakaoId, oAuth2Provider, role, deleted,
                createdBy, createdAt, modifiedBy, modifiedAt, birthday);
    }

    /**
     * 신규 사용자 정보로 User 객체를 생성합니다.
     *
     * @param email 이메일
     * @param name 이름
     * @param profileUrl 프로필 URL
     * @param kakaoId 카카오 ID
     * @param oAuth2Provider OAuth2 제공자
     * @param role 사용자 역할
     * @param deleted 삭제 여부
     * @param birthday 생년월일
     * @return 생성된 User 객체
     */
    public static User newUser(
        String email,
        String name,
        String profileUrl,
        String kakaoId,
        OAuth2Provider oAuth2Provider,
        UserRole role,
        boolean deleted,
        LocalDateTime birthday
    ) {
        return new User(null, email, name, profileUrl, kakaoId, oAuth2Provider, role, deleted,
                null, null, null, null, birthday);
    }

    /**
     * 수동 등록 사용자를 생성합니다.
     * 로그인할 수 없는 사용자입니다 (애완동물, 아이 등).
     *
     * @param name 이름
     * @param profileUrl 프로필 URL
     * @param birthday 생년월일
     * @return 생성된 User 객체
     */
    public static User newManualUser(
        String name,
        String profileUrl,
        LocalDateTime birthday
    ) {
        return new User(null, null, name, profileUrl, null, null, UserRole.USER, false,
                null, null, null, null, birthday);
    }

    /**
     * 사용자가 로그인 가능한지 확인합니다.
     * OAuth2 제공자가 설정된 사용자만 로그인할 수 있습니다.
     *
     * @return 로그인 가능 여부
     */
    public boolean isLoginable() {
        return oAuth2Provider != null;
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
