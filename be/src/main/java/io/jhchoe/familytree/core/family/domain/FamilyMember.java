package io.jhchoe.familytree.core.family.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;


/**
 * FamilyMember 클래스는 Family 구성원의 정보를 저장하는 도메인 클래스입니다.
 * <p>
 * 이 클래스는 구성원의 고유 ID, 이름, 프로필 URL, 생일, 국적 등 주요 정보를 포함하며,
 * 생성자 및 팩토리 메서드를 통해 FamilyMember 객체를 생성할 수 있습니다.
 */
@Getter
public class FamilyMember {

    private final Long id;
    private final Long familyId;
    private final Long userId; // nullable - 비회원도 가능
    private final String kakaoId; // 카카오 ID
    private final String name;
    private final String relationship;
    private final String profileUrl;
    private final LocalDateTime birthday;
    private final String nationality;
    private final FamilyMemberStatus status;
    private final FamilyMemberRole role; // 추가된 필드
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final Long modifiedBy;
    private final LocalDateTime modifiedAt;

    /**
     * FamilyMember 객체 생성자.
     *
     * @param id          고유 ID
     * @param familyId    Family ID
     * @param userId      사용자 ID (nullable - 비회원인 경우 null)
     * @param kakaoId     카카오 ID (nullable - 카카오 인증을 하지 않은 경우 null)
     * @param name        이름
     * @param relationship 나와의 관계
     * @param profileUrl  프로필 URL
     * @param birthday    생일
     * @param nationality 국적
     * @param status      멤버 상태
     * @param role        멤버 역할
     * @param createdBy   생성한 사용자 ID
     * @param createdAt   생성 일시
     * @param modifiedBy  수정한 사용자 ID
     * @param modifiedAt  수정 일시
     */
    private FamilyMember(
        Long id,
        Long familyId,
        Long userId,
        String kakaoId,
        String name,
        String relationship,
        String profileUrl,
        LocalDateTime birthday,
        String nationality,
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
        this.kakaoId = kakaoId;
        this.name = name;
        this.relationship = relationship;
        this.profileUrl = profileUrl;
        this.birthday = birthday;
        this.nationality = nationality;
        this.status = status;
        this.role = role;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }

    
    /**
     * Family에 가입할 FamilyMember 객체를 생성합니다.
     *
     * @param familyId    가족 ID
     * @param userId      사용자 ID
     * @param name        이름
     * @param profileUrl  프로필 URL
     * @param birthday    생일
     * @param nationality 국적
     * @return 새로운 FamilyMember 인스턴스 (ID 및 audit 필드 없음)
     */
    public static FamilyMember newMember(
        Long familyId,
        Long userId,
        String name,
        String profileUrl,
        LocalDateTime birthday,
        String nationality
    ) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");

