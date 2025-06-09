package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.common.exception.CommonExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyTreePort;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * Family 아웃바운드 어댑터 클래스입니다. 이 클래스는 Family와 관련된 모든 outbound port를 구현합니다.
 */
@Component
@RequiredArgsConstructor
public class FamilyAdapter implements SaveFamilyPort, ModifyFamilyPort, FindFamilyPort, FindFamilyTreePort {

    private final FamilyJpaRepository familyJpaRepository;
    private final FamilyMemberJpaRepository familyMemberJpaRepository;
    private final FamilyMemberRelationshipJpaRepository familyMemberRelationshipJpaRepository;

    /**
     * Family 데이터를 저장하고, 저장된 Family의 ID를 반환합니다.
     *
     * @param family 저장할 Family 데이터 (null 불가)
     * @return 저장된 Family의 ID
     * @throws NullPointerException Family 데이터가 null인 경우 예외가 발생합니다.
     */
    @Override
    public Long save(Family family) {
        Objects.requireNonNull(family, "family must not be null");

        FamilyJpaEntity familyJpaEntity = FamilyJpaEntity.from(family);
        return familyJpaRepository.save(familyJpaEntity).getId();
    }

    @Override
    public Optional<Family> findById(Long id) {
        return familyJpaRepository.findById(id).map(FamilyJpaEntity::toFamily);
    }

    @Override
    public boolean existsById(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        return familyJpaRepository.existsById(id);
    }

    /**
     * 전달받은 name을 Family name으로 포함하고 있는 Family 목록을 조회하여 응답합니다.
     *
     * @param name 조회할 Family이 포함할 이름
     * @return 조회된 Family 목록
     */
    @Override
    public List<Family> findByNameContaining(String name) {
        String nonNullName = Objects.requireNonNullElse(name, "");
        List<FamilyJpaEntity> families = familyJpaRepository.findByNameContaining(nonNullName);

        return families.stream()
            .map(FamilyJpaEntity::toFamily)
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Family> findByName(String name) {
        Objects.requireNonNull(name, "name must not be null");
        
        return familyJpaRepository.findByNameAndDeletedFalse(name)
            .map(FamilyJpaEntity::toFamily);
    }

    /**
     * Family 데이터를 수정하고 저장된 Family의 ID를 반환합니다.
     *
     * @param family 수정할 Family 데이터 (null 불가)
     * @return 수정된 Family의 ID
     * @throws NullPointerException Family 데이터가 null인 경우 예외가 발생합니다.
     * @throws FTException 지정된 ID에 해당하는 Family가 없을 경우 예외가 발생합니다.
     */
    @Override
    public Long modifyFamily(Family family) {
        Objects.requireNonNull(family, "family must not be null");

        return familyJpaRepository.findById(family.getId())
            .map(familyJpaEntity -> {
                familyJpaEntity.update(family.getName(), family.getDescription(), family.getProfileUrl(), family.getIsPublic());
                FamilyJpaEntity save = familyJpaRepository.save(familyJpaEntity);
                return save.getId();
            })
            .orElseThrow(() -> new FTException(CommonExceptionCode.NOT_FOUND, "family"));
    }

    // FindFamilyTreePort 구현

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Family> findFamily(final Long familyId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        
        return familyJpaRepository.findById(familyId)
            .map(FamilyJpaEntity::toFamily);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FamilyMember> findActiveFamilyMembers(final Long familyId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        
        return familyMemberJpaRepository.findAllByFamilyId(familyId).stream()
            .filter(entity -> entity.getStatus() == FamilyMemberStatus.ACTIVE)
            .map(FamilyMemberJpaEntity::toFamilyMember)
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FamilyMember> findFamilyMember(final Long memberId) {
        Objects.requireNonNull(memberId, "memberId must not be null");
        
        return familyMemberJpaRepository.findById(memberId)
            .map(FamilyMemberJpaEntity::toFamilyMember);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FamilyMemberRelationship> findFamilyMemberRelationships(final Long familyId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        
        // 가족의 모든 구성원 관계를 조회
        return familyMemberRelationshipJpaRepository.findAllByFamilyId(familyId).stream()
            .map(FamilyMemberRelationshipJpaEntity::toFamilyRelationship)
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FamilyMemberRelationship> findRelationshipsByFromMember(final Long fromMemberId) {
        Objects.requireNonNull(fromMemberId, "fromMemberId must not be null");
        
        // fromMemberId로 familyId를 조회해야 함
        Optional<FamilyMemberJpaEntity> memberEntity = familyMemberJpaRepository.findById(fromMemberId);
        if (memberEntity.isEmpty()) {
            return List.of();
        }
        
        Long familyId = memberEntity.get().getFamilyId();
        return familyMemberRelationshipJpaRepository.findAllByFamilyIdAndFromMemberId(familyId, fromMemberId).stream()
            .map(FamilyMemberRelationshipJpaEntity::toFamilyRelationship)
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FamilyMember> findFamilyMemberByUserId(final Long familyId, final Long userId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        
        return familyMemberJpaRepository.findByFamilyIdAndUserId(familyId, userId)
            .map(FamilyMemberJpaEntity::toFamilyMember);
    }
}
