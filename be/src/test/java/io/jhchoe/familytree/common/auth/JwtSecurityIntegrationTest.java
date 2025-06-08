package io.jhchoe.familytree.common.auth;

import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.util.JwtTokenUtil;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("[통합 테스트] JWT 보안 시스템")
class JwtSecurityIntegrationTest extends AcceptanceTestBase {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    @DisplayName("유효한 JWT 토큰으로 보호된 API에 접근할 수 있다")
    void access_protected_api_with_valid_jwt_token() {
        // given - 유효한 JWT 토큰 생성
        String validToken = createValidJwtToken();

        // when & then - 실제 HTTP 요청으로 JWT 필터 통과 테스트
        given()
            .header("Authorization", "Bearer " + validToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .get("/api/user/me")
        .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("email", notNullValue())
            .body("name", notNullValue());
    }

    @Test
    @DisplayName("JWT 토큰 없이 보호된 API에 접근하면 401 응답을 받는다")
    void return_401_when_accessing_protected_api_without_token() {
        // when & then - 토큰 없이 보호된 API 접근
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .get("/api/user/me")
        .then()
            .statusCode(401)
            .body("code", equalTo("A001"))
            .body("message", equalTo("인증 실패."));
    }

    @Test
    @DisplayName("만료된 JWT 토큰으로 API에 접근하면 401 응답을 받는다")
    void return_401_when_accessing_api_with_expired_token() {
        // given - 만료된 JWT 토큰 생성
        String expiredToken = createExpiredJwtToken();

        // when & then - 만료된 토큰으로 API 접근
        given()
            .header("Authorization", "Bearer " + expiredToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .get("/api/user/me")
        .then()
            .statusCode(401)
            .body("code", equalTo("A004")) // EXPIRED_TOKEN
            .body("message", equalTo("토큰이 만료되었습니다. 다시 로그인해주세요."));
    }

    @Test
    @DisplayName("잘못된 형식의 JWT 토큰으로 API에 접근하면 401 응답을 받는다")
    void return_401_when_accessing_api_with_invalid_token_format() {
        // given - 잘못된 형식의 토큰
        String invalidToken = "invalid.token.format";

        // when & then - 잘못된 토큰으로 API 접근
        given()
            .header("Authorization", "Bearer " + invalidToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .get("/api/user/me")
        .then()
            .statusCode(401)
            .body("code", equalTo("A005")) // INVALID_TOKEN_FORMAT
            .body("message", equalTo("유효하지 않은 토큰 형식입니다."));
    }

    @Test
    @DisplayName("Bearer 접두사 없는 토큰으로 API에 접근하면 401 응답을 받는다")
    void return_401_when_accessing_api_without_bearer_prefix() {
        // given - Bearer 접두사 없는 토큰
        String validToken = createValidJwtToken();

        // when & then - Bearer 접두사 없이 API 접근
        given()
            .header("Authorization", validToken) // Bearer 없음
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .get("/api/user/me")
        .then()
            .statusCode(401);
    }

    @Test
    @DisplayName("공개 엔드포인트는 토큰 없이도 접근할 수 있다")
    void access_public_endpoints_without_token() {
        // when & then - 공개 엔드포인트 접근
        given()
        .when()
            .get("/")
        .then()
            .statusCode(200);

        given()
        .when()
            .get("/login")
        .then()
            .statusCode(200);

        given()
        .when()
            .get("/docs/index.html")
        .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("5분 TTL 설정이 올바르게 적용되는지 확인한다")
    void verify_5_minute_ttl_configuration() {
        // given - JWT 토큰 생성
        String token = createValidJwtToken();

        // when - 토큰에서 만료 시간 추출
        Long expirationTime = jwtTokenUtil.extractExpiration(token).getTime();
        Long issuedTime = System.currentTimeMillis(); // 현재 시간으로 근사치 계산
        
        // then - 5분(300초) TTL 확인
        long ttlSeconds = (expirationTime - issuedTime) / 1000;
        
        // 약간의 오차를 고려하여 295-305초 범위로 검증
        assertThat(ttlSeconds)
            .as("JWT Access Token TTL이 5분(300초)으로 설정되어야 함")
            .isBetween(295L, 305L);
    }

    /**
     * 테스트용 유효한 JWT 토큰을 생성합니다.
     */
    private String createValidJwtToken() {
        // 실제 FTUser 생성하여 토큰 생성
        FTUser testUser = FTUser.ofJwtUser(1L, "테스트사용자", "test@example.com", "USER");
        return jwtTokenUtil.generateAccessToken(testUser);
    }

    /**
     * 테스트용 만료된 JWT 토큰을 생성합니다.
     */
    private String createExpiredJwtToken() {
        // 과거 시간으로 설정하여 즉시 만료되는 토큰 생성
        FTUser testUser = FTUser.ofJwtUser(1L, "테스트사용자", "test@example.com", "USER");
        
        // 현재 시간보다 1초 전에 만료되도록 설정 (즉시 만료)
        long expiredTime = System.currentTimeMillis() - 1000; // 1초 전
        
        // JWT 토큰을 수동으로 생성 (만료 시간을 과거로 설정)
        return io.jsonwebtoken.Jwts.builder()
            .setSubject(testUser.getId().toString())
            .claim("email", testUser.getEmail())
            .claim("name", testUser.getName())
            .claim("role", "USER")
            .setIssuer("family-tree-test-app")
            .setIssuedAt(new java.util.Date(expiredTime - 300000)) // 5분 전 발급
            .setExpiration(new java.util.Date(expiredTime)) // 1초 전 만료
            .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, "TestFamilyTree2025!TokenKeyJwtSigningSecureForTesting123456789".getBytes())
            .compact();
    }
}
