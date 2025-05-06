package io.jhchoe.familytree.core.user.application.port.in;

import io.jhchoe.familytree.core.user.domain.User;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 조회를 위한 유스케이스 인터페이스입니다.
 */
public interface FindUserUseCase {

    /**
     * 사용자 ID로 사용자를 조회합니다.
     *
     * @param id 조회할 사용자의 ID
     * @return 조회된 사용자, 존재하지 않을 경우 빈 Optional 반환
     */
    Optional<User> findById(Long id);
    
    /**
     * 이름으로 사용자를 조회합니다.
     *
     * @param query 이름 검색을 위한 쿼리 객체
     * @return 해당 이름을 가진 사용자 목록
     */
    List<User> findByName(FindUserByNameQuery query);
    
    /**
     * 모든 사용자를 조회합니다.
     *
     * @return 모든 사용자 목록
     */
    List<User> findAll();
}
