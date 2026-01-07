package io.jhchoe.familytree.core.family.application.port.in;

import java.util.Collections;
import java.util.List;

/**
 * 멤버 태그 할당 명령 객체입니다.
 * <p>
 * 전체 교체 방식으로 동작합니다: 빈 tagIds 전달 시 모든 태그가 해제됩니다.
 *
 * @param familyId 가족 ID (필수)
 * @param memberId 멤버 ID (필수)
 * @param tagIds   할당할 태그 ID 목록 (빈 목록이면 모든 태그 해제, 최대 10개)
 */
public record ModifyMemberTagsCommand(Long familyId, Long memberId, List<Long> tagIds) {

    private static final int MAX_TAG_COUNT = 10;

    /**
     * 멤버 태그 할당 명령 객체를 생성합니다.
     *
     * @param familyId 가족 ID
     * @param memberId 멤버 ID
     * @param tagIds   할당할 태그 ID 목록
     * @throws IllegalArgumentException 유효성 검증 실패 시
     */
    public ModifyMemberTagsCommand {
        validateFamilyId(familyId);
        validateMemberId(memberId);
        tagIds = normalizeTagIds(tagIds);
        validateTagIdsCount(tagIds);
    }

    private static void validateFamilyId(final Long familyId) {
        if (familyId == null) {
            throw new IllegalArgumentException("가족 ID는 필수입니다.");
        }
        if (familyId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 가족 ID입니다.");
        }
    }

    private static void validateMemberId(final Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("멤버 ID는 필수입니다.");
        }
        if (memberId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 멤버 ID입니다.");
        }
    }

    private static List<Long> normalizeTagIds(final List<Long> tagIds) {
        if (tagIds == null) {
            return Collections.emptyList();
        }
        return List.copyOf(tagIds);
    }

    private static void validateTagIdsCount(final List<Long> tagIds) {
        if (tagIds.size() > MAX_TAG_COUNT) {
            throw new IllegalArgumentException("한 멤버에 최대 10개의 태그만 할당할 수 있습니다.");
        }
    }
}
