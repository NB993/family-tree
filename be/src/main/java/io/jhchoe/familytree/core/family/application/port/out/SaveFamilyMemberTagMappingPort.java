package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.FamilyMemberTagMapping;
import java.util.List;

/**
 * 태그 매핑 저장을 위한 아웃바운드 포트입니다.
 */
public interface SaveFamilyMemberTagMappingPort {

    /**
     * 태그 매핑 목록을 저장합니다.
     *
     * @param mappings 저장할 매핑 목록
     */
    void saveAll(final List<FamilyMemberTagMapping> mappings);
}
