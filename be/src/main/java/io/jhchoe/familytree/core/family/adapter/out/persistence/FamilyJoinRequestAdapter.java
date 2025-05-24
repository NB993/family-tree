package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.application.port.out.FindFamilyJoinRequestPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyJoinRequestPort;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Family 가입 신청 관련 아웃바운드 포트를 구현하는 어댑터 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class FamilyJoinRequestAdapter implements SaveFamilyJoinRequestPort, FindFamilyJoinRequestPort {

    private final FamilyJoinRequestJpaRepository familyJoinRequestJpaRepository;

    /**
     * 가장 최근의 Family 가입 신청을 조회합니다.
     *
     * @param familyId 조회할 Family ID
     * @param requesterId 조회할 신청자 ID
     * @return 가장 최근의 가입 신청 정보를 Optional로 반환
     */
    @Override
    public Optional<FamilyJoinRequest> findLatestByFamilyIdAndRequesterId(Long familyId, Long requesterId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(requesterId, "requesterId must not be null");

        return familyJoinRequestJpaRepository
            .findTopByFamilyIdAndRequesterIdOrderByIdDesc(familyId, requesterId)
            .map(FamilyJoinRequestJpaEntity::toFamilyJoinRequest);
    }

    /**
     * 특정 Family의 모든 가입 신청 목록을 조회합니다.
     *
     * @param familyId 조회할 Family ID
     * @return Family의 가입 신청 목록
     */
    @Override
    public List<FamilyJoinRequest> findAllByFamilyId(Long familyId) {
        Objects.requireNonNull(familyId, "familyId must not be null");

        return familyJoinRequestJpaRepository
            .findAllByFamilyIdOrderByCreatedAtDesc(familyId)
            .stream()
            .map(FamilyJoinRequestJpaEntity::toFamilyJoinRequest)
            .toList();
    }

    /**
     * Family 가입 신청을 저장합니다.
     *
     * @param familyJoinRequest 저장할 가입 신청 정보
     * @return 저장된 가입 신청의 ID
     */
    @Override
    public Long save(FamilyJoinRequest familyJoinRequest) {
        Objects.requireNonNull(familyJoinRequest, "familyJoinRequest must not be null");

        FamilyJoinRequestJpaEntity entity = FamilyJoinRequestJpaEntity.from(familyJoinRequest);
        FamilyJoinRequestJpaEntity savedEntity = familyJoinRequestJpaRepository.save(entity);
        return savedEntity.getId();
    }
}
