package io.jhchoe.familytree.core.user.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import io.jhchoe.familytree.common.auth.UserJpaEntity;
import io.jhchoe.familytree.common.auth.UserJpaRepository;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.jhchoe.familytree.test.fixture.UserFixture;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@DisplayName("[Acceptance Test] ModifyUserControllerTest")
class ModifyUserControllerTest extends AcceptanceTestBase {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAll();
    }

    private Long createTestUserAndGetId() {
        UserJpaEntity userEntity = UserJpaEntity.ofOAuth2User(UserFixture.newOAuth2User());
        UserJpaEntity savedUser = userJpaRepository.saveAndFlush(userEntity);
        return savedUser.getId();
    }

    private FTUser createMockPrincipal(Long userId) {
        return FTUser.ofOAuth2User(
            userId,
            "테스트사용자",
            "test@example.com",
            OAuth2Provider.GOOGLE,
            Map.of(
                "sub", String.valueOf(userId),
                "name", "테스트사용자",
                "email", "test@example.com"
            )
        );
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @Test
        @DisplayName("유효한 요청으로 User 프로필을 수정하면 200 OK와 수정된 정보를 반환합니다")
        void modify_user_returns_200_when_valid_request() {
            // given
            Long userId = createTestUserAndGetId();

            // when & then
            given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .postProcessors(SecurityMockMvcRequestPostProcessors.user(createMockPrincipal(userId)))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("""
                    {
                        "name": "새이름",
                        "birthday": "1995-05-15T00:00:00",
                        "birthdayType": "LUNAR"
                    }
                    """)
                .when()
                .patch("/api/users/me")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(userId.intValue()))
                .body("name", equalTo("새이름"))
                .body("email", notNullValue())
                .body("profileUrl", notNullValue())
                .body("birthday", equalTo("1995-05-15T00:00:00"))
                .body("birthdayType", equalTo("LUNAR"));
        }

        @Test
        @DisplayName("이름만 수정하면 200 OK를 반환합니다")
        void modify_user_name_only_returns_200() {
            // given
            Long userId = createTestUserAndGetId();

            // when & then
            given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .postProcessors(SecurityMockMvcRequestPostProcessors.user(createMockPrincipal(userId)))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("""
                    {
                        "name": "변경된이름"
                    }
                    """)
                .when()
                .patch("/api/users/me")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(userId.intValue()))
                .body("name", equalTo("변경된이름"));
        }

        @Test
        @DisplayName("생일을 null로 수정하면 200 OK를 반환합니다")
        void modify_user_birthday_to_null_returns_200() {
            // given
            Long userId = createTestUserAndGetId();

            // when & then
            given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .postProcessors(SecurityMockMvcRequestPostProcessors.user(createMockPrincipal(userId)))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("""
                    {
                        "name": "테스트사용자",
                        "birthday": null,
                        "birthdayType": null
                    }
                    """)
                .when()
                .patch("/api/users/me")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(userId.intValue()))
                .body("birthday", nullValue())
                .body("birthdayType", nullValue());
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureCases {

        @Test
        @DisplayName("존재하지 않는 User 수정 시 404 Not Found를 반환합니다")
        void modify_user_returns_404_when_user_not_found() {
            // given - 존재하지 않는 ID로 mock principal 생성
            FTUser nonExistentUser = createMockPrincipal(999L);

            // when & then
            given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .postProcessors(SecurityMockMvcRequestPostProcessors.user(nonExistentUser))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("""
                    {
                        "name": "새이름"
                    }
                    """)
                .when()
                .patch("/api/users/me")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("이름이 빈 문자열이면 400 Bad Request를 반환합니다")
        void modify_user_returns_400_when_name_is_blank() {
            // given
            Long userId = createTestUserAndGetId();

            // when & then
            given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .postProcessors(SecurityMockMvcRequestPostProcessors.user(createMockPrincipal(userId)))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("""
                    {
                        "name": "   "
                    }
                    """)
                .when()
                .patch("/api/users/me")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("생일만 있고 생일 유형이 없으면 400 Bad Request를 반환합니다")
        void modify_user_returns_400_when_birthday_without_type() {
            // given
            Long userId = createTestUserAndGetId();

            // when & then
            given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .postProcessors(SecurityMockMvcRequestPostProcessors.user(createMockPrincipal(userId)))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("""
                    {
                        "name": "테스트",
                        "birthday": "1995-05-15T00:00:00"
                    }
                    """)
                .when()
                .patch("/api/users/me")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("인증되지 않은 요청은 401 Unauthorized를 반환합니다")
        void modify_user_returns_401_when_not_authenticated() {
            // when & then
            given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("""
                    {
                        "name": "새이름"
                    }
                    """)
                .when()
                .patch("/api/users/me")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }
}