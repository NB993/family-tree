package io.jhchoe.familytree.core.family.adapter.in;

import static org.hamcrest.Matchers.*;

import io.jhchoe.familytree.common.auth.UserJpaEntity;
import io.jhchoe.familytree.common.auth.UserJpaRepository;
import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.domain.UserRole;
import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.user.domain.User;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.time.LocalDateTime;

@DisplayName("[Acceptance Test] FamilyControllerTest")
class SaveFamilyControllerTest extends AcceptanceTestBase {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository familyJpaRepository;

    @Autowired
    private io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository familyMemberJpaRepository;

    @AfterEach
    void tearDown() {
        // 테스트 종료 후 관련 데이터 모두 정리 (순서 중요: 외래키 제약조건 고려)
        familyMemberJpaRepository.deleteAll();
        familyJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Family 생성 요청 시 성공하면 201 상태코드를 반환한다")
    void test_save_family_success() {
        // given: 테스트용 사용자 생성 후 SecurityContext 설정
        Long userId = createTestUserAndGetId();
        
        // when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .postProcessors(SecurityMockMvcRequestPostProcessors.user(createMockPrincipal(userId)))
            .body("""
                {
                    "name": "family name",
                    "description": "family description",
                    "profileUrl": "https://example.com/profile.jpg"
                }
                """)
            .when()
            .post("/api/families")
            .then()
            .statusCode(201)
            .body("id", greaterThan(0));
    }

    @Test
    @DisplayName("Family 생성 요청 시 필수값인 name만 전송해도 성공한다")
    void test_save_family_success_with_only_name() {
        // given: 테스트용 사용자 생성 후 SecurityContext 설정
        Long userId = createTestUserAndGetId();
        
        // when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .postProcessors(SecurityMockMvcRequestPostProcessors.user(createMockPrincipal(userId)))
            .body("""
                {
                    "name": "family name"
                }
                """)
            .when()
            .post("/api/families")
            .then()
            .statusCode(201)
            .body("id", greaterThan(0));
    }

    /**
     * 테스트용 User를 생성하고 생성된 User의 ID를 반환합니다.
     */
    private Long createTestUserAndGetId() {
        LocalDateTime now = LocalDateTime.now();
        
        // 신규 사용자로 생성 (ID 자동 생성)
        User testUser = User.newUser(
            "test@example.com",
            "테스트사용자",
            "https://example.com/test-profile.jpg",
            null, // kakaoId
            AuthenticationType.OAUTH2,
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false,
            null // birthday
        );
        
        UserJpaEntity userEntity = UserJpaEntity.ofOAuth2User(testUser);
        UserJpaEntity savedUser = userJpaRepository.saveAndFlush(userEntity);
        
        System.out.println("Created test user with auto-generated ID: " + savedUser.getId());
        return savedUser.getId();
    }

    /**
     * 생성된 사용자 ID를 사용하는 MockUser Principal을 생성합니다.
     */
    private io.jhchoe.familytree.common.auth.domain.FTUser createMockPrincipal(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return io.jhchoe.familytree.common.auth.domain.FTUser.ofOAuth2User(
            userId,
            "테스트사용자",
            "test@example.com",
            io.jhchoe.familytree.common.auth.domain.OAuth2Provider.GOOGLE,
            java.util.Map.of(
                "sub", String.valueOf(userId),
                "name", "테스트사용자",
                "email", "test@example.com"
            )
        );
    }

    @WithMockOAuth2User
    @ValueSource(strings = {"", " ", "\\t", "\\n"})
    @ParameterizedTest
    @DisplayName("Family 생성 시 이름을 입력하지 않으면 400 상태코드와 유효성 검증 오류를 반환한다")
    void test_save_family_fail_when_name_is_empty(String emptyName) {
        // given & when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .body(String.format("""
                {
                    "name": "%s"
                }
                """, emptyName))
            .when()
            .post("/api/families")
            .then()
            .body("validations.size()", greaterThan(0)) // validations 배열 크기 확인
            .body("validations.field", hasItem("name")) // validations 목록 중 field: "name" 존재
            .body("validations.find { it.field == 'name' }.message",
                not(emptyOrNullString())) // 해당 필드의 message가 비어있지 않음
            .statusCode(400);
    }
    
    @WithMockOAuth2User
    @Test
    @DisplayName("Family 생성 시 프로필 URL이 http:// 또는 https://로 시작하지 않으면 400 상태코드를 반환한다")
    void test_save_family_fail_when_profile_url_is_invalid() {
        // given & when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .body("""
                {
                    "name": "family name",
                    "profileUrl": "invalid-url"
                }
                """)
            .when()
            .post("/api/families")
            .then()
            .statusCode(400);
    }
    
    @Test
    @DisplayName("Family 생성 시 인증되지 않은 사용자는 401 상태코드를 반환한다")
    void test_save_family_fail_when_user_is_not_authenticated() {
        // given & when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .body("""
                {
                    "name": "family name"
                }
                """)
            .when()
            .post("/api/families")
            .then()
            .statusCode(401);
    }
}
