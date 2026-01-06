package io.jhchoe.familytree.test.fixture;

import io.jhchoe.familytree.core.family.domain.BirthdayType;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import java.time.LocalDateTime;

/**
 * FamilyMember 테스트 픽스처 생성을 위한 헬퍼 클래스.
 * <p>
 * 기본값이 설정되어 있어 필요한 필드만 오버라이드하여 사용할 수 있습니다.
 * 도메인 클래스 변경 시 이 클래스만 수정하면 모든 테스트에 반영됩니다.
 */
public final class FamilyMemberFixture {

    // 기본값 상수
    private static final Long DEFAULT_FAMILY_ID = 1L;
    private static final Long DEFAULT_USER_ID = 1L;
    private static final String DEFAULT_NAME = "테스트멤버";
    private static final String DEFAULT_PROFILE_URL = "http://example.com/profile.jpg";
    private static final LocalDateTime DEFAULT_BIRTHDAY = LocalDateTime.of(1990, 1, 1, 0, 0);
    private static final BirthdayType DEFAULT_BIRTHDAY_TYPE = BirthdayType.SOLAR;

    private FamilyMemberFixture() {
    }

    // ==================== 신규 생성 (ID 없음) ====================

    /**
     * 기본 MEMBER 역할의 FamilyMember를 생성합니다.
     */
    public static FamilyMember newMember() {
        return FamilyMember.newMember(DEFAULT_FAMILY_ID, DEFAULT_USER_ID, DEFAULT_NAME,
            DEFAULT_PROFILE_URL, DEFAULT_BIRTHDAY, DEFAULT_BIRTHDAY_TYPE);
    }

    /**
     * 지정된 familyId와 userId로 MEMBER 역할의 FamilyMember를 생성합니다.
     */
    public static FamilyMember newMember(Long familyId, Long userId) {
        return FamilyMember.newMember(familyId, userId, DEFAULT_NAME,
            DEFAULT_PROFILE_URL, DEFAULT_BIRTHDAY, DEFAULT_BIRTHDAY_TYPE);
    }

    /**
     * 지정된 familyId, userId, name으로 MEMBER 역할의 FamilyMember를 생성합니다.
     */
    public static FamilyMember newMember(Long familyId, Long userId, String name) {
        return FamilyMember.newMember(familyId, userId, name,
            DEFAULT_PROFILE_URL, DEFAULT_BIRTHDAY, DEFAULT_BIRTHDAY_TYPE);
    }

    /**
     * 기본 OWNER 역할의 FamilyMember를 생성합니다.
     */
    public static FamilyMember newOwner() {
        return FamilyMember.newOwner(DEFAULT_FAMILY_ID, DEFAULT_USER_ID, "테스트오너",
            DEFAULT_PROFILE_URL, DEFAULT_BIRTHDAY, DEFAULT_BIRTHDAY_TYPE);
    }

    /**
     * 지정된 familyId와 userId로 OWNER 역할의 FamilyMember를 생성합니다.
     */
    public static FamilyMember newOwner(Long familyId, Long userId) {
        return FamilyMember.newOwner(familyId, userId, "테스트오너",
            DEFAULT_PROFILE_URL, DEFAULT_BIRTHDAY, DEFAULT_BIRTHDAY_TYPE);
    }

    /**
     * 기본 ADMIN 역할의 FamilyMember를 생성합니다.
     */
    public static FamilyMember newAdmin() {
        return FamilyMember.withRole(DEFAULT_FAMILY_ID, DEFAULT_USER_ID, "테스트어드민",
            DEFAULT_PROFILE_URL, DEFAULT_BIRTHDAY, DEFAULT_BIRTHDAY_TYPE, FamilyMemberRole.ADMIN);
    }

    /**
     * 지정된 familyId와 userId로 ADMIN 역할의 FamilyMember를 생성합니다.
     */
    public static FamilyMember newAdmin(Long familyId, Long userId) {
        return FamilyMember.withRole(familyId, userId, "테스트어드민",
            DEFAULT_PROFILE_URL, DEFAULT_BIRTHDAY, DEFAULT_BIRTHDAY_TYPE, FamilyMemberRole.ADMIN);
    }

    // ==================== ID 포함 (기존 엔티티 복원) ====================

    /**
     * 지정된 ID로 기본 MEMBER 역할의 FamilyMember를 생성합니다.
     */
    public static FamilyMember withId(Long id) {
        return withIdAndRole(id, DEFAULT_FAMILY_ID, DEFAULT_USER_ID, FamilyMemberRole.MEMBER);
    }

