package io.jhchoe.familytree.core.user.application.port.in;

import lombok.Getter;

/**
 * 사용자 이름으로 조회하기 위한 쿼리 객체입니다.
 */
@Getter
public class FindUserByNameQuery {
    private final String name;

    /**
     * 사용자 이름 조회 쿼리 객체를 생성합니다.
     *
     * @param name 조회할 사용자의 이름
     * @throws IllegalArgumentException 이름이 null이거나 빈 값일 경우 예외 발생
     */
    public FindUserByNameQuery(String name) {
        validateName(name);
        this.name = name;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
    }
}
