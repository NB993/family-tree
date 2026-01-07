package io.jhchoe.familytree.core.family.application.port.out;

/**
 * 태그 매핑 삭제를 위한 아웃바운드 포트입니다.
 */
public interface DeleteFamilyMemberTagMappingPort {

    /**
     * 멤버에 할당된 모든 태그 매핑을 삭제합니다.
     *
     * @param memberId 멤버 ID
     */
    void deleteAllByMemberId(Long memberId);
}
