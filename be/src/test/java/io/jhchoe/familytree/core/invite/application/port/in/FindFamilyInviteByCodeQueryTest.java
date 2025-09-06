package io.jhchoe.familytree.core.invite.application.port.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] FindFamilyInviteByCodeQueryTest")
class FindFamilyInviteByCodeQueryTest {

    @Test
    @DisplayName("유효한 초대 코드로 쿼리를 생성할 수 있습니다")
    void create_query_with_valid_invite_code() {
        // when
        FindFamilyInviteByCodeQuery query = new FindFamilyInviteByCodeQuery("test-code");

        // then
        assertThat(query.inviteCode()).isEqualTo("test-code");
    }

    @Test
    @DisplayName("초대 코드가 null이면 예외가 발생합니다")
    void throw_exception_when_invite_code_is_null() {
        // when & then
        assertThatThrownBy(() -> new FindFamilyInviteByCodeQuery(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("inviteCode must not be null");
    }

    @Test
    @DisplayName("초대 코드가 빈 문자열이면 예외가 발생합니다")
    void throw_exception_when_invite_code_is_blank() {
        // when & then
        assertThatThrownBy(() -> new FindFamilyInviteByCodeQuery(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("inviteCode must not be blank");

        assertThatThrownBy(() -> new FindFamilyInviteByCodeQuery("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("inviteCode must not be blank");
    }
}