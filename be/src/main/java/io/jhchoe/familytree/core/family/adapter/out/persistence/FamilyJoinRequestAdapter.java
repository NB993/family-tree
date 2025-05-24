package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.application.port.out.FindFamilyJoinRequestPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyJoinRequestPort;
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
public class FamilyJoinRequestAdapter implements SaveFamilyJoinRequestPort, FindFamilyJoinRequestPort, ModifyFamilyJoinRequestPort {

    private final FamilyJoinRequestJpaRepository familyJoinRequestJpaRepository;

    /**
     * ID로 Family 가입 신청을 조회합니다.
     *
     * @param id 조회할 가입 신청 ID
     * @return 가입 신청 정보를 Optional로 반환
     */
    @Override
    public Optional<FamilyJoinRequest> findById(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        return familyJoinRequestJpaRepository.findById(id)
            .map(FamilyJoinRequestJpaEntity::toFamilyJoinRequest);
    }

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

    /**
     * Family 가입 신청 정보를 업데이트합니다.
     *
     * @param familyJoinRequest 업데이트할 가입 신청 정보
     * @return 업데이트된 FamilyJoinRequest 객체
     */
    @Override
    public FamilyJoinRequest updateFamilyJoinRequest(FamilyJoinRequest familyJoinRequest) {
        Objects.requireNonNull(familyJoinRequest, "familyJoinRequest must not be null");
        Objects.requireNonNull(familyJoinRequest.getId(), "familyJoinRequest.id must not be null");

        FamilyJoinRequestJpaEntity entity = FamilyJoinRequestJpaEntity.from(familyJoinRequest);
        FamilyJoinRequestJpaEntity savedEntity = familyJoinRequestJpaRepository.save(entity);
        return savedEntity.toFamilyJoinRequest();
    }
}
