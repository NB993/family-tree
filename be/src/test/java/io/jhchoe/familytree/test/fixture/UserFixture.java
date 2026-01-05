package io.jhchoe.familytree.test.fixture;

import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.domain.UserRole;
import io.jhchoe.familytree.core.user.domain.User;
import java.time.LocalDateTime;

/**
 * User 테스트 픽스처 생성을 위한 헬퍼 클래스.
 * <p>
 * 기본값이 설정되어 있어 필요한 필드만 오버라이드하여 사용할 수 있습니다.
 * 도메인 클래스 변경 시 이 클래스만 수정하면 모든 테스트에 반영됩니다.
 */
public final class UserFixture {

    // 기본값 상수
    private static final String DEFAULT_EMAIL = "test@example.com";
    private static final String DEFAULT_NAME = "테스트사용자";
    private static final String DEFAULT_PROFILE_URL = "https://example.com/profile.jpg";
    private static final String DEFAULT_KAKAO_ID = "kakao_test_id";
    private static final LocalDateTime DEFAULT_BIRTHDAY = LocalDateTime.of(1990, 1, 1, 0, 0);

    private UserFixture() {
    }

    // ==================== 신규 생성 (ID 없음) ====================

    /**
     * 기본 OAuth2 사용자를 생성합니다 (Google).
     */
    public static User newOAuth2User() {
        return newOAuth2User(OAuth2Provider.GOOGLE);
    }

    /**
     * 지정된 OAuth2 제공자로 사용자를 생성합니다.
     */
    public static User newOAuth2User(OAuth2Provider provider) {
        return newOAuth2User(DEFAULT_EMAIL, DEFAULT_NAME, provider);
    }

    /**
     * 지정된 이메일, 이름, OAuth2 제공자로 사용자를 생성합니다.
     */
    public static User newOAuth2User(String email, String name, OAuth2Provider provider) {
        String kakaoId = provider == OAuth2Provider.KAKAO ? DEFAULT_KAKAO_ID : null;
        return User.newUser(email, name, DEFAULT_PROFILE_URL, kakaoId,
            provider, UserRole.USER, false, DEFAULT_BIRTHDAY);
    }

    /**
     * 수동 등록 사용자를 생성합니다 (NONE 타입, 로그인 불가).
     */
    public static User newManualUser() {
        return newManualUser(DEFAULT_NAME);
    }

    /**
     * 지정된 이름으로 수동 등록 사용자를 생성합니다.
     */
    public static User newManualUser(String name) {
        return User.newManualUser(name, DEFAULT_PROFILE_URL, DEFAULT_BIRTHDAY);
    }

    // ==================== ID 포함 (기존 엔티티 복원) ====================

    /**
     * 지정된 ID로 기본 OAuth2 사용자를 생성합니다 (Google).
     */
    public static User withId(Long id) {
        return withId(id, OAuth2Provider.GOOGLE);
    }

    /**
     * 지정된 ID와 OAuth2 제공자로 사용자를 생성합니다.
     */
    public static User withId(Long id, OAuth2Provider provider) {
        return withId(id, DEFAULT_EMAIL, DEFAULT_NAME, provider, UserRole.USER, false);
    }

    /**
     * 지정된 ID, 이메일, 이름, OAuth2 제공자, 역할, 삭제 여부로 사용자를 생성합니다.
     */
    public static User withId(Long id, String email, String name, OAuth2Provider provider, UserRole role, boolean deleted) {
        String kakaoId = provider == OAuth2Provider.KAKAO ? DEFAULT_KAKAO_ID : null;
        return User.withId(id, email, name, DEFAULT_PROFILE_URL, kakaoId,
            provider, role, deleted,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now(), DEFAULT_BIRTHDAY);
    }

    /**
     * 지정된 ID와 역할로 사용자를 생성합니다.
     */
    public static User withIdAndRole(Long id, UserRole role) {
        return withId(id, DEFAULT_EMAIL, DEFAULT_NAME, OAuth2Provider.GOOGLE, role, false);
    }

    /**
     * 지정된 ID로 삭제된 사용자를 생성합니다.
     */
    public static User withIdDeleted(Long id) {
        return withId(id, DEFAULT_EMAIL, DEFAULT_NAME, OAuth2Provider.GOOGLE, UserRole.USER, true);
    }

    /**
     * 지정된 ID로 수동 등록 사용자를 생성합니다 (NONE 타입).
     */
    public static User withIdManual(Long id) {
        return withIdManual(id, DEFAULT_NAME);
    }

    /**
     * 지정된 ID와 이름으로 수동 등록 사용자를 생성합니다.
     */
    public static User withIdManual(Long id, String name) {
        return User.withId(id, null, name, DEFAULT_PROFILE_URL, null,
            null, UserRole.USER, false,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now(), DEFAULT_BIRTHDAY);
    }

    /**
     * 지정된 ID와 생일로 사용자를 생성합니다.
     */
    public static User withIdAndBirthday(Long id, LocalDateTime birthday) {
        return User.withId(id, DEFAULT_EMAIL, DEFAULT_NAME, DEFAULT_PROFILE_URL, null,
            OAuth2Provider.GOOGLE, UserRole.USER, false,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now(), birthday);
    }
}
