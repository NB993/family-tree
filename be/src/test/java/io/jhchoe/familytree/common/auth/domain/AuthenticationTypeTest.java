package io.jhchoe.familytree.common.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * AuthenticationType enum 테스트
 */
@DisplayName("[Unit Test] AuthenticationTypeTest")
class AuthenticationTypeTest {

    @Test
    @DisplayName("NONE 값이 존재합니다 - 수동 등록 사용자용")
    void contains_none_value() {
        // when
        AuthenticationType none = AuthenticationType.NONE;

        // then
        assertThat(none).isNotNull();
        assertThat(none.name()).isEqualTo("NONE");
    }

    @Test
    @DisplayName("NONE 타입은 로그인 불가능 여부를 확인할 수 있습니다")
    void none_type_is_not_loginable() {
        // given
        AuthenticationType none = AuthenticationType.NONE;

        // when
        boolean loginable = none.isLoginable();

        // then
        assertThat(loginable).isFalse();
    }

    @Test
    @DisplayName("OAUTH2 타입은 로그인 가능합니다")
    void oauth2_type_is_loginable() {
        // given
        AuthenticationType oauth2 = AuthenticationType.OAUTH2;

        // when
        boolean loginable = oauth2.isLoginable();

        // then
        assertThat(loginable).isTrue();
    }
}
