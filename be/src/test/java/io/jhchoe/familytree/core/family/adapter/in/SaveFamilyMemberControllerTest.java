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
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@DisplayName("[Acceptance Test] SaveFamilyMemberControllerTest")
class SaveFamilyMemberControllerTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("필수 필드만으로 구성원을 등록하면 201 Created를 반환합니다")
        void save_returns_201_when_valid_request_with_required_fields() throws JsonProcessingException {
            // given
            Family family = Family.newFamily("테스트 가족", "설명", null, true);
            FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
            Long familyId = savedFamily.getId();

            // 현재 사용자를 가족 구성원으로 등록
            FamilyMember currentUserMember = FamilyMember.newMember(familyId, 1L, "현재 사용자", null, null);
            familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(currentUserMember));

            Map<String, Object> request = Map.of(
                "name", "홍길동"
            );

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/families/{familyId}/members", familyId)
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("id", notNullValue());
        }

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("모든 필드를 포함하여 구성원을 등록하면 201 Created를 반환합니다")
        void save_returns_201_when_valid_request_with_all_fields() throws JsonProcessingException {
            // given
            Family family = Family.newFamily("테스트 가족", "설명", null, true);
            FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
            Long familyId = savedFamily.getId();

            // 현재 사용자를 가족 구성원으로 등록
            FamilyMember currentUserMember = FamilyMember.newMember(familyId, 1L, "현재 사용자", null, null);
            familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(currentUserMember));

            Map<String, Object> request = Map.of(
                "name", "홍길동",
                "birthday", "1990-01-15T00:00:00",
                "relationshipType", "FATHER"
            );

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/families/{familyId}/members", familyId)
                .then()
                .statusCode(201)
                .body("id", notNullValue());
        }

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("CUSTOM 관계 타입과 사용자 정의 관계명으로 구성원을 등록하면 201 Created를 반환합니다")
        void save_returns_201_when_custom_relationship() throws JsonProcessingException {
            // given
            Family family = Family.newFamily("테스트 가족", "설명", null, true);
            FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
            Long familyId = savedFamily.getId();

            // 현재 사용자를 가족 구성원으로 등록
            FamilyMember currentUserMember = FamilyMember.newMember(familyId, 1L, "현재 사용자", null, null);
            familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(currentUserMember));

            Map<String, Object> request = Map.of(
                "name", "홍길동",
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
                .post("/api/families/{familyId}/members", familyId)
                .then()
                .statusCode(201)
                .body("id", notNullValue());
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureCases {

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("이름이 빈 문자열이면 400 Bad Request를 반환합니다")
        void save_returns_400_when_name_is_blank() throws JsonProcessingException {
            // given
            Long familyId = 1L;
            Map<String, Object> request = Map.of(
                "name", ""
            );

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/families/{familyId}/members", familyId)
                .then()
                .statusCode(400);
        }

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("CUSTOM 타입인데 customRelationship이 없으면 400 Bad Request를 반환합니다")
        void save_returns_400_when_custom_relationship_missing() throws JsonProcessingException {
            // given
            Long familyId = 1L;
            Map<String, Object> request = Map.of(
                "name", "홍길동",
                "relationshipType", "CUSTOM"
            );

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/families/{familyId}/members", familyId)
                .then()
                .statusCode(400);
        }

        @Test
        @WithMockOAuth2User(id = 999L)
        @DisplayName("가족 구성원이 아닌 사용자가 등록하면 404 Not Found를 반환합니다")
        void save_returns_404_when_not_family_member() throws JsonProcessingException {
            // given
            Family family = Family.newFamily("테스트 가족", "설명", null, true);
            FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
            Long familyId = savedFamily.getId();

            Map<String, Object> request = Map.of(
                "name", "홍길동"
            );

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/families/{familyId}/members", familyId)
                .then()
                .statusCode(404);
        }

        @Test
        @WithMockOAuth2User(id = 1L)
        @DisplayName("존재하지 않는 가족에 등록하면 404 Not Found를 반환합니다")
        void save_returns_404_when_family_not_found() throws JsonProcessingException {
            // given
            Long nonExistentFamilyId = 999L;
            Map<String, Object> request = Map.of(
                "name", "홍길동"
            );

            // when & then
            RestAssuredMockMvc
                .given()
                .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/families/{familyId}/members", nonExistentFamilyId)
                .then()
                .statusCode(404);
        }
    }
}
