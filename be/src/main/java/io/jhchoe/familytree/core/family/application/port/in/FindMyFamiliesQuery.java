package io.jhchoe.familytree.core.family.application.port.in;

import java.util.Objects;
import lombok.Getter;

/**
 * 사용자별 소속 Family 목록 조회를 위한 쿼리 객체입니다.
 * 
 * <p>현재 인증된 사용자가 소속된 모든 Family 목록을 조회하는 데 사용됩니다.
 * 페이징은 적용하지 않으며, 일반적으로 15개 이하의 Family 목록을 반환합니다.</p>
 */
@Getter
public class FindMyFamiliesQuery {

    private final Long userId;

    /**
     * 내 소속 Family 목록 조회 쿼리 객체를 생성합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @throws IllegalArgumentException userId가 null인 경우
     */
    public FindMyFamiliesQuery(Long userId) {
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
    }
}
