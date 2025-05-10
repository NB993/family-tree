package io.jhchoe.familytree.core.relationship.application.port.in;

import io.jhchoe.familytree.core.relationship.domain.FamilyMemberRelationshipType;
import lombok.Getter;

/**
 * 가족 관계 정의 명령 객체입니다.
 */
@Getter
public class SaveFamilyMemberRelationshipCommand {

    private final Long familyId;
    private final Long fromMemberId;
    private final Long toMemberId;
    private final FamilyMemberRelationshipType relationshipType;
    private final String customRelationship;
    private final String description;

    /**
     * 가족 관계 정의 명령 객체를 생성합니다.
     *
     * @param familyId           가족 ID
     * @param fromMemberId       관계를 정의하는 사람의 ID
     * @param toMemberId         관계가 정의되는 대상의 ID
     * @param relationshipType   관계 유형
     * @param customRelationship 사용자 정의 관계명 (relationshipType이 CUSTOM인 경우 필수)
     * @param description        부가 설명 (선택)
     */
    public SaveFamilyMemberRelationshipCommand(
        Long familyId,
        Long fromMemberId,
        Long toMemberId,
        FamilyMemberRelationshipType relationshipType,
        String customRelationship,
        String description
    ) {
        validateFamilyId(familyId);
        validateMemberIds(fromMemberId, toMemberId);
        validateRelationshipType(relationshipType, customRelationship);
        
        this.familyId = familyId;
        this.fromMemberId = fromMemberId;
        this.toMemberId = toMemberId;
        this.relationshipType = relationshipType;
        this.customRelationship = customRelationship;
        this.description = description;
    }

    private void validateFamilyId(Long familyId) {
        if (familyId == null) {
            throw new IllegalArgumentException("가족 ID는 필수입니다.");
        }
        if (familyId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 가족 ID입니다.");
        }
    }

    private void validateMemberIds(Long fromMemberId, Long toMemberId) {
        if (fromMemberId == null) {
            throw new IllegalArgumentException("관계를 정의하는 구성원 ID는 필수입니다.");
        }
        if (fromMemberId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 구성원 ID입니다.");
        }
        if (toMemberId == null) {
            throw new IllegalArgumentException("관계를 정의할 대상 구성원 ID는 필수입니다.");
        }
        if (toMemberId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 대상 구성원 ID입니다.");
        }
        if (fromMemberId.equals(toMemberId)) {
            throw new IllegalArgumentException("관계를 정의하는 사람과 대상은 서로 달라야 합니다.");
        }
    }

    private void validateRelationshipType(FamilyMemberRelationshipType relationshipType, String customRelationship) {
        if (relationshipType == null) {
            throw new IllegalArgumentException("관계 유형은 필수입니다.");
        }
        
        if (relationshipType == FamilyMemberRelationshipType.CUSTOM) {
            if (customRelationship == null || customRelationship.isBlank()) {
                throw new IllegalArgumentException("CUSTOM 관계 유형을 선택한 경우 사용자 정의 관계명은 필수입니다.");
            }
            if (customRelationship.length() > 50) {
                throw new IllegalArgumentException("사용자 정의 관계명은 50자 이내로 작성해주세요.");
            }
        }
    }
}
