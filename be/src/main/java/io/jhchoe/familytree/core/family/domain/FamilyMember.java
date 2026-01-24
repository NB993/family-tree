package io.jhchoe.familytree.core.family.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;


/**
 * FamilyMember 클래스는 Family 구성원의 정보를 저장하는 도메인 클래스입니다.
 * <p>
 * 이 클래스는 구성원의 고유 ID, 이름, 프로필 URL, 생일 등 주요 정보를 포함하며,
 * 생성자 및 팩토리 메서드를 통해 FamilyMember 객체를 생성할 수 있습니다.
 * <p>
 * name, profileUrl, birthday는 Family별로 독립적으로 관리됩니다.
 * 초대 수락 시 User 정보를 복사하며, 이후 Family 주인이 수정할 수 있습니다.
 */
@Getter
public class FamilyMember {

    private final Long id;
    private final Long familyId;
    private final Long userId; // nullable - 수동 등록(애완동물, 아이 등)인 경우 null
    private final String name;
    private final FamilyMemberRelationshipType relationshipType;
    private final String customRelationship;
    private final String profileUrl;
    private final LocalDateTime birthday;
    private final BirthdayType birthdayType;
    private final FamilyMemberStatus status;
    private final FamilyMemberRole role;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final Long modifiedBy;
    private final LocalDateTime modifiedAt;

    /**
     * FamilyMember 객체 생성자.
     *
     * @param id                 고유 ID
     * @param familyId           Family ID
     * @param userId             사용자 ID (nullable - 수동 등록인 경우 null)
     * @param name               이름
     * @param relationshipType   관계 타입 (nullable)
     * @param customRelationship CUSTOM일 때 사용자 입력값 (nullable)
     * @param profileUrl         프로필 URL
     * @param birthday           생일
     * @param birthdayType       생일 유형 (양력/음력)
     * @param status             멤버 상태
     * @param role               멤버 역할
     * @param createdBy          생성한 사용자 ID
     * @param createdAt          생성 일시
     * @param modifiedBy         수정한 사용자 ID
     * @param modifiedAt         수정 일시
     */
    private FamilyMember(
        Long id,
        Long familyId,
        Long userId,
        String name,
        FamilyMemberRelationshipType relationshipType,
        String customRelationship,
        String profileUrl,
        LocalDateTime birthday,
        BirthdayType birthdayType,
        FamilyMemberStatus status,
        FamilyMemberRole role,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.familyId = familyId;
        this.userId = userId;
        this.name = name;
        this.relationshipType = relationshipType;
        this.customRelationship = customRelationship;
        this.profileUrl = profileUrl;
        this.birthday = birthday;
        this.birthdayType = birthdayType;
        this.status = status;
        this.role = role;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }


    /**
     * Family에 가입할 FamilyMember 객체를 생성합니다.
     * 회원 가입 시 사용합니다. User의 정보를 복사하여 FamilyMember에 저장합니다.
     *
     * @param familyId     가족 ID
     * @param userId       사용자 ID
     * @param name         이름 (User에서 복사)
     * @param profileUrl   프로필 URL (User에서 복사)
     * @param birthday     생일 (User에서 복사)
     * @param birthdayType 생일 유형 (User에서 복사)
     * @return 새로운 FamilyMember 인스턴스 (ID 및 audit 필드 없음)
     */
    public static FamilyMember newMember(
        Long familyId,
        Long userId,
        String name,
        String profileUrl,
        LocalDateTime birthday,
        BirthdayType birthdayType
    ) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(name, "name must not be null");

