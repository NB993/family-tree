package io.jhchoe.familytree.core.family.application.port.in;

/**
 * 태그 삭제 명령 객체입니다.
 *
 * @param familyId 가족 ID (필수)
 * @param tagId    태그 ID (필수)
 */
public record DeleteFamilyMemberTagCommand(Long familyId, Long tagId) {

    /**
     * 태그 삭제 명령 객체를 생성합니다.
     *
     * @throws IllegalArgumentException 유효성 검증 실패 시
     */
    public DeleteFamilyMemberTagCommand {
        if (familyId == null) {
            throw new IllegalArgumentException("가족 ID는 필수입니다.");
        }
        if (familyId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 가족 ID입니다.");
        }
        if (tagId == null) {
            throw new IllegalArgumentException("태그 ID는 필수입니다.");
        }
        if (tagId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 태그 ID입니다.");
        }
    }
}
