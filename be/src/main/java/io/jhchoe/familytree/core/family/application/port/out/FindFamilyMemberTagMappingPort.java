package io.jhchoe.familytree.core.family.application.port.out;

/**
 * 태그-멤버 매핑 조회를 위한 아웃바운드 포트입니다.
 */
public interface FindFamilyMemberTagMappingPort {

    /**
     * 태그에 할당된 멤버 수를 조회합니다.
     *
     * @param tagId 태그 ID
     * @return 멤버 수
     */
    int countByTagId(Long tagId);
}
