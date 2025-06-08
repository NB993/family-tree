package io.jhchoe.familytree.common.auth.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * RefreshToken JPA 엔티티에 대한 리포지토리 인터페이스입니다.
 */
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, Long> {

    /**
     * 사용자 ID로 RefreshToken을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 조회된 RefreshToken (없으면 Optional.empty())
     */
    Optional<RefreshTokenJpaEntity> findByUserId(Long userId);

    /**
     * 사용자 ID로 RefreshToken을 삭제합니다.
     *
     * @param userId 사용자 ID
     */
    void deleteByUserId(Long userId);

    /**
     * 만료된 RefreshToken들을 조회합니다.
     *
     * @param currentDateTime 현재 시간
     * @return 만료된 RefreshToken 목록
     */
    @Query("SELECT rt FROM RefreshTokenJpaEntity rt WHERE rt.expiresAt < :currentDateTime")
    List<RefreshTokenJpaEntity> findExpiredTokens(@Param("currentDateTime") LocalDateTime currentDateTime);

    /**
     * 만료된 RefreshToken들을 일괄 삭제합니다.
     * 배치 작업에서 사용됩니다.
     *
     * @param currentDateTime 현재 시간
     * @return 삭제된 레코드 수
     */
    @Modifying
    @Query("DELETE FROM RefreshTokenJpaEntity rt WHERE rt.expiresAt < :currentDateTime")
    int deleteExpiredTokens(@Param("currentDateTime") LocalDateTime currentDateTime);

    /**
     * 사용자 ID가 존재하는지 확인합니다.
     *
     * @param userId 사용자 ID
     * @return 존재 여부
     */
    boolean existsByUserId(Long userId);
}
