package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.UpdateFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Family 멤버 관련 아웃바운드 포트를 구현하는 어댑터 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class FamilyMemberAdapter implements FindFamilyMemberPort, UpdateFamilyMemberPort {

    private final FamilyMemberJpaRepository familyMemberJpaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByFamilyIdAndUserId(Long familyId, Long userId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");

        return familyMemberJpaRepository.existsByFamilyIdAndUserId(familyId, userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countActiveByUserId(Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        
        return familyMemberJpaRepository.countByUserIdAndStatus(userId, FamilyMemberStatus.ACTIVE);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FamilyMember> findById(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        
        return familyMemberJpaRepository.findById(id)
                .map(this::mapToDomainEntity);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FamilyMember> findByFamilyIdAndUserId(Long familyId, Long userId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        
        return familyMemberJpaRepository.findByFamilyIdAndUserId(familyId, userId)
                .map(this::mapToDomainEntity);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<FamilyMember> findAllByFamilyId(Long familyId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        
        return familyMemberJpaRepository.findAllByFamilyId(familyId)
                .stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Long update(FamilyMember familyMember) {
        Objects.requireNonNull(familyMember, "familyMember must not be null");
        Objects.requireNonNull(familyMember.getId(), "familyMember.id must not be null");
        
        // 기존 엔티티를 조회
        FamilyMemberJpaEntity entity = familyMemberJpaRepository.findById(familyMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + familyMember.getId()));
        
        // 업데이트할 엔티티 생성
        FamilyMemberJpaEntity updatedEntity = new FamilyMemberJpaEntity(
                entity.getId(),
                entity.getFamilyId(),
                entity.getUserId(),
                entity.getName(),
                entity.getProfileUrl(),
                entity.getBirthday(),
                entity.getNationality(),
                familyMember.getStatus(),
                familyMember.getRole(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getModifiedBy(),
                entity.getModifiedAt()
        );
        
        // 저장 및 ID 반환
        return familyMemberJpaRepository.save(updatedEntity).getId();
    }
    
    /**
     * JPA 엔티티를 도메인 엔티티로 변환합니다.
     *
     * @param jpaEntity 변환할 JPA 엔티티
     * @return 변환된 도메인 엔티티
     */
    private FamilyMember mapToDomainEntity(FamilyMemberJpaEntity jpaEntity) {
        return FamilyMember.withRole(
                jpaEntity.getId(),
                jpaEntity.getFamilyId(),
                jpaEntity.getUserId(),
                jpaEntity.getName(),
                jpaEntity.getProfileUrl(),
                jpaEntity.getBirthday(),
                jpaEntity.getNationality(),
                jpaEntity.getStatus(),
                jpaEntity.getRole(),
                jpaEntity.getCreatedBy(),
                jpaEntity.getCreatedAt(),
                jpaEntity.getModifiedBy(),
                jpaEntity.getModifiedAt()
        );
    }
}
