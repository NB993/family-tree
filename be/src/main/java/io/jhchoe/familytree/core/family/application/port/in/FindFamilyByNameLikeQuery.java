package io.jhchoe.familytree.core.family.application.port.in;

import lombok.Getter;

/**
 * Family 이름 검색을 위한 쿼리 객체입니다.
 */
@Getter
public class FindFamilyByNameLikeQuery {

    private final String name;

    /**
     * Family 이름 검색 쿼리 객체를 생성합니다.
     *
     * @param name 검색할 Family의 이름
     * @throws IllegalArgumentException name이 null이거나 비어있을 경우 예외 발생
     */
    public FindFamilyByNameLikeQuery(String name) {
        validateName(name);
        this.name = name;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Family 이름은 필수입니다.");
        }
    }
}
