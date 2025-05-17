package io.jhchoe.familytree.core.family.adapter.out.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyJpaRepository extends JpaRepository<FamilyJpaEntity, Long> {

    List<FamilyJpaEntity> findByNameContaining(String name);
}
