package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@DisplayName("[Acceptance Test] ModifyFamilyMemberRoleControllerTest")
class ModifyFamilyMemberRoleControllerTest extends AcceptanceTestBase {

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
    @DisplayName("인증되지 않은 사용자가 역할 변경을 요청하면 401 Unauthorized를 반환합니다")
    void modify_role_returns_401_when_unauthenticated() {
        // given
        Long familyId = 1L;
        Long memberId = 1L;

        // when & then
        given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
                {
                    "role": "ADMIN"
                }
                """)
        .when()
            .put("/api/families/{familyId}/members/{memberId}/role", familyId, memberId)
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
