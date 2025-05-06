package io.jhchoe.familytree.core.user.application.port.in;

import io.jhchoe.familytree.core.user.domain.User;
import java.util.List;

/**
 * 사용자 이름 기반 조회 유스케이스
 */
public interface FindUserUseCase {

    /**
     * 이름으로 사용자를 조회합니다.
     *
     * @param query 사용자 조회 쿼리 객체
     * @return 조회된 사용자 목록
     */
    List<User> findByName(FindUserByNameQuery query);
}
