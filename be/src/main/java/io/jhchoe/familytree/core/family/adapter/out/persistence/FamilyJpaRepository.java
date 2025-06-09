package io.jhchoe.familytree.core.family.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyJpaRepository extends JpaRepository<FamilyJpaEntity, Long> {

    List<FamilyJpaEntity> findByNameContaining(String name);
    
    /**
     * 가족명으로 Family를 조회합니다.
     * 소프트 딜리트된 Family는 조회 결과에서 제외됩니다.
     * 
     * @param name 가족명 (정확히 일치하는 이름)
     * @return Family 조회 결과, 존재하지 않거나 삭제된 경우 빈 Optional 반환
     */
    Optional<FamilyJpaEntity> findByNameAndDeletedFalse(String name);
}