        return new FamilyMember(null, familyId, userId, null, name, null, profileUrl, birthday, nationality,
                              FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER, 
                              null, null, null, null);
    }

    /**
     * 고유 ID를 가지고 있는 기존 FamilyMember 객체를 생성합니다.
     *
     * @param id          고유 ID
     * @param familyId    Family ID
     * @param userId      사용자 ID (nullable - 비회원인 경우 null)
     * @param kakaoId     카카오 ID (nullable)
     * @param name        이름
     * @param profileUrl  프로필 URL
     * @param birthday    생일
     * @param nationality 국적
     * @param status      멤버 상태
     * @param role        멤버 역할
     * @param createdBy   생성한 사용자 ID
     * @param createdAt   생성 일시
     * @param modifiedBy  수정한 사용자 ID
     * @param modifiedAt  수정 일시
     * @return 새로운 FamilyMember 인스턴스 (ID 및 audit 필드 포함)
     */
    public static FamilyMember withId(
        Long id,
        Long familyId,
        Long userId,
        String kakaoId,
        String name,
        String relationship,
        String profileUrl,
        LocalDateTime birthday,
        String nationality,
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

        return new FamilyMember(id, familyId, userId, kakaoId, name, relationship, profileUrl, birthday, nationality,
                              status, role, createdBy, createdAt, modifiedBy, modifiedAt);
    }

    /**
     * 고유 ID를 가지고 있는 기존 FamilyMember 객체를 생성합니다.
     *
     * @param id          고유 ID
     * @param familyId    Family ID
     * @param userId      사용자 ID (nullable - 비회원인 경우 null)
     * @param kakaoId     카카오 ID (nullable)
     * @param name        이름
     * @param profileUrl  프로필 URL
     * @param birthday    생일
     * @param nationality 국적
     * @param status      멤버 상태
     * @param role        멤버 역할
     * @param createdBy   생성한 사용자 ID
     * @param createdAt   생성 일시
     * @param modifiedBy  수정한 사용자 ID
     * @param modifiedAt  수정 일시
     * @return 새로운 FamilyMember 인스턴스 (ID 및 audit 필드 포함)
     */
    public static FamilyMember withIdKakao(
        Long id,
        Long familyId,
        Long userId,
        String kakaoId,
        String name,
        String relationship,
        String profileUrl,
        LocalDateTime birthday,
        String nationality,
        FamilyMemberStatus status,
        FamilyMemberRole role,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(kakaoId, "kakaoId must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(role, "role must not be null");

        return new FamilyMember(id, familyId, userId, kakaoId, name, relationship, profileUrl, birthday, nationality,
            status, role, createdBy, createdAt, modifiedBy, modifiedAt);
    }
    
    /**
     * Family 생성자(OWNER)를 생성합니다.
     *
     * @param familyId    가족 ID
     * @param userId      사용자 ID
     * @param kakaoId     카카오 ID
     * @param name        이름
     * @param profileUrl  프로필 URL
     * @param birthday    생일
     * @param nationality 국적
     * @return 새로운 FamilyMember 인스턴스 (OWNER 역할, ACTIVE 상태)
     */
    public static FamilyMember newOwner(
        Long familyId,
        Long userId,
        String kakaoId,
        String name,
        String profileUrl,
        LocalDateTime birthday,
        String nationality
    ) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");

        return new FamilyMember(
            null, familyId, userId, kakaoId, name, null, profileUrl, birthday, nationality,
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            null, null, null, null
        );
    }

    /**
     * 역할을 지정하여 신규 Family 구성원을 생성합니다.
     *
     * @param familyId    Family ID
     * @param userId      사용자 ID
     * @param name        이름
     * @param profileUrl  프로필 URL
     * @param birthday    생일
     * @param nationality 국적
     * @param role        멤버 역할
     * @return 새로운 FamilyMember 인스턴스 (ID 및 audit 필드 없음)
     */
    public static FamilyMember withRole(
        Long familyId,
        Long userId,
        String name,
        String profileUrl,
        LocalDateTime birthday,
        String nationality,
        FamilyMemberRole role
    ) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(role, "role must not be null");

        return new FamilyMember(
            null, familyId, userId, null, name, null, profileUrl, birthday, nationality,
            FamilyMemberStatus.ACTIVE, role, null, null, null, null
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
            this.id, this.familyId, this.userId, this.kakaoId, this.name, this.relationship, this.profileUrl,
            this.birthday, this.nationality, this.status, newRole, 
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
            this.id, this.familyId, this.userId, this.kakaoId, this.name, this.relationship, this.profileUrl,
            this.birthday, this.nationality, newStatus, this.role, 
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
     * 카카오 OAuth로 인증한 FamilyMember를 생성합니다.
     * User가 있으면 userId를 함께 저장하고, 없으면 kakaoId만 저장합니다.
     *
     * @param familyId    Family ID
     * @param userId      사용자 ID (nullable - User가 없으면 null)
     * @param kakaoId     카카오 ID
     * @param name        이름
     * @param profileUrl  프로필 URL
     * @param birthday    생일
     * @return 새로운 FamilyMember 인스턴스
     */
    public static FamilyMember newKakaoMember(
        Long familyId,
        Long userId,
        String kakaoId,
        String name,
        String profileUrl,
        LocalDateTime birthday
    ) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(kakaoId, "kakaoId must not be null");
        Objects.requireNonNull(name, "name must not be null");

        return new FamilyMember(
            null, familyId, userId, kakaoId, name, null, profileUrl, birthday, null,
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            null, null, null, null
        );
    }
    
    /**
     * 구성원이 회원인지 확인합니다.
     *
     * @return userId가 있으면 true (회원), 없으면 false (비회원)
     */
    public boolean isRegisteredUser() {
        return this.userId != null;
    }
    
    /**
     * 구성원이 카카오 인증 사용자인지 확인합니다.
     *
     * @return kakaoId가 있으면 true, 없으면 false
     */
    public boolean isKakaoAuthenticated() {
        return this.kakaoId != null;
    }
}
