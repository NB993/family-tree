package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import java.util.List;
import java.util.Optional;

/**
 * 태그 조회를 위한 아웃바운드 포트입니다.
 */
public interface FindFamilyMemberTagPort {

    /**
     * ID로 태그를 조회합니다.
     *
     * @param id 태그 ID
     * @return 태그 도메인 객체 (없으면 빈 Optional)
     */
    Optional<FamilyMemberTag> findById(Long id);

    /**
     * Family의 모든 태그를 조회합니다.
     *
     * @param familyId Family ID
     * @return 태그 목록
     */
    List<FamilyMemberTag> findAllByFamilyId(Long familyId);

    /**
     * Family 내에서 동일한 이름의 태그가 존재하는지 확인합니다.
     *
     * @param familyId Family ID
     * @param name     태그 이름
     * @return 존재하면 해당 태그, 없으면 빈 Optional
     */
    Optional<FamilyMemberTag> findByFamilyIdAndName(Long familyId, String name);

    /**
     * Family의 태그 수를 조회합니다.
     *
     * @param familyId Family ID
     * @return 태그 수
     */
    int countByFamilyId(Long familyId);
}
