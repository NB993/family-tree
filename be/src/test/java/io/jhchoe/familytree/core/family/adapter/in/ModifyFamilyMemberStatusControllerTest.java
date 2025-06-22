package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@DisplayName("[Acceptance Test] ModifyFamilyMemberStatusControllerTest")
class ModifyFamilyMemberStatusControllerTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @AfterEach
    void tearDown() {
        familyMemberJpaRepository.deleteAll();
        familyJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 상태 변경을 요청하면 401 Unauthorized를 반환합니다")
    void modify_status_returns_401_when_unauthenticated() {
        // given
        Long familyId = 1L;
        Long memberId = 1L;

        // when & then
        given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
                {
                    "status": "SUSPENDED",
                    "reason": "인증 없는 요청 테스트"
                }
                """)
        .when()
            .put("/api/families/{familyId}/members/{memberId}/status", familyId, memberId)
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
