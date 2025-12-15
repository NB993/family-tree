package io.jhchoe.familytree.core.user.application.port.out;

import io.jhchoe.familytree.core.user.domain.User;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 사용자 조회를 위한 아웃바운드 포트 인터페이스
 */
public interface FindUserPort {

    /**
     * 이름으로 사용자를 조회합니다.
     *
     * @param name 검색할 사용자 이름
     * @param pageable 페이징 정보
     * @return 조회된 사용자 목록
     */
    List<User> findByNameContaining(String name, Pageable pageable);

    /**
     * ID로 사용자를 조회합니다.
     * @param id 사용자 ID
     * @return 조회된 사용자 정보
     */
    Optional<User> findById(Long id);

    /**
     * 이메일로 사용자를 조회합니다.
     *
     * @param email 검색할 이메일
     * @return 조회된 사용자 정보
     */
    Optional<User> findByEmail(String email);
}
