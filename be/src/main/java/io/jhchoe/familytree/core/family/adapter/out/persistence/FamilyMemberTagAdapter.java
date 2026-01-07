package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.application.port.out.DeleteFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * FamilyMemberTag 아웃바운드 어댑터 클래스입니다.
 * 태그 관련 모든 outbound port를 구현합니다.
 */
@Component
@RequiredArgsConstructor
public class FamilyMemberTagAdapter implements SaveFamilyMemberTagPort, FindFamilyMemberTagPort, DeleteFamilyMemberTagPort {

    private final FamilyMemberTagJpaRepository familyMemberTagJpaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long save(FamilyMemberTag tag) {
        Objects.requireNonNull(tag, "tag must not be null");

        FamilyMemberTagJpaEntity entity = FamilyMemberTagJpaEntity.from(tag);
        FamilyMemberTagJpaEntity savedEntity = familyMemberTagJpaRepository.save(entity);
        return savedEntity.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FamilyMemberTag> findById(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        return familyMemberTagJpaRepository.findById(id)
            .map(FamilyMemberTagJpaEntity::toFamilyMemberTag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FamilyMemberTag> findAllByFamilyId(Long familyId) {
        Objects.requireNonNull(familyId, "familyId must not be null");

        return familyMemberTagJpaRepository.findAllByFamilyId(familyId).stream()
            .map(FamilyMemberTagJpaEntity::toFamilyMemberTag)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FamilyMemberTag> findByFamilyIdAndName(Long familyId, String name) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(name, "name must not be null");

        return familyMemberTagJpaRepository.findByFamilyIdAndName(familyId, name)
            .map(FamilyMemberTagJpaEntity::toFamilyMemberTag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countByFamilyId(Long familyId) {
        Objects.requireNonNull(familyId, "familyId must not be null");

        return familyMemberTagJpaRepository.countByFamilyId(familyId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        familyMemberTagJpaRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FamilyMemberTag> findAllByIds(List<Long> ids) {
        Objects.requireNonNull(ids, "ids must not be null");

        if (ids.isEmpty()) {
            return List.of();
        }

        return familyMemberTagJpaRepository.findAllById(ids).stream()
            .map(FamilyMemberTagJpaEntity::toFamilyMemberTag)
            .toList();
    }
}
