package io.jhchoe.familytree.common.auth;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByEmail(String email);
    
    /**
     * 이름에 주어진 문자열이 포함된 사용자를 조회합니다.
     *
     * @param name 검색할 이름 문자열
     * @param pageable 페이징 정보
     * @return 조회된 사용자 목록
     */
    List<UserJpaEntity> findByNameContaining(String name, Pageable pageable);

    /**
     * 카카오 ID로 사용자를 조회합니다.
     *
     * @param kakaoId 검색할 카카오 ID
     * @return 조회된 사용자 정보
     */
    Optional<UserJpaEntity> findByKakaoId(String kakaoId);
}
