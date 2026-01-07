package io.jhchoe.familytree.core.family.application.port.in;

/**
 * Family의 태그 목록 조회 쿼리 객체입니다.
 *
 * @param familyId 가족 ID (필수)
 */
public record FindFamilyMemberTagsQuery(Long familyId) {

    /**
     * 태그 목록 조회 쿼리 객체를 생성합니다.
     *
     * @throws IllegalArgumentException familyId가 유효하지 않은 경우
     */
    public FindFamilyMemberTagsQuery {
        if (familyId == null) {
            throw new IllegalArgumentException("가족 ID는 필수입니다.");
        }
        if (familyId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 가족 ID입니다.");
        }
    }
}
