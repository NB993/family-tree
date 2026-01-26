package io.jhchoe.familytree.core.user.domain;

import java.time.LocalDateTime;

import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.domain.UserRole;
import io.jhchoe.familytree.core.family.domain.BirthdayType;
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
    private final BirthdayType birthdayType;

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
        LocalDateTime birthday,
        BirthdayType birthdayType
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
        this.birthdayType = birthdayType;
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
     * @param birthdayType 생년월일 유형 (양력/음력)
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
        LocalDateTime birthday,
        BirthdayType birthdayType
    ) {
        Objects.requireNonNull(id, "id는 null일 수 없습니다");
        Objects.requireNonNull(role, "role은 null일 수 없습니다");

        return new User(id, email, name, profileUrl, kakaoId, oAuth2Provider, role, deleted,
                createdBy, createdAt, modifiedBy, modifiedAt, birthday, birthdayType);
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
     * @param birthdayType 생년월일 유형 (양력/음력)
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
        LocalDateTime birthday,
        BirthdayType birthdayType
    ) {
        return new User(null, email, name, profileUrl, kakaoId, oAuth2Provider, role, deleted,
                null, null, null, null, birthday, birthdayType);
    }

    /**
     * 수동 등록 사용자를 생성합니다.
     * 로그인할 수 없는 사용자입니다 (애완동물, 아이 등).
     *
     * @param name 이름
     * @param profileUrl 프로필 URL
     * @param birthday 생년월일
     * @param birthdayType 생년월일 유형 (양력/음력)
     * @return 생성된 User 객체
     */
    public static User newManualUser(
        String name,
        String profileUrl,
        LocalDateTime birthday,
        BirthdayType birthdayType
    ) {
        return new User(null, null, name, profileUrl, null, null, UserRole.USER, false,
                null, null, null, null, birthday, birthdayType);
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

    /**
     * 사용자 프로필을 수정합니다.
     * 불변 객체이므로 새로운 User 객체를 반환합니다.
     *
     * @param name         새 이름 (필수)
     * @param birthday     새 생일 (nullable)
     * @param birthdayType 새 생일 유형 (nullable, birthday가 있으면 필수)
     * @return 수정된 User 객체
     * @throws NullPointerException     name이 null인 경우
     * @throws IllegalArgumentException name이 비어있거나, birthday와 birthdayType이 쌍으로 제공되지 않은 경우
     */
    public User modifyProfile(
        String name,
        LocalDateTime birthday,
        BirthdayType birthdayType
    ) {
        Objects.requireNonNull(name, "name은 null일 수 없습니다");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다");
        }
        if (birthday != null && birthdayType == null) {
            throw new IllegalArgumentException("생일이 있으면 생일 유형도 필수입니다");
        }
        if (birthday == null && birthdayType != null) {
            throw new IllegalArgumentException("생일 유형이 있으면 생일도 필수입니다");
        }

        return new User(
            this.id,
            this.email,
            name,
            this.profileUrl,
            this.kakaoId,
            this.oAuth2Provider,
            this.role,
            this.deleted,
            this.createdBy,
            this.createdAt,
            this.modifiedBy,
            this.modifiedAt,
            birthday,
            birthdayType
        );
    }
}
