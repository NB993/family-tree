package io.jhchoe.familytree.core.relationship.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

/**
 * 가족 구성원 간의 관계를 나타내는 도메인 객체입니다.
 * 한 사용자가 다른 가족 구성원과의 관계를 개인적으로 정의할 수 있습니다.
 */
@Getter
public class FamilyMemberRelationship {

    private final Long id;
    private final Long familyId;
    private final Long fromMemberId;
    private final Long toMemberId;
    private final FamilyMemberRelationshipType relationshipType;
    private final String customRelationship;
    private final String description;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final Long modifiedBy;
    private final LocalDateTime modifiedAt;

    /**
     * FamilyRelationship 객체를 생성하는 생성자입니다.
     * 
     * @param id                 고유 식별자
     * @param familyId           가족 ID
     * @param fromMemberId       관계를 정의하는 구성원 ID
     * @param toMemberId         관계가 정의되는 대상 구성원 ID
     * @param relationshipType   관계 유형
     * @param customRelationship 사용자 정의 관계명 (relationshipType이 CUSTOM인 경우 사용)
     * @param description        부가 설명
     * @param createdBy          생성자 ID
     * @param createdAt          생성 시간
     * @param modifiedBy         수정자 ID
     * @param modifiedAt         수정 시간
     */
    private FamilyMemberRelationship(
        Long id, 
        Long familyId,
        Long fromMemberId,
        Long toMemberId,
        FamilyMemberRelationshipType relationshipType,
        String customRelationship,
        String description,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.familyId = familyId;
        this.fromMemberId = fromMemberId;
        this.toMemberId = toMemberId;
        this.relationshipType = relationshipType;
        this.customRelationship = customRelationship;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }

    /**
     * 새로운 가족 관계를 생성합니다.
     * 
     * @param familyId           가족 ID
     * @param fromMemberId       관계를 정의하는 구성원 ID
     * @param toMemberId         관계가 정의되는 대상 구성원 ID
     * @param relationshipType   관계 유형
     * @param customRelationship 사용자 정의 관계명 (relationshipType이 CUSTOM인 경우 필수)
     * @param description        부가 설명 (선택)
     * @return 새로운 FamilyRelationship 인스턴스
     */
    public static FamilyMemberRelationship createRelationship(
        Long familyId,
        Long fromMemberId,
        Long toMemberId,
        FamilyMemberRelationshipType relationshipType,
        String customRelationship,
        String description
    ) {
        validateRelationshipCreation(familyId, fromMemberId, toMemberId, relationshipType, customRelationship);
        
        return new FamilyMemberRelationship(
            null,
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description,
            null,
            null,
            null,
            null
        );
    }

    /**
     * 기존 가족 관계 객체를 불러옵니다.
     * 
     * @param id                 고유 식별자
     * @param familyId           가족 ID
     * @param fromMemberId       관계를 정의하는 구성원 ID
     * @param toMemberId         관계가 정의되는 대상 구성원 ID
     * @param relationshipType   관계 유형
     * @param customRelationship 사용자 정의 관계명
     * @param description        부가 설명
     * @param createdBy          생성자 ID
     * @param createdAt          생성 시간
     * @param modifiedBy         수정자 ID
     * @param modifiedAt         수정 시간
     * @return 기존 데이터로 구성된 FamilyRelationship 인스턴스
     */
    public static FamilyMemberRelationship withId(
        Long id,
        Long familyId,
        Long fromMemberId,
        Long toMemberId,
        FamilyMemberRelationshipType relationshipType,
        String customRelationship,
        String description,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        Objects.requireNonNull(id, "id must not be null");
        validateRelationshipCreation(familyId, fromMemberId, toMemberId, relationshipType, customRelationship);
        
        return new FamilyMemberRelationship(
            id,
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description,
            createdBy,
            createdAt,
            modifiedBy,
            modifiedAt
        );
    }

    /**
     * 가족 관계 타입, 사용자 정의 관계 및 설명을 업데이트합니다.
     * 
     * @param relationshipType   새 관계 유형
     * @param customRelationship 새 사용자 정의 관계명
     * @param description        새 설명
     * @return 업데이트된 FamilyRelationship 객체
     */
    public FamilyMemberRelationship updateRelationship(
        FamilyMemberRelationshipType relationshipType,
        String customRelationship,
        String description
    ) {
        validateRelationshipType(relationshipType, customRelationship);
        
        return new FamilyMemberRelationship(
            this.id,
            this.familyId,
            this.fromMemberId,
            this.toMemberId,
            relationshipType,
            customRelationship,
            description,
            this.createdBy,
            this.createdAt,
            this.modifiedBy,
            this.modifiedAt
        );
    }
    
    /**
     * 관계 생성 시 필요한 유효성 검증을 수행합니다.
     */
    private static void validateRelationshipCreation(
        Long familyId,
        Long fromMemberId,
        Long toMemberId,
        FamilyMemberRelationshipType relationshipType,
        String customRelationship
    ) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(fromMemberId, "fromMemberId must not be null");
        Objects.requireNonNull(toMemberId, "toMemberId must not be null");
        
        if (fromMemberId.equals(toMemberId)) {
            throw new IllegalArgumentException("fromMemberId와 toMemberId는 서로 달라야 합니다");
        }
        
        validateRelationshipType(relationshipType, customRelationship);
    }
    
    /**
     * 관계 타입의 유효성을 검증합니다.
     */
    private static void validateRelationshipType(
        FamilyMemberRelationshipType relationshipType,
        String customRelationship
    ) {
        Objects.requireNonNull(relationshipType, "relationshipType must not be null");
        
        if (relationshipType == FamilyMemberRelationshipType.CUSTOM) {
            if (customRelationship == null || customRelationship.isBlank()) {
                throw new IllegalArgumentException("CUSTOM 관계 타입을 선택한 경우 customRelationship은 필수입니다");
            }
            if (customRelationship.length() > 50) {
                throw new IllegalArgumentException("사용자 정의 관계명은 50자 이내로 작성해주세요");
            }
        }
    }
}
