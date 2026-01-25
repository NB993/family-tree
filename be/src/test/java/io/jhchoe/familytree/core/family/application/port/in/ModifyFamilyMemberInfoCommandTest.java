package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.*;

import io.jhchoe.familytree.core.family.domain.BirthdayType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * ModifyFamilyMemberInfoCommand 클래스의 단위 테스트입니다.
 */
@DisplayName("[Unit Test] ModifyFamilyMemberInfoCommandTest")
class ModifyFamilyMemberInfoCommandTest {

    @Test
    @DisplayName("유효한 파라미터로 Command 객체가 생성됩니다")
    void create_command_when_parameters_are_valid() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = 3L;
        String name = "홍길동";
        LocalDateTime birthday = LocalDateTime.of(1990, 5, 15, 0, 0);
        BirthdayType birthdayType = BirthdayType.SOLAR;

        // when
        ModifyFamilyMemberInfoCommand command = new ModifyFamilyMemberInfoCommand(
            familyId, memberId, currentUserId, name, birthday, birthdayType
        );

        // then
        assertThat(command.familyId()).isEqualTo(familyId);
        assertThat(command.memberId()).isEqualTo(memberId);
        assertThat(command.currentUserId()).isEqualTo(currentUserId);
        assertThat(command.name()).isEqualTo(name);
        assertThat(command.birthday()).isEqualTo(birthday);
        assertThat(command.birthdayType()).isEqualTo(birthdayType);
    }

    @Test
    @DisplayName("familyId가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_family_id_is_null() {
        assertThatThrownBy(() -> new ModifyFamilyMemberInfoCommand(
            null, 2L, 3L, "홍길동", null, null
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("familyId");
    }

    @Test
    @DisplayName("memberId가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_member_id_is_null() {
        assertThatThrownBy(() -> new ModifyFamilyMemberInfoCommand(
            1L, null, 3L, "홍길동", null, null
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("memberId");
    }

    @Test
    @DisplayName("currentUserId가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_current_user_id_is_null() {
        assertThatThrownBy(() -> new ModifyFamilyMemberInfoCommand(
            1L, 2L, null, "홍길동", null, null
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("currentUserId");
    }

    @Test
    @DisplayName("name이 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_name_is_null() {
        assertThatThrownBy(() -> new ModifyFamilyMemberInfoCommand(
            1L, 2L, 3L, null, null, null
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("name");
    }

    @Test
    @DisplayName("name이 빈 문자열인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_name_is_blank() {
        assertThatThrownBy(() -> new ModifyFamilyMemberInfoCommand(
            1L, 2L, 3L, "   ", null, null
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("name");
    }

    @Test
    @DisplayName("familyId가 0 이하인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_family_id_is_not_positive() {
        assertThatThrownBy(() -> new ModifyFamilyMemberInfoCommand(
            0L, 2L, 3L, "홍길동", null, null
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("familyId");
    }

    @Test
    @DisplayName("memberId가 0 이하인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_member_id_is_not_positive() {
        assertThatThrownBy(() -> new ModifyFamilyMemberInfoCommand(
            1L, -1L, 3L, "홍길동", null, null
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("memberId");
    }

    @Test
    @DisplayName("currentUserId가 0 이하인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_current_user_id_is_not_positive() {
        assertThatThrownBy(() -> new ModifyFamilyMemberInfoCommand(
            1L, 2L, 0L, "홍길동", null, null
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("currentUserId");
    }

    @Test
    @DisplayName("birthday와 birthdayType이 null이어도 정상 생성됩니다")
    void create_command_when_birthday_and_type_are_null() {
        // when
        ModifyFamilyMemberInfoCommand command = new ModifyFamilyMemberInfoCommand(
            1L, 2L, 3L, "홍길동", null, null
        );

        // then
        assertThat(command.birthday()).isNull();
        assertThat(command.birthdayType()).isNull();
    }
}
