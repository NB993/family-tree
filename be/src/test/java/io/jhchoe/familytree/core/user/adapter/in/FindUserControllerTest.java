package io.jhchoe.familytree.core.user.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.domain.UserRole;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.config.FTMockUser;
import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.user.application.port.in.FindUserByNameQuery;
import io.jhchoe.familytree.core.user.application.port.in.FindUserUseCase;
import io.jhchoe.familytree.core.user.domain.User;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("[Acceptance Test] FindUserController")
class FindUserControllerTest extends AcceptanceTestBase {

    @MockitoBean
    private FindUserUseCase findUserUseCase;

    @Test
    @WithMockOAuth2User
    @DisplayName("이름으로 사용자 조회 성공")
    void given_valid_name_when_find_users_by_name_then_return_user_list() {
        // given
        String name = "홍길동";
        List<User> mockUsers = new ArrayList<>();
        
        User user = User.withId(
            1L, 
            "test@example.com", 
            "홍길동", 
            "profile.jpg",
            AuthenticationType.OAUTH2,
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false,
                1L,
                LocalDateTime.now(),
                1L,
                LocalDateTime.now()
        );
        mockUsers.add(user);
        
        when(findUserUseCase.findByName(any(FindUserByNameQuery.class)))
            .thenReturn(mockUsers);

        // when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .param("name", name)
            .when()
            .get("/api/users/search")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("$", hasSize(1));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("이름으로 사용자 조회 시 결과가 없으면 404 응답")
    void given_invalid_name_when_find_users_by_name_then_return_not_found() {
        // given
        String name = "존재하지않는이름";
        
        when(findUserUseCase.findByName(any(FindUserByNameQuery.class)))
            .thenThrow(FTException.NOT_FOUND);

        // when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .param("name", name)
            .when()
            .get("/api/users/search")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }
    
    @Test
    @WithMockOAuth2User
    @DisplayName("이름 파라미터 없이 요청 시 400 응답")
    void given_no_name_parameter_when_find_users_by_name_then_return_bad_request() {
        // when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/users/search")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
