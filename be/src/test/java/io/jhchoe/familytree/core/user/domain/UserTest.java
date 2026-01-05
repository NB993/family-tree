package io.jhchoe.familytree.core.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.domain.UserRole;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] User 도메인")
class UserTest {

    @Test
    @DisplayName("withId 메서드는 유효한 매개변수로 User 객체를 생성해야 한다")
    void given_valid_parameters_when_create_user_with_id_then_return_user_object() {
        // given
        Long id = 1L;
        String email = "test@example.com";
        String name = "Test User";
        String profileUrl = "http://example.com/profile.jpg";
        OAuth2Provider oAuth2Provider = OAuth2Provider.GOOGLE;
        UserRole role = UserRole.USER;
        boolean deleted = false;
        Long createdBy = 1L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 2L;
        LocalDateTime modifiedAt = LocalDateTime.now();
        LocalDateTime birthday = LocalDateTime.of(1990, 5, 15, 0, 0);

        // when
        User user = User.withId(
            id,
            email,
            name,
            profileUrl,
            null, // kakaoId
            oAuth2Provider,
            role,
            deleted,
            createdBy,
            createdAt,
            modifiedBy,
            modifiedAt,
            birthday
        );

        // then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(user.getOAuth2Provider()).isEqualTo(oAuth2Provider);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.isDeleted()).isEqualTo(deleted);
        assertThat(user.getCreatedBy()).isEqualTo(createdBy);
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
        assertThat(user.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(user.getModifiedAt()).isEqualTo(modifiedAt);
        assertThat(user.getBirthday()).isEqualTo(birthday);
    }

    @Test
    @DisplayName("withId 메서드는 id가 null일 경우 예외를 발생시켜야 한다")
    void given_null_id_when_create_user_with_id_then_throw_exception() {
        // given
        Long id = null;
        String email = "test@example.com";
        String name = "Test User";
        OAuth2Provider oAuth2Provider = OAuth2Provider.GOOGLE;
        UserRole role = UserRole.USER;

        // when & then
        assertThatThrownBy(() -> User.withId(
            id,
            email,
            name,
            null,
            null, // kakaoId
            oAuth2Provider,
            role,
            false,
            null,
            null,
            null,
            null,
            null
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("id는 null일 수 없습니다");
    }

    @Test
    @DisplayName("withId 메서드는 role이 null일 경우 예외를 발생시켜야 한다")
    void given_null_role_when_create_user_with_id_then_throw_exception() {
        // given
        Long id = 1L;
        String email = "test@example.com";
        String name = "Test User";
        OAuth2Provider oAuth2Provider = OAuth2Provider.GOOGLE;
        UserRole role = null;

        // when & then
        assertThatThrownBy(() -> User.withId(
            id,
            email,
            name,
            null,
            null, // kakaoId
            oAuth2Provider,
            role,
            false,
            null,
            null,
            null,
            null,
            null
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("role은 null일 수 없습니다");
    }

    @Test
    @DisplayName("hasName 메서드는 이름이 일치할 경우 true를 반환해야 한다")
    void given_matching_name_when_has_name_then_return_true() {
        // given
        User user = User.withId(
            1L,
            "test@example.com",
            "Test User",
            null,
            null, // kakaoId
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false,
            null,
            null,
            null,
            null,
            null
        );
        String nameToCompare = "Test User";

        // when
        boolean result = user.hasName(nameToCompare);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("hasName 메서드는 이름이 일치하지 않을 경우 false를 반환해야 한다")
    void given_non_matching_name_when_has_name_then_return_false() {
        // given
        User user = User.withId(
            1L,
            "test@example.com",
            "Test User",
            null,
            null, // kakaoId
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false,
            null,
            null,
            null,
            null,
            null
        );
        String nameToCompare = "Different Name";

        // when
        boolean result = user.hasName(nameToCompare);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("newUser 메서드는 유효한 매개변수로 새 User 객체를 생성해야 한다")
    void given_valid_parameters_when_create_new_user_then_return_user_object() {
        // given
        String email = "test@example.com";
        String name = "Test User";
        String profileUrl = "http://example.com/profile.jpg";
        OAuth2Provider oAuth2Provider = OAuth2Provider.GOOGLE;
        UserRole role = UserRole.USER;
        LocalDateTime birthday = LocalDateTime.of(1990, 5, 15, 0, 0);

        // when
        User user = User.newUser(
            email,
            name,
            profileUrl,
            null, // kakaoId
            oAuth2Provider,
            role,
            false,
            birthday
        );

        // then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(user.getOAuth2Provider()).isEqualTo(oAuth2Provider);
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
        assertThat(user.isDeleted()).isFalse();
        assertThat(user.getBirthday()).isEqualTo(birthday);
        assertThat(user.getCreatedBy()).isNull();
        assertThat(user.getCreatedAt()).isNull();
        assertThat(user.getModifiedBy()).isNull();
        assertThat(user.getModifiedAt()).isNull();
    }

    @Test
    @DisplayName("hasName 메서드는 사용자 이름이 null이고 비교할 이름도 null일 경우 true를 반환해야 한다")
    void given_both_names_null_when_has_name_then_return_true() {
        // given
        User user = User.withId(
            1L,
            "test@example.com",
            null,
            null,
            null, // kakaoId
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false,
            null,
            null,
            null,
            null,
            null
        );
        String nameToCompare = null;

        // when
        boolean result = user.hasName(nameToCompare);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("hasName 메서드는 사용자 이름이 null이고 비교할 이름이 null이 아닐 경우 false를 반환해야 한다")
    void given_user_name_null_compare_name_not_null_when_has_name_then_return_false() {
        // given
        User user = User.withId(
            1L,
            "test@example.com",
            null,
            null,
            null, // kakaoId
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false,
            null,
            null,
            null,
            null,
            null
        );
        String nameToCompare = "Test User";

        // when
        boolean result = user.hasName(nameToCompare);

        // then
        assertThat(result).isFalse();
    }

    // ===== PRD-001: User 도메인 확장 테스트 =====

    @Test
    @DisplayName("birthday를 포함하여 User를 생성할 수 있다")
    void create_user_with_birthday() {
        // given
        Long id = 1L;
        String email = "test@example.com";
        String name = "Test User";
        LocalDateTime birthday = LocalDateTime.of(1990, 1, 15, 0, 0);

        // when
        User user = User.withId(
            id,
            email,
            name,
            null,
            null,
            OAuth2Provider.KAKAO,
            UserRole.USER,
            false,
            null,
            null,
            null,
            null,
            birthday
        );

        // then
        assertThat(user.getBirthday()).isEqualTo(birthday);
    }

    @Test
    @DisplayName("email이 null인 User를 생성할 수 있다 - 수동 등록 사용자용")
    void create_user_with_null_email() {
        // given
        Long id = 1L;
        String name = "수동 등록 사용자";

        // when
        User user = User.withId(
            id,
            null, // email nullable
            name,
            null,
            null,
            null, // 수동 등록 사용자는 OAuth2Provider도 nullable
            UserRole.USER,
            false,
            null,
            null,
            null,
            null,
            null
        );

        // then
        assertThat(user.getEmail()).isNull();
        assertThat(user.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("newManualUser로 생성된 사용자는 로그인 불가능하다 - isLoginable() false 검증")
    void create_user_with_manual_user_is_not_loginable() {
        // given
        String name = "수동 등록 사용자";

        // when
        User user = User.newManualUser(name, null, null);

        // then
        assertThat(user.getId()).isNull();
        assertThat(user.isLoginable()).isFalse();
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isNull();
    }

    @Test
    @DisplayName("OAuth2 사용자는 로그인 가능하다 - isLoginable() true 검증")
    void oauth2_user_is_loginable() {
        // given
        User user = User.withId(
            1L,
            "test@example.com",
            "Test User",
            null,
            null,
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false,
            null,
            null,
            null,
            null,
            null
        );

        // when
        boolean loginable = user.isLoginable();

        // then
        assertThat(loginable).isTrue();
    }
}
