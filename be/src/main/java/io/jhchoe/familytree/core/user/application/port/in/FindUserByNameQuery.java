package io.jhchoe.familytree.core.user.application.port.in;

import lombok.Getter;

import java.util.Objects;

/**
 * 이름으로 사용자를 조회하기 위한 쿼리 객체
 */
@Getter
public class FindUserByNameQuery {
    private final String name;
    private final int page;
    private final int size;

    /**
     * 이름으로 사용자 조회 쿼리 객체를 생성합니다.
     *
     * @param name 검색할 사용자 이름 (필수)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지당 항목 수
     */
    public FindUserByNameQuery(String name, int page, int size) {
        validateName(name);
        validatePagination(page, size);
        
        this.name = name;
        this.page = page;
        this.size = size;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("사용자 이름은 필수입니다.");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("사용자 이름은 50자를 초과할 수 없습니다.");
        }
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("페이지 크기는 1에서 100 사이여야 합니다.");
        }
    }
}
