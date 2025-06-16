package io.jhchoe.familytree.core.family.adapter.in;

import static org.hamcrest.Matchers.equalTo;

import io.jhchoe.familytree.common.auth.UserJpaEntity;
import io.jhchoe.familytree.common.auth.UserJpaRepository;
import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.domain.UserRole;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.user.domain.User;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.time.LocalDateTime;

@DisplayName("[Acceptance Test] CheckFamilyNameControllerTest")
class CheckFamilyNameControllerTest extends AcceptanceTestBase {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @AfterEach
    void tearDown() {
        // 테스트 종료 후 관련 데이터 모두 정리
        familyJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("새로운 가족명은 available true를 반환한다")
    void should_return_available_true_when_family_name_is_new() {
        // given
        String familyName = "새로운가족명";
        Long userId = createTestUserAndGetId();
        
        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.user(createMockPrincipal(userId)))
            .when()
            .get("/api/families/check-name?name={name}", familyName)
            .then()
            .statusCode(200)
            .body("available", equalTo(true))
            .body("message", equalTo("사용 가능한 가족명입니다"));
    }

    @Test
    @DisplayName("이미 존재하는 가족명은 available false를 반환한다")
    void should_return_available_false_when_family_name_already_exists() {
        // given
        String existingFamilyName = "기존가족명";
        Long userId = createTestUserAndGetId();
        createTestFamily(existingFamilyName);
        
        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.user(createMockPrincipal(userId)))
            .when()
            .get("/api/families/check-name?name={name}", existingFamilyName)
            .then()
            .statusCode(200)
            .body("available", equalTo(false))
            .body("message", equalTo("이미 사용 중인 가족명입니다"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("가족명이 빈 문자열이거나 공백만 있는 경우 400 상태코드를 반환한다")
    void should_return_bad_request_when_family_name_is_empty_or_whitespace(String invalidName) {
        // given
        Long userId = createTestUserAndGetId();
        
        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.user(createMockPrincipal(userId)))
            .when()
            .get("/api/families/check-name?name={name}", invalidName)
            .then()
            .statusCode(400)
            .body("available", equalTo(false));
    }

    @Test
    @DisplayName("가족명이 20자를 초과하는 경우 400 상태코드를 반환한다")
    void should_return_bad_request_when_family_name_exceeds_max_length() {
        // given
        String longFamilyName = "a".repeat(21); // 21자
        Long userId = createTestUserAndGetId();
        
        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.user(createMockPrincipal(userId)))
            .when()
            .get("/api/families/check-name?name={name}", longFamilyName)
            .then()
            .statusCode(400)
            .body("available", equalTo(false))
            .body("message", equalTo("가족명은 20자를 초과할 수 없습니다"));
    }

    @Test
    @DisplayName("정확히 20자인 가족명은 정상적으로 처리된다")
    void should_process_family_name_with_max_length_successfully() {
        // given
        String maxLengthFamilyName = "a".repeat(20); // 정확히 20자
        Long userId = createTestUserAndGetId();
        
        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.user(createMockPrincipal(userId)))
            .when()
            .get("/api/families/check-name?name={name}", maxLengthFamilyName)
            .then()
            .statusCode(200)
            .body("available", equalTo(true))
            .body("message", equalTo("사용 가능한 가족명입니다"));
    }

    @Test
    @DisplayName("name 파라미터가 없는 경우 400 상태코드를 반환한다")
    void should_return_bad_request_when_name_parameter_is_missing() {
        // given
        Long userId = createTestUserAndGetId();
        
        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.user(createMockPrincipal(userId)))
            .when()
            .get("/api/families/check-name")
            .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("삭제된 가족과 동일한 이름은 사용 가능하다")
    void should_return_available_true_when_family_name_belongs_to_deleted_family() {
        // given
        String familyName = "삭제된가족명";
        Long userId = createTestUserAndGetId();
        FamilyJpaEntity deletedFamily = createTestFamily(familyName);
        deletedFamily.setDeleted(true); // 소프트 딜리트 실행
        familyJpaRepository.save(deletedFamily);
        
        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.user(createMockPrincipal(userId)))
            .when()
            .get("/api/families/check-name?name={name}", familyName)
            .then()
            .statusCode(200)
            .body("available", equalTo(true))
            .body("message", equalTo("사용 가능한 가족명입니다"));
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 401 상태코드를 반환한다")
    void should_return_unauthorized_when_user_is_not_authenticated() {
        // given
        String familyName = "테스트가족명";
        
        // when & then
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/check-name?name={name}", familyName)
            .then()
            .statusCode(401);
    }

    /**
     * 테스트용 Family를 생성하고 반환합니다.
     */
    private FamilyJpaEntity createTestFamily(String familyName) {
        Long testUserId = createTestUserAndGetId();
        LocalDateTime now = LocalDateTime.now();
        
        Family family = Family.newFamily(
            familyName,
            "테스트 가족 설명",
            "https://example.com/test-family.jpg",
            true
        );
        
        FamilyJpaEntity familyEntity = FamilyJpaEntity.from(family);
        // JPA Entity의 audit 필드들은 자동으로 설정되므로 직접 설정하지 않음
        
        return familyJpaRepository.save(familyEntity);
    }

    /**
     * 테스트용 User를 생성하고 생성된 User의 ID를 반환합니다.
     */
    private Long createTestUserAndGetId() {
        LocalDateTime now = LocalDateTime.now();
        
        User testUser = User.newUser(
            "test@example.com",
            "테스트사용자",
            "https://example.com/test-profile.jpg",
            AuthenticationType.OAUTH2,
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false
        );
        
        UserJpaEntity userEntity = UserJpaEntity.ofOAuth2User(testUser);
        UserJpaEntity savedUser = userJpaRepository.saveAndFlush(userEntity);
        
        return savedUser.getId();
    }

    /**
     * 생성된 사용자 ID를 사용하는 MockUser Principal을 생성합니다.
     */
    private io.jhchoe.familytree.common.auth.domain.FTUser createMockPrincipal(Long userId) {
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
}
