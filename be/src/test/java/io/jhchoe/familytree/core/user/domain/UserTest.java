package io.jhchoe.familytree.core.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
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
        AuthenticationType authenticationType = AuthenticationType.OAUTH2;
        OAuth2Provider oAuth2Provider = OAuth2Provider.GOOGLE;
        UserRole role = UserRole.USER;
        boolean deleted = false;
        Long createdBy = 1L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 2L;
        LocalDateTime modifiedAt = LocalDateTime.now();

        // when
        User user = User.withId(
            id,
            email,
            name,
            profileUrl,
            authenticationType,
            oAuth2Provider,
            role,
            deleted,
            createdBy,
            createdAt,
            modifiedBy,
            modifiedAt
        );

        // then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(user.getAuthenticationType()).isEqualTo(authenticationType);
        assertThat(user.getOAuth2Provider()).isEqualTo(oAuth2Provider);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.isDeleted()).isEqualTo(deleted);
        assertThat(user.getCreatedBy()).isEqualTo(createdBy);
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
        assertThat(user.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(user.getModifiedAt()).isEqualTo(modifiedAt);
    }

    @Test
    @DisplayName("withId 메서드는 id가 null일 경우 예외를 발생시켜야 한다")
    void given_null_id_when_create_user_with_id_then_throw_exception() {
        // given
        Long id = null;
        String email = "test@example.com";
        String name = "Test User";
        AuthenticationType authenticationType = AuthenticationType.OAUTH2;
        OAuth2Provider oAuth2Provider = OAuth2Provider.GOOGLE;
        UserRole role = UserRole.USER;

        // when & then
        assertThatThrownBy(() -> User.withId(
            id,
            email,
            name,
            null,
            authenticationType,
            oAuth2Provider,
            role,
            false,
            null,
            null,
            null,
            null
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("id must not be null");
    }

    @Test
    @DisplayName("withId 메서드는 email이 null일 경우 예외를 발생시켜야 한다")
    void given_null_email_when_create_user_with_id_then_throw_exception() {
        // given
        Long id = 1L;
        String email = null;
        String name = "Test User";
        AuthenticationType authenticationType = AuthenticationType.OAUTH2;
        OAuth2Provider oAuth2Provider = OAuth2Provider.GOOGLE;
        UserRole role = UserRole.USER;

        // when & then
        assertThatThrownBy(() -> User.withId(
            id,
            email,
            name,
            null,
            authenticationType,
            oAuth2Provider,
            role,
            false,
            null,
            null,
            null,
            null
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("email must not be null");
    }

    @Test
    @DisplayName("withId 메서드는 authenticationType이 null일 경우 예외를 발생시켜야 한다")
    void given_null_authentication_type_when_create_user_with_id_then_throw_exception() {
        // given
        Long id = 1L;
        String email = "test@example.com";
        String name = "Test User";
        AuthenticationType authenticationType = null;
        OAuth2Provider oAuth2Provider = OAuth2Provider.GOOGLE;
        UserRole role = UserRole.USER;

        // when & then
        assertThatThrownBy(() -> User.withId(
            id,
            email,
            name,
            null,
            authenticationType,
            oAuth2Provider,
            role,
            false,
            null,
            null,
            null,
            null
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("authenticationType must not be null");
    }

    @Test
    @DisplayName("withId 메서드는 oAuth2Provider가 null일 경우 예외를 발생시켜야 한다")
    void given_null_oauth2_provider_when_create_user_with_id_then_throw_exception() {
        // given
        Long id = 1L;
        String email = "test@example.com";
        String name = "Test User";
        AuthenticationType authenticationType = AuthenticationType.OAUTH2;
        OAuth2Provider oAuth2Provider = null;
        UserRole role = UserRole.USER;

        // when & then
        assertThatThrownBy(() -> User.withId(
            id,
            email,
            name,
            null,
            authenticationType,
            oAuth2Provider,
            role,
            false,
            null,
            null,
            null,
            null
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("oAuth2Provider must not be null");
    }

    @Test
    @DisplayName("withId 메서드는 role이 null일 경우 예외를 발생시켜야 한다")
    void given_null_role_when_create_user_with_id_then_throw_exception() {
        // given
        Long id = 1L;
        String email = "test@example.com";
        String name = "Test User";
        AuthenticationType authenticationType = AuthenticationType.OAUTH2;
        OAuth2Provider oAuth2Provider = OAuth2Provider.GOOGLE;
        UserRole role = null;

        // when & then
        assertThatThrownBy(() -> User.withId(
            id,
            email,
            name,
            null,
            authenticationType,
            oAuth2Provider,
            role,
            false,
            null,
            null,
            null,
            null
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("role must not be null");
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
            AuthenticationType.OAUTH2,
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false,
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
            AuthenticationType.OAUTH2,
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false,
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
        AuthenticationType authenticationType = AuthenticationType.OAUTH2;
        OAuth2Provider oAuth2Provider = OAuth2Provider.GOOGLE;
        UserRole role = UserRole.USER;

        // when
        User user = User.newUser(
            email,
            name,
            profileUrl,
            authenticationType,
            oAuth2Provider,
            role,
            false,
            null,
            null,
            null,
            null
        );

        // then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(user.getAuthenticationType()).isEqualTo(authenticationType);
        assertThat(user.getOAuth2Provider()).isEqualTo(oAuth2Provider);
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
        assertThat(user.isDeleted()).isFalse();
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
            AuthenticationType.OAUTH2,
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false,
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
            AuthenticationType.OAUTH2,
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false,
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
}
