package io.jhchoe.familytree.core.user.application.port.out;

import io.jhchoe.familytree.core.user.domain.User;

/**
 * User 정보를 수정하기 위한 포트입니다.
 */
public interface ModifyUserPort {

    /**
     * User 정보를 수정합니다.
     *
     * @param user 수정할 User 객체
     * @return 수정된 User의 ID
     */
    Long modify(User user);
}