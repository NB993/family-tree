package io.jhchoe.familytree.core.family.application.port.in;

import java.util.Objects;
import lombok.Getter;

/**
 * Family 이름 검색을 위한 쿼리 객체입니다.
 */
@Getter
public class FindFamilyByNameContainingQuery {

    private final String name;

    /**
     * Family 이름 검색 쿼리 객체를 생성합니다.
     *
     * @param name 검색할 Family의 이름
     */
    public FindFamilyByNameContainingQuery(String name) {
        String nonNullName = Objects.requireNonNullElse(name, "");
        this.name = nonNullName.trim();
    }
}
