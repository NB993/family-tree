package io.jhchoe.familytree.core.user.adapter.in;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.config.FTMockUser;
import io.jhchoe.familytree.core.user.application.port.in.FindUserUseCase;
import io.jhchoe.familytree.core.user.domain.User;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@DisplayName("[Acceptance Test] FindUserController")
class FindUserControllerTest extends AcceptanceTestBase {

    @MockBean
    private FindUserUseCase findUserUseCase;

    @Test
    @DisplayName("findUserById 메서드는 ID로 사용자를 조회해야 한다")
    @FTMockUser
    void given_id_when_find_user_by_id_then_return_user() {
        // given
        Long userId = 1L;
        User user = User.withId(
            userId,
            "test@example.com",
            "Test User",
            "http://example.com/profile.jpg",
            1L,
            LocalDateTime.now().minusDays(1),
            null,
            null
        );
        
        when(findUserUseCase.findById(anyLong())).thenReturn(Optional.of(user));

        // when & then
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/users/{id}", userId)
            .then()
            .statusCode(200)
            .body("id", org.hamcrest.Matchers.equalTo(1))
            .body("email", org.hamcrest.Matchers.equalTo("test@example.com"))
            .body("name", org.hamcrest.Matchers.equalTo("Test User"));
    }

    @Test
    @DisplayName("findUserById 메서드는 존재하지 않는 ID로 조회 시 404 상태코드를 반환해야 한다")
    @FTMockUser
    void given_non_existent_id_when_find_user_by_id_then_return_404() {
        // given
        Long userId = 999L;
        
        when(findUserUseCase.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/users/{id}", userId)
            .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("findUsersByName 메서드는 이름으로 사용자를 조회해야 한다")
    @FTMockUser
    void given_name_when_find_users_by_name_then_return_users() {
        // given
        String name = "Test User";
        
        User user1 = User.withId(
            1L,
            "test1@example.com",
            name,
            null,
            1L,
            LocalDateTime.now().minusDays(1),
            null,
            null
        );
        
        User user2 = User.withId(
            2L,
            "test2@example.com",
            name,
            null,
            1L,
            LocalDateTime.now().minusDays(1),
            null,
            null
        );
        
        when(findUserUseCase.findByName(any())).thenReturn(Arrays.asList(user1, user2));

        // when & then
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .param("name", name)
            .when()
            .get("/api/users/search")
            .then()
            .statusCode(200)
            .body("size()", org.hamcrest.Matchers.equalTo(2))
            .body("[0].email", org.hamcrest.Matchers.equalTo("test1@example.com"))
            .body("[1].email", org.hamcrest.Matchers.equalTo("test2@example.com"));
    }

    @Test
    @DisplayName("findUsersByName 메서드는 해당 이름의 사용자가 없을 경우 빈 배열을 반환해야 한다")
    @FTMockUser
    void given_non_existent_name_when_find_users_by_name_then_return_empty_array() {
        // given
        String name = "Non Existent Name";
        
        when(findUserUseCase.findByName(any())).thenReturn(Collections.emptyList());

        // when & then
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .param("name", name)
            .when()
            .get("/api/users/search")
            .then()
            .statusCode(200)
            .body("size()", org.hamcrest.Matchers.equalTo(0));
    }

    @Test
    @DisplayName("findAllUsers 메서드는 모든 사용자를 조회해야 한다")
    @FTMockUser
    void when_find_all_users_then_return_all_users() {
        // given
        User user1 = User.withId(
            1L,
            "user1@example.com",
            "User One",
            null,
            1L,
            LocalDateTime.now().minusDays(1),
            null,
            null
        );
        
        User user2 = User.withId(
            2L,
            "user2@example.com",
            "User Two",
            null,
            1L,
            LocalDateTime.now().minusDays(1),
            null,
            null
        );
        
        when(findUserUseCase.findAll()).thenReturn(Arrays.asList(user1, user2));

        // when & then
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/users")
            .then()
            .statusCode(200)
            .body("size()", org.hamcrest.Matchers.equalTo(2))
            .body("[0].email", org.hamcrest.Matchers.equalTo("user1@example.com"))
            .body("[1].email", org.hamcrest.Matchers.equalTo("user2@example.com"));
    }

    @Test
    @DisplayName("findCurrentUser 메서드는 현재 로그인한 사용자 정보를 조회해야 한다")
    @FTMockUser(id = 1L, email = "ftuser@email.com")
    void when_find_current_user_then_return_current_user() {
        // given
        User user = User.withId(
            1L,
            "ftuser@email.com",
            "FT User",
            "http://example.com/profile.jpg",
            1L,
            LocalDateTime.now().minusDays(1),
            null,
            null
        );
        
        when(findUserUseCase.findById(anyLong())).thenReturn(Optional.of(user));

        // when & then
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/users/me")
            .then()
            .statusCode(200)
            .body("id", org.hamcrest.Matchers.equalTo(1))
            .body("email", org.hamcrest.Matchers.equalTo("ftuser@email.com"));
    }
    
    @Test
    @DisplayName("findCurrentUser 메서드는 현재 사용자를 찾을 수 없을 경우 404 상태코드를 반환해야 한다")
    @FTMockUser(id = 1L, email = "ftuser@email.com")
    void when_current_user_not_found_then_return_404() {
        // given
        when(findUserUseCase.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/users/me")
            .then()
            .statusCode(404);
    }
}