    /**
     * 지정된 ID와 역할로 FamilyMember를 생성합니다.
     */
    public static FamilyMember withIdAndRole(Long id, FamilyMemberRole role) {
        return withIdAndRole(id, DEFAULT_FAMILY_ID, DEFAULT_USER_ID, role);
    }

    /**
     * 지정된 ID, familyId, userId로 MEMBER 역할의 FamilyMember를 생성합니다.
     */
    public static FamilyMember withId(Long id, Long familyId, Long userId) {
        return withIdAndRole(id, familyId, userId, FamilyMemberRole.MEMBER);
    }

    /**
     * 지정된 ID, familyId, userId, 역할로 FamilyMember를 생성합니다.
     */
    public static FamilyMember withIdAndRole(Long id, Long familyId, Long userId, FamilyMemberRole role) {
        return FamilyMember.withId(id, familyId, userId, DEFAULT_NAME, null, null, DEFAULT_PROFILE_URL,
            DEFAULT_BIRTHDAY, DEFAULT_BIRTHDAY_TYPE,
            FamilyMemberStatus.ACTIVE, role,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now());
    }

    /**
     * 지정된 ID와 상태로 FamilyMember를 생성합니다.
     */
    public static FamilyMember withIdAndStatus(Long id, FamilyMemberStatus status) {
        return FamilyMember.withId(id, DEFAULT_FAMILY_ID, DEFAULT_USER_ID, DEFAULT_NAME, null, null, DEFAULT_PROFILE_URL,
            DEFAULT_BIRTHDAY, DEFAULT_BIRTHDAY_TYPE,
            status, FamilyMemberRole.MEMBER,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now());
    }

    /**
     * 지정된 ID, 역할, 상태로 FamilyMember를 생성합니다.
     */
    public static FamilyMember withIdRoleAndStatus(Long id, FamilyMemberRole role, FamilyMemberStatus status) {
        return FamilyMember.withId(id, DEFAULT_FAMILY_ID, DEFAULT_USER_ID, DEFAULT_NAME, null, null, DEFAULT_PROFILE_URL,
            DEFAULT_BIRTHDAY, DEFAULT_BIRTHDAY_TYPE,
            status, role,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now());
    }

    /**
     * 지정된 ID, familyId, userId, 역할, 상태로 FamilyMember를 생성합니다.
     */
    public static FamilyMember withIdFull(Long id, Long familyId, Long userId, FamilyMemberRole role, FamilyMemberStatus status) {
        return FamilyMember.withId(id, familyId, userId, DEFAULT_NAME, null, null, DEFAULT_PROFILE_URL,
            DEFAULT_BIRTHDAY, DEFAULT_BIRTHDAY_TYPE,
            status, role,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now());
    }

    /**
     * 지정된 ID와 이름으로 MEMBER 역할의 FamilyMember를 생성합니다.
     */
    public static FamilyMember withIdAndName(Long id, String name) {
        return FamilyMember.withId(id, DEFAULT_FAMILY_ID, DEFAULT_USER_ID, name, null, null, DEFAULT_PROFILE_URL,
            DEFAULT_BIRTHDAY, DEFAULT_BIRTHDAY_TYPE,
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now());
    }

    /**
     * 지정된 ID, familyId, userId, 역할, 이름, 생일로 FamilyMember를 생성합니다 (ID 포함).
     * 생일 기반 정렬 테스트에 유용합니다.
     */
    public static FamilyMember withIdRoleNameAndBirthday(Long id, Long familyId, Long userId, FamilyMemberRole role, String name, LocalDateTime birthday) {
        return FamilyMember.withId(id, familyId, userId, name, null, null, DEFAULT_PROFILE_URL,
            birthday, DEFAULT_BIRTHDAY_TYPE,
            FamilyMemberStatus.ACTIVE, role,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now());
    }

    /**
     * 지정된 ID, familyId, userId, 역할, 상태, 이름, 생일로 FamilyMember를 생성합니다 (ID 포함).
     * 상태와 생일을 함께 지정해야 하는 테스트에 유용합니다.
     */
    public static FamilyMember withIdFullAndBirthday(Long id, Long familyId, Long userId, FamilyMemberRole role, FamilyMemberStatus status, String name, LocalDateTime birthday) {
        return FamilyMember.withId(id, familyId, userId, name, null, null, DEFAULT_PROFILE_URL,
            birthday, DEFAULT_BIRTHDAY_TYPE,
            status, role,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now());
    }
}
