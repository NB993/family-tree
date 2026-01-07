package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.application.port.out.DeleteFamilyMemberTagMappingPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagMappingPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberTagMappingPort;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTagMapping;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * FamilyMemberTagMapping 아웃바운드 어댑터 클래스입니다.
 * 태그 매핑 관련 outbound port를 구현합니다.
 */
@Component
@RequiredArgsConstructor
public class FamilyMemberTagMappingAdapter implements
    SaveFamilyMemberTagMappingPort,
    DeleteFamilyMemberTagMappingPort,
    FindFamilyMemberTagMappingPort {

    private final FamilyMemberTagMappingJpaRepository familyMemberTagMappingJpaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveAll(final List<FamilyMemberTagMapping> mappings) {
        Objects.requireNonNull(mappings, "mappings must not be null");

        if (mappings.isEmpty()) {
            return;
        }

        List<FamilyMemberTagMappingJpaEntity> entities = mappings.stream()
            .map(FamilyMemberTagMappingJpaEntity::from)
            .toList();

        familyMemberTagMappingJpaRepository.saveAll(entities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllByMemberId(final Long memberId) {
        Objects.requireNonNull(memberId, "memberId must not be null");

        familyMemberTagMappingJpaRepository.deleteAllByMemberId(memberId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FamilyMemberTagMapping> findAllByMemberId(final Long memberId) {
        Objects.requireNonNull(memberId, "memberId must not be null");

        return familyMemberTagMappingJpaRepository.findAllByMemberId(memberId).stream()
            .map(FamilyMemberTagMappingJpaEntity::toFamilyMemberTagMapping)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countByTagId(final Long tagId) {
        Objects.requireNonNull(tagId, "tagId must not be null");

        return familyMemberTagMappingJpaRepository.countByTagId(tagId);
    }
}
