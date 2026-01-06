package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * 가족 구성원 수동 등록 명령 객체입니다.
 */
@Getter
public class SaveFamilyMemberCommand {

    private final Long familyId;
    private final String name;
    private final LocalDateTime birthday;
    private final FamilyMemberRelationshipType relationshipType;
    private final String customRelationship;

    /**
     * 가족 구성원 수동 등록 명령 객체를 생성합니다.
     *
     * @param familyId           가족 ID (필수)
     * @param name               구성원 이름 (필수)
     * @param birthday           생년월일 (선택)
     * @param relationshipType   관계 유형 (선택)
     * @param customRelationship 사용자 정의 관계명 (relationshipType이 CUSTOM인 경우 필수)
     */
    public SaveFamilyMemberCommand(
        Long familyId,
        String name,
        LocalDateTime birthday,
        FamilyMemberRelationshipType relationshipType,
        String customRelationship
    ) {
        validateFamilyId(familyId);
        validateName(name);
        validateRelationshipType(relationshipType, customRelationship);

        this.familyId = familyId;
        this.name = name;
        this.birthday = birthday;
        this.relationshipType = relationshipType;
        this.customRelationship = customRelationship;
    }

    private void validateFamilyId(Long familyId) {
        if (familyId == null) {
            throw new IllegalArgumentException("가족 ID는 필수입니다.");
        }
        if (familyId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 가족 ID입니다.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
    }

    private void validateRelationshipType(
        FamilyMemberRelationshipType relationshipType,
        String customRelationship
    ) {
        if (relationshipType == FamilyMemberRelationshipType.CUSTOM) {
            if (customRelationship == null || customRelationship.isBlank()) {
                throw new IllegalArgumentException("CUSTOM 관계 유형을 선택한 경우 사용자 정의 관계명은 필수입니다.");
            }
            if (customRelationship.length() > 50) {
                throw new IllegalArgumentException("사용자 정의 관계명은 50자 이내로 작성해주세요.");
            }
        }
    }

    /**
     * 관계 표시명을 반환합니다.
     * CUSTOM인 경우 customRelationship을, 그 외에는 relationshipType의 displayName을 반환합니다.
     *
     * @return 관계 표시명
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
}