        return new FamilyMember(null, familyId, userId, name, null, null, profileUrl, birthday,
                              birthdayType, FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
                              null, null, null, null);
    }

    /**
     * 고유 ID를 가지고 있는 기존 FamilyMember 객체를 생성합니다.
     * DB에서 조회한 데이터를 도메인 객체로 복원할 때 사용합니다.
     *
     * @param id                 고유 ID
     * @param familyId           Family ID
     * @param userId             사용자 ID (nullable - 수동 등록인 경우 null)
     * @param name               이름
     * @param relationshipType   관계 타입 (nullable)
     * @param customRelationship CUSTOM일 때 사용자 입력값 (nullable)
     * @param profileUrl         프로필 URL
     * @param birthday           생일
     * @param birthdayType       생일 유형 (양력/음력)
     * @param status             멤버 상태
     * @param role               멤버 역할
     * @param createdBy          생성한 사용자 ID
     * @param createdAt          생성 일시
     * @param modifiedBy         수정한 사용자 ID
     * @param modifiedAt         수정 일시
     * @return 새로운 FamilyMember 인스턴스 (ID 및 audit 필드 포함)
     */
    public static FamilyMember withId(
        Long id,
        Long familyId,
        Long userId,
        String name,
        FamilyMemberRelationshipType relationshipType,
        String customRelationship,
        String profileUrl,
        LocalDateTime birthday,
        BirthdayType birthdayType,
        FamilyMemberStatus status,
        FamilyMemberRole role,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(role, "role must not be null");

        return new FamilyMember(id, familyId, userId, name, relationshipType, customRelationship,
                              profileUrl, birthday, birthdayType, status, role, createdBy, createdAt, modifiedBy, modifiedAt);
    }

    /**
     * Family 생성자(OWNER)를 생성합니다.
     *
     * @param familyId     가족 ID
     * @param userId       사용자 ID
     * @param name         이름 (User에서 복사)
     * @param profileUrl   프로필 URL (User에서 복사)
     * @param birthday     생일 (User에서 복사)
     * @param birthdayType 생일 유형 (User에서 복사)
     * @return 새로운 FamilyMember 인스턴스 (OWNER 역할, ACTIVE 상태)
     */
    public static FamilyMember newOwner(
        Long familyId,
        Long userId,
        String name,
        String profileUrl,
        LocalDateTime birthday,
        BirthdayType birthdayType
    ) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(name, "name must not be null");

        return new FamilyMember(
            null, familyId, userId, name, null, null, profileUrl, birthday,
            birthdayType, FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            null, null, null, null
        );
    }

    /**
     * 역할을 지정하여 신규 Family 구성원을 생성합니다.
     *
     * @param familyId     Family ID
     * @param userId       사용자 ID
     * @param name         이름 (User에서 복사)
     * @param profileUrl   프로필 URL (User에서 복사)
     * @param birthday     생일 (User에서 복사)
     * @param birthdayType 생일 유형 (User에서 복사)
     * @param role         멤버 역할
     * @return 새로운 FamilyMember 인스턴스 (ID 및 audit 필드 없음)
     */
    public static FamilyMember withRole(
        Long familyId,
        Long userId,
        String name,
        String profileUrl,
        LocalDateTime birthday,
        BirthdayType birthdayType,
        FamilyMemberRole role
    ) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(role, "role must not be null");

        return new FamilyMember(
            null, familyId, userId, name, null, null, profileUrl, birthday,
            birthdayType, FamilyMemberStatus.ACTIVE, role, null, null, null, null
        );
    }

    /**
     * FamilyMember의 역할을 변경합니다.
     *
     * @param newRole 변경할 역할
     * @return 역할이 변경된 새로운 FamilyMember 객체
     * @throws IllegalStateException OWNER 역할은 변경할 수 없는 경우
     */
    public FamilyMember updateRole(FamilyMemberRole newRole) {
        Objects.requireNonNull(newRole, "role must not be null");

        // OWNER 역할은 변경할 수 없음
        if (this.role == FamilyMemberRole.OWNER) {
            throw new IllegalStateException("Cannot change role of the Family OWNER");
        }

        return new FamilyMember(
            this.id, this.familyId, this.userId, this.name, this.relationshipType, this.customRelationship,
            this.profileUrl, this.birthday, this.birthdayType, this.status, newRole,
            this.createdBy, this.createdAt, this.modifiedBy, this.modifiedAt
        );
    }

    /**
     * FamilyMember의 상태를 변경합니다.
     *
     * @param newStatus 변경할 상태
     * @return 상태가 변경된 새로운 FamilyMember 객체
     * @throws IllegalStateException OWNER 상태는 변경할 수 없는 경우
     */
    public FamilyMember updateStatus(FamilyMemberStatus newStatus) {
        Objects.requireNonNull(newStatus, "status must not be null");

        // OWNER 상태는 변경할 수 없음
        if (this.role == FamilyMemberRole.OWNER) {
            throw new IllegalStateException("Cannot change status of the Family OWNER");
        }

        return new FamilyMember(
            this.id, this.familyId, this.userId, this.name, this.relationshipType, this.customRelationship,
            this.profileUrl, this.birthday, this.birthdayType, newStatus, this.role,
            this.createdBy, this.createdAt, this.modifiedBy, this.modifiedAt
        );
    }

    /**
     * 해당 역할 이상의 권한을 가지고 있는지 확인합니다.
     *
     * @param requiredRole 필요한 최소 역할
     * @return 요구되는 역할 이상의 권한을 가지고 있으면 true, 그렇지 않으면 false
     */
    public boolean hasRoleAtLeast(FamilyMemberRole requiredRole) {
        return this.role.isAtLeast(requiredRole);
    }

    /**
     * 구성원이 활성 상태인지 확인합니다.
     *
     * @return 활성 상태이면 true, 그렇지 않으면 false
     */
    public boolean isActive() {
        return this.status == FamilyMemberStatus.ACTIVE;
    }

    /**
     * 구성원이 회원인지 확인합니다.
     *
     * @return userId가 있으면 true (회원), 없으면 false (수동 등록)
     */
    public boolean isRegisteredUser() {
        return this.userId != null;
    }

    /**
     * 수동으로 등록하는 Family 구성원을 생성합니다.
     * userId 없이 구성원만 등록하는 경우 사용합니다. (애완동물, 아이 등)
     *
     * @param familyId           Family ID
     * @param name               구성원 이름
     * @param relationshipType   관계 타입 (선택)
     * @param customRelationship CUSTOM일 때 사용자 입력값 (선택)
     * @param birthday           생년월일 (선택)
     * @param birthdayType       생일 유형 (선택)
     * @return 새로운 FamilyMember 인스턴스 (userId 없음, ACTIVE 상태, MEMBER 역할)
     */
    public static FamilyMember newManualMember(
        Long familyId,
        String name,
        FamilyMemberRelationshipType relationshipType,
        String customRelationship,
        LocalDateTime birthday,
        BirthdayType birthdayType
    ) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        return new FamilyMember(
            null, familyId, null, name, relationshipType, customRelationship, null, birthday,
            birthdayType, FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            null, null, null, null
        );
    }

    /**
     * 관계를 표시할 문자열을 반환합니다.
     * CUSTOM인 경우 customRelationship을, 그 외에는 relationshipType의 displayName을 반환합니다.
     *
     * @return 관계 표시 문자열 (null인 경우 null 반환)
     */
    public String getRelationshipDisplayName() {
        if (relationshipType == null) {
            return null;
        }
        if (relationshipType == FamilyMemberRelationshipType.CUSTOM) {
            return customRelationship;
        }
        return relationshipType.getDisplayName();
    }

    /**
     * 구성원의 관계 정보를 변경합니다.
     *
     * @param relationshipType   새로운 관계 타입 (null 불허)
     * @param customRelationship CUSTOM 타입일 때 사용자 정의 관계명 (CUSTOM이 아닌 경우 무시됨)
     * @return 관계 정보가 변경된 새로운 FamilyMember 객체
     * @throws NullPointerException     relationshipType이 null인 경우
     * @throws IllegalArgumentException CUSTOM 타입이지만 customRelationship이 없거나 50자를 초과하는 경우
     */
    public FamilyMember updateRelationship(
        FamilyMemberRelationshipType relationshipType,
        String customRelationship
    ) {
        validateRelationshipType(relationshipType, customRelationship);

        return new FamilyMember(
            this.id, this.familyId, this.userId, this.name,
            relationshipType, customRelationship,
            this.profileUrl, this.birthday, this.birthdayType,
            this.status, this.role,
            this.createdBy, this.createdAt, this.modifiedBy, this.modifiedAt
        );
    }

    /**
     * 관계 타입과 커스텀 관계명의 유효성을 검증합니다.
     *
     * @param relationshipType   관계 타입
     * @param customRelationship 커스텀 관계명
     * @throws NullPointerException     relationshipType이 null인 경우
     * @throws IllegalArgumentException CUSTOM 타입이지만 customRelationship이 없거나 50자를 초과하는 경우
     */
    private static void validateRelationshipType(
        FamilyMemberRelationshipType relationshipType,
        String customRelationship
    ) {
        Objects.requireNonNull(relationshipType, "relationshipType must not be null");
        if (relationshipType == FamilyMemberRelationshipType.CUSTOM) {
            if (customRelationship == null || customRelationship.isBlank()) {
                throw new IllegalArgumentException("CUSTOM 관계 타입 선택 시 customRelationship 필수");
            }
            if (customRelationship.length() > 50) {
                throw new IllegalArgumentException("customRelationship은 50자 이내");
            }
        }
    }
}
