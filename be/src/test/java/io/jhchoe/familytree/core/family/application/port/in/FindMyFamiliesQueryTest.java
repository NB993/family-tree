package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * FindMyFamiliesQuery 클래스의 단위 테스트입니다.
 */
@DisplayName("[Unit Test] FindMyFamiliesQuery")
class FindMyFamiliesQueryTest {

    @Test
    @DisplayName("유효한 사용자 ID로 쿼리 객체를 생성할 수 있다")
    void 유효한_사용자_ID로_쿼리_객체_생성() {
        // given
        Long userId = 1L;

        // when
        FindMyFamiliesQuery query = new FindMyFamiliesQuery(userId);

        // then
        assertThat(query.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("사용자 ID가 null이면 예외가 발생한다")
    void 사용자_ID가_null이면_예외_발생() {
        // given
        Long userId = null;

        // when & then
        assertThatThrownBy(() -> new FindMyFamiliesQuery(userId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("userId must not be null");
    }
}
