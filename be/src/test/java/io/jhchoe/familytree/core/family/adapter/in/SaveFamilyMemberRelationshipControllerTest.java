package io.jhchoe.familytree.core.family.adapter.in;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.in.request.SaveFamilyMemberRelationshipRequest;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DisplayName("[Acceptance Test] SaveFamilyMemberRelationshipControllerTest")
class SaveFamilyMemberRelationshipControllerTest extends AcceptanceTestBase {

    @MockitoBean
    private SaveFamilyMemberRelationshipUseCase saveFamilyMemberRelationshipUseCase;

    @MockitoBean
    private FindFamilyMemberRelationshipUseCase findFamilyMemberRelationshipUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockOAuth2User
    @DisplayName("save 메서드는 유효한 요청을 받으면 관계를 정의하고 상태 코드 200을 반환해야 한다")
    void given_valid_request_when_save_relationship_then_return_status_200() throws JsonProcessingException {
        // given
        Long id = 3L;
        Long familyId = 1L;
        Long fromMemberId = 1L;
        Long toMemberId = 2L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.FATHER;
        String description = "부모 관계";

        SaveFamilyMemberRelationshipRequest request = new SaveFamilyMemberRelationshipRequest(
            familyId,
            toMemberId,
            relationshipType,
            null,
            description
        );

        when(saveFamilyMemberRelationshipUseCase.save(any()))
            .thenReturn(id);

        FamilyMemberRelationship relationship = FamilyMemberRelationship.withId(
            id,
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            null,
            description,
            100L,
            LocalDateTime.now(),
            100L,
            LocalDateTime.now()
        );

        when(findFamilyMemberRelationshipUseCase.find(any()))
            .thenReturn(relationship);

        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(objectMapper.writeValueAsString(request))
            .when()
            .post("/api/families/{familyId}/members/{toMemberId}/relationships", familyId, toMemberId)
            .then()
            .statusCode(201)
            .header("Location", equalTo(
                String.format("/api/families/%d/members/%d/relationships/%d",
                    familyId, toMemberId, id
                )))
            .body("id", equalTo(id.intValue()));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("save 메서드는 CUSTOM 타입 요청을 받으면 커스텀 관계를 정의하고 상태 코드 200을 반환해야 한다")
    void given_custom_type_request_when_save_relationship_then_return_status_200() throws JsonProcessingException {
        // given
        Long id = 3L;
        Long familyId = 1L;
        Long fromMemberId = 1L;
        Long toMemberId = 2L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.CUSTOM;
        String customRelationship = "친가 쪽 큰아버지";
        String description = "아버지의 형제";

        SaveFamilyMemberRelationshipRequest request = new SaveFamilyMemberRelationshipRequest(
            familyId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        );

        when(saveFamilyMemberRelationshipUseCase.save(any()))
            .thenReturn(id);

        FamilyMemberRelationship relationship = FamilyMemberRelationship.withId(
            id,
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description,
            100L,
            LocalDateTime.now(),
            100L,
            LocalDateTime.now()
        );

        when(findFamilyMemberRelationshipUseCase.find(any()))
            .thenReturn(relationship);

        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(objectMapper.writeValueAsString(request))
            .when()
            .post("/api/families/{familyId}/members/{toMemberId}/relationships", familyId, toMemberId)
            .then()
            .statusCode(201)
            .header("Location", equalTo(
                String.format("/api/families/%d/members/%d/relationships/%d",
                    familyId, toMemberId, id
            )))
            .body("id", equalTo(id.intValue()));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("save 메서드는 유효하지 않은 familyId를 전달 받으면 상태 코드 400을 반환해야 한다")
    void given_invalid_family_id_when_save_relationship_then_return_status_400() throws JsonProcessingException {
        // given
        Long toMemberId = 1L;
        SaveFamilyMemberRelationshipRequest request = new SaveFamilyMemberRelationshipRequest(
            null,
            1L,
            FamilyMemberRelationshipType.FATHER,
            null,
            "설명"
        );

        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(objectMapper.writeValueAsString(request))
            .when()
            // RestAssured 에서 경로에 null을 입력하는 경우 NPE를 발생시키기 때문에 검증을 수행하기 위해
            // null이 아니면서 유효하지 않은 값을 임의의 문자열을 전달하였음.
            .post("/api/families/{familyId}/members/{toMemberId}/relationships", "not-a-number", toMemberId)
            .then()
            .statusCode(400);
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("save 메서드는 유효하지 않은 toMemberId를 전달받으면 상태 코드 400을 반환해야 한다")
    void given_invalid_to_member_id_when_save_relationship_then_return_status_400() throws JsonProcessingException {
        // given
        Long familyId = 1L;
        SaveFamilyMemberRelationshipRequest request = new SaveFamilyMemberRelationshipRequest(
            familyId,
            null, // toMemberId가 null
            null, // relationshipType이 null
            null,
            "설명"
        );

        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(objectMapper.writeValueAsString(request))
            .when()
            // RestAssured 에서 경로에 null을 입력하는 경우 NPE를 발생시키기 때문에 검증을 수행하기 위해
            // null이 아니면서 유효하지 않은 값을 임의의 문자열을 전달하였음.
            .post("/api/families/{familyId}/members/{toMemberId}/relationships", familyId, "not-a-number")
            .then()
            .statusCode(400);
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("save 메서드는 유효하지 않은 relationshipType을 전달받으면 상태 코드 400을 반환해야 한다")
    void given_invalid_relation_type_when_save_relationship_then_return_status_400() throws JsonProcessingException {
        // given
        Long familyId = 1L;
        Long toMemberId = 1L;
        SaveFamilyMemberRelationshipRequest request = new SaveFamilyMemberRelationshipRequest(
            familyId,
            toMemberId,
            null, // relationshipType이 null
            null,
            "설명"
        );

        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(objectMapper.writeValueAsString(request))
            .when()
            .post("/api/families/{familyId}/members/{toMemberId}/relationships", familyId, toMemberId)
            .then()
            .statusCode(400);
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("save 메서드는 유효하지 않은 customRelationship을 전달받으면 상태 코드 400을 반환해야 한다")
    void given_invalid_customRelationship_when_save_relationship_then_return_status_400() throws JsonProcessingException {
        // given
        Long familyId = 1L;
        Long toMemberId = 1L;
        SaveFamilyMemberRelationshipRequest request = new SaveFamilyMemberRelationshipRequest(
            familyId,
            toMemberId,
            FamilyMemberRelationshipType.FATHER,
            "C".repeat(51), // > 50
            "설명"
        );

        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(objectMapper.writeValueAsString(request))
            .when()
            .post("/api/families/{familyId}/members/{toMemberId}/relationships", familyId, toMemberId)
            .then()
            .statusCode(400);
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("save 메서드는 유효하지 않은 description을 전달받으면 상태 코드 400을 반환해야 한다")
    void given_invalid_description_when_save_relationship_then_return_status_400() throws JsonProcessingException {
        // given
        Long familyId = 1L;
        Long toMemberId = 1L;
        SaveFamilyMemberRelationshipRequest request = new SaveFamilyMemberRelationshipRequest(
            familyId,
            toMemberId,
            FamilyMemberRelationshipType.FATHER,
            null,
            "D".repeat(201) // > 200
        );

        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(objectMapper.writeValueAsString(request))
            .when()
            .post("/api/families/{familyId}/members/{toMemberId}/relationships", familyId, toMemberId)
            .then()
            .statusCode(400);
    }
}
