package io.jhchoe.familytree.core.family.domain;

import java.time.LocalDateTime;
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
    private final Long userId;
    private final String name;
    private final String profileUrl;
    private final LocalDateTime birthday;
    private final String nationality;
    private final MemberStatus status;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final Long modifiedBy;
    private final LocalDateTime modifiedAt;

    /**
     * FamilyMember 객체 생성자.
     *
     * @param id          고유 ID
     * @param familyId    Family ID
     * @param userId      사용자 ID
     * @param name        이름
     * @param profileUrl  프로필 URL
     * @param birthday    생일
     * @param nationality 국적
     * @param status      멤버 상태
     * @param createdBy   생성한 사용자 ID
     * @param createdAt   생성 일시
     * @param modifiedBy  수정한 사용자 ID
     * @param modifiedAt  수정 일시
     */
    private FamilyMember(
        Long id,
        Long familyId,
        Long userId,
        String name,
        String profileUrl,
        LocalDateTime birthday,
        String nationality,
        MemberStatus status,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.familyId = familyId;
        this.userId = userId;
        this.name = name;
        this.profileUrl = profileUrl;
        this.birthday = birthday;
        this.nationality = nationality;
        this.status = status;
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
        return new FamilyMember(null, familyId, userId, name, profileUrl, birthday, nationality, MemberStatus.JOIN, null, null, null, null);
    }

    /**
     * 고유 ID를 가지고 있는 FamilyMember 객체를 생성합니다.
     *
     * @param id          고유 ID
     * @param familyId    Family ID
     * @param userId      사용자 ID
     * @param name        이름
     * @param profileUrl  프로필 URL
     * @param birthday    생일
     * @param nationality 국적
     * @param status      멤버 상태
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
        String name,
        String profileUrl,
        LocalDateTime birthday,
        String nationality,
        MemberStatus status,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        return new FamilyMember(id, familyId, userId, name, profileUrl, birthday, nationality, status, createdBy, createdAt, modifiedBy, modifiedAt);
    }
}
