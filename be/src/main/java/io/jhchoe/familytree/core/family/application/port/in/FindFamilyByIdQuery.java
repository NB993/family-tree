package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.common.exception.FTException;

/**
 * Family ID로 조회하기 위한 쿼리 클래스.
 * 
 * <p>프레젠테이션 계층에서 전달받은 사용자 입력을 검증하고 
 * 애플리케이션 계층으로 전달하는 역할을 담당합니다.</p>
 * 
 * @param id 조회할 Family의 ID (null 불가)
 * @param currentUserId 현재 사용자의 ID (접근 제어용, null 허용 - 비로그인 사용자)
 * @throws IllegalArgumentException id가 null인 경우 발생
 */
public record FindFamilyByIdQuery(Long id, Long currentUserId) {

    /**
     * FindFamilyByIdQuery 생성자.
     * 
     * @param id 조회할 Family의 ID
     * @param currentUserId 현재 사용자의 ID (접근 제어용, null 허용)
     * @throws IllegalArgumentException id가 null인 경우 발생 (400 Bad Request로 매핑됨)
     */
    public FindFamilyByIdQuery {
        if (id == null) {
            throw new IllegalArgumentException("Family ID must not be null");
        }
    }
}
