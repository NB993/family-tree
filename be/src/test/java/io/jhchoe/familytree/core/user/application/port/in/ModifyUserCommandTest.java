package io.jhchoe.familytree.core.user.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.BirthdayType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] ModifyUserCommandTest")
class ModifyUserCommandTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 값으로 Command 객체가 생성됩니다")
        void create_command_successfully() {
            // given
            Long userId = 1L;
            String name = "홍길동";
            LocalDateTime birthday = LocalDateTime.of(1990, 5, 15, 0, 0);
            BirthdayType birthdayType = BirthdayType.SOLAR;

            // when
            ModifyUserCommand command = new ModifyUserCommand(userId, name, birthday, birthdayType);

            // then
            assertThat(command.userId()).isEqualTo(userId);
            assertThat(command.name()).isEqualTo(name);
            assertThat(command.birthday()).isEqualTo(birthday);
            assertThat(command.birthdayType()).isEqualTo(birthdayType);
        }

        @Test
        @DisplayName("생일 없이 이름만으로 Command 객체가 생성됩니다")
        void create_command_without_birthday_successfully() {
            // given
            Long userId = 1L;
            String name = "홍길동";

            // when
            ModifyUserCommand command = new ModifyUserCommand(userId, name, null, null);

            // then
            assertThat(command.userId()).isEqualTo(userId);
            assertThat(command.name()).isEqualTo(name);
            assertThat(command.birthday()).isNull();
            assertThat(command.birthdayType()).isNull();
        }
    }

    @Nested
    @DisplayName("userId 검증 테스트")
    class UserIdValidationTest {

        @Test
        @DisplayName("userId가 null인 경우 NullPointerException이 발생합니다")
        void throw_when_user_id_is_null() {
            // when & then
            assertThatThrownBy(() -> new ModifyUserCommand(null, "홍길동", null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("userId는 null일 수 없습니다");
        }

        @Test
        @DisplayName("userId가 0 이하인 경우 IllegalArgumentException이 발생합니다")
        void throw_when_user_id_is_not_positive() {
            // when & then
            assertThatThrownBy(() -> new ModifyUserCommand(0L, "홍길동", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId는 0보다 커야 합니다");

            assertThatThrownBy(() -> new ModifyUserCommand(-1L, "홍길동", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId는 0보다 커야 합니다");
        }
    }

    @Nested
    @DisplayName("name 검증 테스트")
    class NameValidationTest {

        @Test
        @DisplayName("name이 null인 경우 NullPointerException이 발생합니다")
        void throw_when_name_is_null() {
            // when & then
            assertThatThrownBy(() -> new ModifyUserCommand(1L, null, null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("name은 null일 수 없습니다");
        }

        @Test
        @DisplayName("name이 공백만인 경우 IllegalArgumentException이 발생합니다")
        void throw_when_name_is_blank() {
            // when & then
            assertThatThrownBy(() -> new ModifyUserCommand(1L, "", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name은 비어있을 수 없습니다");

            assertThatThrownBy(() -> new ModifyUserCommand(1L, "   ", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name은 비어있을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("birthday/birthdayType 검증 테스트")
    class BirthdayValidationTest {

        @Test
        @DisplayName("birthday가 있는데 birthdayType이 null인 경우 IllegalArgumentException이 발생합니다")
        void throw_when_birthday_without_type() {
            // given
            LocalDateTime birthday = LocalDateTime.of(1990, 5, 15, 0, 0);

            // when & then
            assertThatThrownBy(() -> new ModifyUserCommand(1L, "홍길동", birthday, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("생일이 있으면 생일 유형도 필수입니다");
        }

        @Test
        @DisplayName("birthdayType이 있는데 birthday가 null인 경우 IllegalArgumentException이 발생합니다")
        void throw_when_type_without_birthday() {
            // when & then
            assertThatThrownBy(() -> new ModifyUserCommand(1L, "홍길동", null, BirthdayType.SOLAR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("생일 유형이 있으면 생일도 필수입니다");
        }
    }
}