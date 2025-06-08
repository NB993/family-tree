package io.jhchoe.familytree.common.auth.application.port.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("[Unit Test] FindRefreshTokenByUserIdQueryTest")
class FindRefreshTokenByUserIdQueryTest {

    @Test
    @DisplayName("유효한 사용자 ID로 쿼리 객체를 생성합니다")
    void create_success_when_user_id_is_valid() {
        // given
        Long userId = 1L;
        
        // when
        FindRefreshTokenByUserIdQuery query = new FindRefreshTokenByUserIdQuery(userId);
        
        // then
        assertThat(query.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("사용자 ID가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_user_id_is_null() {
        assertThatThrownBy(() -> new FindRefreshTokenByUserIdQuery(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("userId must not be null");
    }

    @Test
    @DisplayName("동일한 사용자 ID를 가진 쿼리 객체는 equals가 true를 반환합니다")
    void equals_returns_true_when_user_id_is_same() {
        // given
        Long userId = 1L;
        FindRefreshTokenByUserIdQuery query1 = new FindRefreshTokenByUserIdQuery(userId);
        FindRefreshTokenByUserIdQuery query2 = new FindRefreshTokenByUserIdQuery(userId);
        
        // when & then
        assertThat(query1).isEqualTo(query2);
        assertThat(query1.hashCode()).isEqualTo(query2.hashCode());
    }
}
