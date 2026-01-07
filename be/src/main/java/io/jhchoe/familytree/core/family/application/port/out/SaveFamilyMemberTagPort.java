package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;

/**
 * 태그 저장을 위한 아웃바운드 포트입니다.
 */
public interface SaveFamilyMemberTagPort {

    /**
     * 태그를 저장합니다.
     *
     * @param tag 저장할 태그 도메인 객체
     * @return 저장된 태그의 ID
     */
    Long save(FamilyMemberTag tag);
}
