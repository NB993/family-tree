package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberPort;
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
public class FamilyMemberAdapter implements FindFamilyMemberPort, ModifyFamilyMemberPort, SaveFamilyMemberPort {

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
    public List<FamilyMember> findAllByUserId(Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        
        return familyMemberJpaRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Long modify(FamilyMember familyMember) {
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
                entity.getRelationshipType(),
                entity.getCustomRelationship(),
                entity.getProfileUrl(),
                entity.getBirthday(),
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
     * {@inheritDoc}
     */
    @Override
    public Long save(FamilyMember familyMember) {
        Objects.requireNonNull(familyMember, "familyMember must not be null");
        
        // 도메인 객체를 JPA 엔티티로 변환
        FamilyMemberJpaEntity entity = FamilyMemberJpaEntity.from(familyMember);
        
        // 저장 및 ID 반환
        return familyMemberJpaRepository.save(entity).getId();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<FamilyMember> findByUserId(Long userId) {
        return findAllByUserId(userId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<FamilyMember> findByFamilyId(Long familyId) {
        return findAllByFamilyId(familyId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FamilyMember> findByUserIdAndRole(Long userId, FamilyMemberRole role) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(role, "role must not be null");

        return familyMemberJpaRepository.findByUserIdAndRole(userId, role)
                .map(this::mapToDomainEntity);
    }

    /**
     * JPA 엔티티를 도메인 엔티티로 변환합니다.
     *
     * @param jpaEntity 변환할 JPA 엔티티
     * @return 변환된 도메인 엔티티
     */
    private FamilyMember mapToDomainEntity(FamilyMemberJpaEntity jpaEntity) {
        return jpaEntity.toFamilyMember();
    }
}
