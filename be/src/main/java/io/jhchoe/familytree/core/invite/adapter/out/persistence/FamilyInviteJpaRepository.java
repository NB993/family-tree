package io.jhchoe.familytree.core.invite.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * FamilyInvite JPA 엔티티에 대한 리포지토리 인터페이스입니다.
 */
public interface FamilyInviteJpaRepository extends JpaRepository<FamilyInviteJpaEntity, Long> {
    
    /**
     * 초대 코드로 초대를 조회합니다.
     *
     * @param inviteCode 초대 코드
     * @return 조회된 초대 엔티티 옵셔널
     */
    Optional<FamilyInviteJpaEntity> findByInviteCode(String inviteCode);
    
    /**
     * 요청자 ID로 모든 초대를 조회합니다.
     *
     * @param requesterId 요청자 ID
     * @return 조회된 초대 엔티티 목록
     */
    List<FamilyInviteJpaEntity> findByRequesterId(Long requesterId);
}