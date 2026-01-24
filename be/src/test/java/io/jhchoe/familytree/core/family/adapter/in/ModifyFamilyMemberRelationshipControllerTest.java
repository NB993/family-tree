package io.jhchoe.familytree.core.family.adapter.in;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@DisplayName("[Acceptance Test] ModifyFamilyMemberRelationshipControllerTest")
class ModifyFamilyMemberRelationshipControllerTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        familyMemberJpaRepository.deleteAll();
        familyJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("관계 타입을 변경하면 200 OK를 반환합니다")
        void modify_relationship_returns_200_when_valid_request() throws JsonProcessingException {
            // given
            Family family = Family.newFamily("테스트 가족", "설명", null, true);
            FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
            Long familyId = savedFamily.getId();

            // 현재 사용자를 가족 구성원으로 등록
            FamilyMember currentUserMember = FamilyMember.newMember(familyId, 1L, "현재 사용자", null, null, null);
            familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(currentUserMember));

            // 대상 구성원 등록
            FamilyMember targetMember = FamilyMember.newMember(familyId, 2L, "대상 구성원", null, null, null);
            FamilyMemberJpaEntity savedTargetMember = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(targetMember));
            Long memberId = savedTargetMember.getId();

            Map<String, Object> request = Map.of(
                "relationshipType", "FATHER"
            );

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .patch("/api/families/{familyId}/members/{memberId}/relationship", familyId, memberId)
                .then()
                .statusCode(200)
                .body("id", equalTo(memberId.intValue()));
        }

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("CUSTOM 타입으로 관계를 변경하면 200 OK를 반환합니다")
        void modify_relationship_returns_200_when_custom_type() throws JsonProcessingException {
            // given
            Family family = Family.newFamily("테스트 가족", "설명", null, true);
            FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
            Long familyId = savedFamily.getId();

            // 현재 사용자를 가족 구성원으로 등록
            FamilyMember currentUserMember = FamilyMember.newMember(familyId, 1L, "현재 사용자", null, null, null);
            familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(currentUserMember));

            // 대상 구성원 등록
            FamilyMember targetMember = FamilyMember.newMember(familyId, 2L, "대상 구성원", null, null, null);
            FamilyMemberJpaEntity savedTargetMember = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(targetMember));
            Long memberId = savedTargetMember.getId();

            Map<String, Object> request = Map.of(
                "relationshipType", "CUSTOM",
                "customRelationship", "외할아버지"
            );

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .patch("/api/families/{familyId}/members/{memberId}/relationship", familyId, memberId)
                .then()
                .statusCode(200)
                .body("id", equalTo(memberId.intValue()));
        }

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("customRelationship이 50자 이내면 200 OK를 반환합니다")
        void modify_relationship_returns_200_when_custom_relationship_is_50_chars() throws JsonProcessingException {
            // given
            Family family = Family.newFamily("테스트 가족", "설명", null, true);
            FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
            Long familyId = savedFamily.getId();

            // 현재 사용자를 가족 구성원으로 등록
            FamilyMember currentUserMember = FamilyMember.newMember(familyId, 1L, "현재 사용자", null, null, null);
            familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(currentUserMember));

            // 대상 구성원 등록
            FamilyMember targetMember = FamilyMember.newMember(familyId, 2L, "대상 구성원", null, null, null);
            FamilyMemberJpaEntity savedTargetMember = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(targetMember));
            Long memberId = savedTargetMember.getId();

            String fiftyChars = "가".repeat(50);
            Map<String, Object> request = Map.of(
                "relationshipType", "CUSTOM",
                "customRelationship", fiftyChars
            );

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .patch("/api/families/{familyId}/members/{memberId}/relationship", familyId, memberId)
                .then()
                .statusCode(200)
                .body("id", equalTo(memberId.intValue()));
        }
    }

    @Nested
    @DisplayName("인증 실패 케이스")
    class AuthenticationFailureCases {

        @Test
        @DisplayName("인증되지 않은 사용자가 관계 변경을 요청하면 401 Unauthorized를 반환합니다")
        void modify_relationship_returns_401_when_unauthenticated() {
            // given
            Long familyId = 1L;
            Long memberId = 1L;

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                    {
                        "relationshipType": "FATHER"
                    }
                    """)
                .when()
                .patch("/api/families/{familyId}/members/{memberId}/relationship", familyId, memberId)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Nested
    @DisplayName("권한/존재 실패 케이스")
    class AuthorizationAndExistenceFailureCases {

        @Test
        @WithMockOAuth2User(id = 999L)
        @DisplayName("가족 구성원이 아닌 사용자가 요청하면 403 Forbidden을 반환합니다")
        void modify_relationship_returns_403_when_not_family_member() throws JsonProcessingException {
            // given
            Family family = Family.newFamily("테스트 가족", "설명", null, true);
            FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
            Long familyId = savedFamily.getId();

            // 다른 사용자가 구성원으로 등록됨 (userId=1)
            FamilyMember otherMember = FamilyMember.newMember(familyId, 1L, "다른 사용자", null, null, null);
            FamilyMemberJpaEntity savedMember = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(otherMember));
            Long memberId = savedMember.getId();

            Map<String, Object> request = Map.of(
                "relationshipType", "FATHER"
            );

            // when & then (userId=999는 구성원이 아님)
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .patch("/api/families/{familyId}/members/{memberId}/relationship", familyId, memberId)
                .then()
                .statusCode(403);
        }

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("존재하지 않는 대상 구성원이면 404 Not Found를 반환합니다")
        void modify_relationship_returns_404_when_target_member_not_found() throws JsonProcessingException {
            // given
            Family family = Family.newFamily("테스트 가족", "설명", null, true);
            FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
            Long familyId = savedFamily.getId();

            // 현재 사용자를 가족 구성원으로 등록
            FamilyMember currentUserMember = FamilyMember.newMember(familyId, 1L, "현재 사용자", null, null, null);
            familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(currentUserMember));

            Long nonExistentMemberId = 9999L;
            Map<String, Object> request = Map.of(
                "relationshipType", "FATHER"
            );

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .patch("/api/families/{familyId}/members/{memberId}/relationship", familyId, nonExistentMemberId)
                .then()
                .statusCode(404);
        }

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("대상 구성원이 다른 가족 소속이면 404 Not Found를 반환합니다")
        void modify_relationship_returns_404_when_member_belongs_to_different_family() throws JsonProcessingException {
            // given
            // 가족 1
            Family family1 = Family.newFamily("가족1", "설명", null, true);
            FamilyJpaEntity savedFamily1 = familyJpaRepository.save(FamilyJpaEntity.from(family1));
            Long familyId1 = savedFamily1.getId();

            // 가족 2
            Family family2 = Family.newFamily("가족2", "설명", null, true);
            FamilyJpaEntity savedFamily2 = familyJpaRepository.save(FamilyJpaEntity.from(family2));
            Long familyId2 = savedFamily2.getId();

            // 현재 사용자를 가족1의 구성원으로 등록
            FamilyMember currentUserMember = FamilyMember.newMember(familyId1, 1L, "현재 사용자", null, null, null);
            familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(currentUserMember));

            // 대상 구성원을 가족2에 등록
            FamilyMember targetMember = FamilyMember.newMember(familyId2, 2L, "대상 구성원", null, null, null);
            FamilyMemberJpaEntity savedTargetMember = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(targetMember));
            Long memberId = savedTargetMember.getId();

            Map<String, Object> request = Map.of(
                "relationshipType", "FATHER"
            );

            // when & then (가족1에 대해 가족2 소속 멤버의 관계 변경 요청)
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .patch("/api/families/{familyId}/members/{memberId}/relationship", familyId1, memberId)
                .then()
                .statusCode(404);
        }
    }

    @Nested
    @DisplayName("유효성 검증 실패 케이스")
    class ValidationFailureCases {

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("relationshipType이 없으면 400 Bad Request를 반환합니다")
        void modify_relationship_returns_400_when_relationship_type_is_null() throws JsonProcessingException {
            // given
            Long familyId = 1L;
            Long memberId = 1L;

            Map<String, Object> request = new HashMap<>();
            request.put("relationshipType", null);

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .patch("/api/families/{familyId}/members/{memberId}/relationship", familyId, memberId)
                .then()
                .statusCode(400);
        }

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("CUSTOM 타입인데 customRelationship이 없으면 400 Bad Request를 반환합니다")
        void modify_relationship_returns_400_when_custom_type_without_custom_relationship() throws JsonProcessingException {
            // given
            Family family = Family.newFamily("테스트 가족", "설명", null, true);
            FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
            Long familyId = savedFamily.getId();

            // 현재 사용자를 가족 구성원으로 등록
            FamilyMember currentUserMember = FamilyMember.newMember(familyId, 1L, "현재 사용자", null, null, null);
            familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(currentUserMember));

            // 대상 구성원 등록
            FamilyMember targetMember = FamilyMember.newMember(familyId, 2L, "대상 구성원", null, null, null);
            FamilyMemberJpaEntity savedTargetMember = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(targetMember));
            Long memberId = savedTargetMember.getId();

            Map<String, Object> request = Map.of(
                "relationshipType", "CUSTOM"
            );

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .patch("/api/families/{familyId}/members/{memberId}/relationship", familyId, memberId)
                .then()
                .statusCode(400);
        }

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("CUSTOM 타입인데 customRelationship이 빈 문자열이면 400 Bad Request를 반환합니다")
        void modify_relationship_returns_400_when_custom_relationship_is_blank() throws JsonProcessingException {
            // given
            Family family = Family.newFamily("테스트 가족", "설명", null, true);
            FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
            Long familyId = savedFamily.getId();

            // 현재 사용자를 가족 구성원으로 등록
            FamilyMember currentUserMember = FamilyMember.newMember(familyId, 1L, "현재 사용자", null, null, null);
            familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(currentUserMember));

            // 대상 구성원 등록
            FamilyMember targetMember = FamilyMember.newMember(familyId, 2L, "대상 구성원", null, null, null);
            FamilyMemberJpaEntity savedTargetMember = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(targetMember));
            Long memberId = savedTargetMember.getId();

            Map<String, Object> request = Map.of(
                "relationshipType", "CUSTOM",
                "customRelationship", "   "
            );

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .patch("/api/families/{familyId}/members/{memberId}/relationship", familyId, memberId)
                .then()
                .statusCode(400);
        }

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("customRelationship이 50자를 초과하면 400 Bad Request를 반환합니다")
        void modify_relationship_returns_400_when_custom_relationship_exceeds_50_chars() throws JsonProcessingException {
            // given
            Long familyId = 1L;
            Long memberId = 1L;

            String fiftyOneChars = "가".repeat(51);
            Map<String, Object> request = Map.of(
                "relationshipType", "CUSTOM",
                "customRelationship", fiftyOneChars
            );

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .patch("/api/families/{familyId}/members/{memberId}/relationship", familyId, memberId)
                .then()
                .statusCode(400);
        }

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("잘못된 relationshipType이면 400 Bad Request를 반환합니다")
        void modify_relationship_returns_400_when_invalid_relationship_type() {
            // given
            Long familyId = 1L;
            Long memberId = 1L;

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("""
                    {
                        "relationshipType": "INVALID_TYPE"
                    }
                    """)
                .when()
                .patch("/api/families/{familyId}/members/{memberId}/relationship", familyId, memberId)
                .then()
                .statusCode(400);
        }
    }
}
