package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberStatusHistoryPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberStatusHistoryPort;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatusHistory;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 구성원 상태 변경 이력 관련 포트를 구현하는 어댑터 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class FamilyMemberStatusHistoryAdapter implements SaveFamilyMemberStatusHistoryPort, FindFamilyMemberStatusHistoryPort {

    private final FamilyMemberStatusHistoryJpaRepository familyMemberStatusHistoryJpaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long save(FamilyMemberStatusHistory history) {
        Objects.requireNonNull(history, "history must not be null");
        
        FamilyMemberStatusHistoryJpaEntity entity = FamilyMemberStatusHistoryJpaEntity.from(history);
        return familyMemberStatusHistoryJpaRepository.save(entity).getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FamilyMemberStatusHistory> findAllByMemberId(Long memberId) {
        Objects.requireNonNull(memberId, "memberId must not be null");
        
        return familyMemberStatusHistoryJpaRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(FamilyMemberStatusHistoryJpaEntity::toDomainEntity)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FamilyMemberStatusHistory> findAllByFamilyId(Long familyId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        
        return familyMemberStatusHistoryJpaRepository.findAllByFamilyIdOrderByCreatedAtDesc(familyId)
                .stream()
                .map(FamilyMemberStatusHistoryJpaEntity::toDomainEntity)
                .collect(Collectors.toList());
    }
}
