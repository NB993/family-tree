package io.jhchoe.familytree.test.fixture;

import io.jhchoe.familytree.core.family.domain.Family;
import java.time.LocalDateTime;

/**
 * Family 테스트 픽스처 생성을 위한 헬퍼 클래스.
 * <p>
 * 기본값이 설정되어 있어 필요한 필드만 오버라이드하여 사용할 수 있습니다.
 * 도메인 클래스 변경 시 이 클래스만 수정하면 모든 테스트에 반영됩니다.
 */
public final class FamilyFixture {

    // 기본값 상수
    private static final String DEFAULT_NAME = "테스트가족";
    private static final String DEFAULT_DESCRIPTION = "테스트 가족 설명";
    private static final String DEFAULT_PROFILE_URL = "https://example.com/family-profile.jpg";
    private static final Boolean DEFAULT_IS_PUBLIC = true;

    private FamilyFixture() {
    }

    // ==================== 신규 생성 (ID 없음) ====================

    /**
     * 기본값으로 Family를 생성합니다.
     */
    public static Family newFamily() {
        return Family.newFamily(DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_PROFILE_URL, DEFAULT_IS_PUBLIC);
    }

    /**
     * 지정된 이름으로 Family를 생성합니다.
     */
    public static Family newFamily(String name) {
        return Family.newFamily(name, DEFAULT_DESCRIPTION, DEFAULT_PROFILE_URL, DEFAULT_IS_PUBLIC);
    }

    /**
     * 지정된 이름과 설명으로 Family를 생성합니다.
     */
    public static Family newFamily(String name, String description) {
        return Family.newFamily(name, description, DEFAULT_PROFILE_URL, DEFAULT_IS_PUBLIC);
    }

    /**
     * 모든 파라미터를 지정하여 Family를 생성합니다.
     */
    public static Family newFamily(String name, String description, String profileUrl, Boolean isPublic) {
        return Family.newFamily(name, description, profileUrl, isPublic);
    }

    // ==================== ID 포함 (기존 엔티티 복원) ====================

    /**
     * 지정된 ID로 기본 Family를 생성합니다.
     */
    public static Family withId(Long id) {
        return withId(id, DEFAULT_NAME);
    }

    /**
     * 지정된 ID와 이름으로 Family를 생성합니다.
     */
    public static Family withId(Long id, String name) {
        return withId(id, name, DEFAULT_IS_PUBLIC);
    }

    /**
     * 지정된 ID, 이름, 공개여부로 Family를 생성합니다.
     */
    public static Family withId(Long id, String name, Boolean isPublic) {
        return withId(id, name, DEFAULT_DESCRIPTION, DEFAULT_PROFILE_URL, isPublic);
    }

    /**
     * 모든 파라미터를 지정하여 Family를 생성합니다.
     */
    public static Family withId(Long id, String name, String description, String profileUrl, Boolean isPublic) {
        return Family.withId(id, name, description, profileUrl, isPublic,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now());
    }
}