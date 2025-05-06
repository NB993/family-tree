package io.jhchoe.familytree.core.user.application.port.out;

import io.jhchoe.familytree.core.user.domain.User;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 정보 조회를 위한 아웃바운드 포트 인터페이스입니다.
 */
public interface FindUserPort {

    /**
     * 사용자 ID로 사용자를 조회합니다.
     *
     * @param id 조회할 사용자의 ID
     * @return 조회된 사용자 정보, 존재하지 않을 경우 빈 Optional 반환
     */
    Optional<User> findById(Long id);
    
    /**
     * 이름으로 사용자를 조회합니다.
     *
     * @param name 조회할 사용자의 이름
     * @return 해당 이름을 가진 사용자 목록
     */
    List<User> findByName(String name);
    
    /**
     * 모든 사용자를 조회합니다.
     *
     * @return 모든 사용자 목록
     */
    List<User> findAll();
}
