package io.jhchoe.familytree.core.invite.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * FamilyInviteResponse JPA 엔티티에 대한 리포지토리 인터페이스입니다.
 */
public interface FamilyInviteResponseJpaRepository extends JpaRepository<FamilyInviteResponseJpaEntity, Long> {
    
    /**
     * 초대 ID와 카카오 ID로 응답을 조회합니다.
     *
     * @param inviteId 초대 ID
     * @param kakaoId 카카오 ID
     * @return 조회된 응답 엔티티 옵셔널
     */
    Optional<FamilyInviteResponseJpaEntity> findByInviteIdAndKakaoId(Long inviteId, String kakaoId);
    
    /**
     * 초대 ID로 모든 응답을 조회합니다.
     *
     * @param inviteId 초대 ID
     * @return 조회된 응답 엔티티 목록
     */
    List<FamilyInviteResponseJpaEntity> findByInviteId(Long inviteId);
}