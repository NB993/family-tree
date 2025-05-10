package io.jhchoe.familytree.core.relationship.adapter.in;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.config.FTMockUser;
import io.jhchoe.familytree.core.family.application.port.out.LoadFamilyPort;
import io.jhchoe.familytree.core.relationship.adapter.in.request.DefineFamilyRelationshipRequest;
import io.jhchoe.familytree.core.relationship.application.port.in.DefineFamilyRelationshipUseCase;
import io.jhchoe.familytree.core.relationship.application.port.in.FindFamilyRelationshipUseCase;
import io.jhchoe.familytree.core.relationship.domain.FamilyRelationship;
import io.jhchoe.familytree.core.relationship.domain.FamilyRelationshipType;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@DisplayName("[Acceptance Test] FamilyRelationshipController")
class FamilyRelationshipControllerTest extends AcceptanceTestBase {

    @MockBean
    private DefineFamilyRelationshipUseCase defineFamilyRelationshipUseCase;

    @MockBean
    private FindFamilyRelationshipUseCase findFamilyRelationshipUseCase;

    @MockBean
    private LoadFamilyPort loadFamilyPort;

    @Test
    @FTMockUser
    @DisplayName("defineRelationship 메서드는 유효한 요청을 받으면 관계를 정의하고 상태 코드 200을 반환해야 한다")
    void given_valid_request_when_define_relationship_then_return_status_200() {
        // given
        Long memberId = 1L;
        Long toMemberId = 2L;
        FamilyRelationshipType relationshipType = FamilyRelationshipType.PARENT;
        String description = "부모 관계";
        Long relationshipId = 3L;
        
        DefineFamilyRelationshipRequest request = new DefineFamilyRelationshipRequest(
            toMemberId,
            relationshipType,
            null,
            description
        );
        
        when(defineFamilyRelationshipUseCase.defineRelationship(any()))
            .thenReturn(relationshipId);
        
        FamilyRelationship relationship = FamilyRelationship.withId(
            relationshipId,
            1L, // 임시 familyId
            memberId,
            toMemberId,
            relationshipType,
            null,
            description,
            100L,
            LocalDateTime.now(),
            100L,
            LocalDateTime.now()
        );
        
        when(findFamilyRelationshipUseCase.findRelationship(any()))
            .thenReturn(Optional.of(relationship));

        // when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .post("/api/family-tree/members/{memberId}/relationships", memberId)
            .then()
            .statusCode(200)
            .body("id", equalTo(relationshipId.intValue()))
            .body("fromMemberId", equalTo(memberId.intValue()))
            .body("toMemberId", equalTo(toMemberId.intValue()))
            .body("relationshipType", equalTo(relationshipType.name()))
            .body("relationshipDisplayName", equalTo(relationshipType.getDisplayName()))
            .body("description", equalTo(description));
    }

    @Test
    @FTMockUser
    @DisplayName("defineRelationship 메서드는 CUSTOM 타입 요청을 받으면 커스텀 관계를 정의하고 상태 코드 200을 반환해야 한다")
    void given_custom_type_request_when_define_relationship_then_return_status_200() {
        // given
        Long memberId = 1L;
        Long toMemberId = 2L;
        FamilyRelationshipType relationshipType = FamilyRelationshipType.CUSTOM;
        String customRelationship = "친가 쪽 큰아버지";
        String description = "아버지의 형제";
        Long relationshipId = 3L;
        
        DefineFamilyRelationshipRequest request = new DefineFamilyRelationshipRequest(
            toMemberId,
            relationshipType,
            customRelationship,
            description
        );
        
        when(defineFamilyRelationshipUseCase.defineRelationship(any()))
            .thenReturn(relationshipId);
        
        FamilyRelationship relationship = FamilyRelationship.withId(
            relationshipId,
            1L, // 임시 familyId
            memberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description,
            100L,
            LocalDateTime.now(),
            100L,
            LocalDateTime.now()
        );
        
        when(findFamilyRelationshipUseCase.findRelationship(any()))
            .thenReturn(Optional.of(relationship));

        // when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .post("/api/family-tree/members/{memberId}/relationships", memberId)
            .then()
            .statusCode(200)
            .body("id", equalTo(relationshipId.intValue()))
            .body("fromMemberId", equalTo(memberId.intValue()))
            .body("toMemberId", equalTo(toMemberId.intValue()))
            .body("relationshipType", equalTo(relationshipType.name()))
            .body("relationshipDisplayName", equalTo(customRelationship))
            .body("customRelationship", equalTo(customRelationship))
            .body("description", equalTo(description));
    }

    @Test
    @FTMockUser
    @DisplayName("defineRelationship 메서드는 유효하지 않은 요청을 받으면 상태 코드 400을 반환해야 한다")
    void given_invalid_request_when_define_relationship_then_return_status_400() {
        // given
        Long memberId = 1L;
        DefineFamilyRelationshipRequest request = new DefineFamilyRelationshipRequest(
            null, // toMemberId가 null
            null, // relationshipType이 null
            null,
            "설명"
        );

        // when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .post("/api/family-tree/members/{memberId}/relationships", memberId)
            .then()
            .statusCode(400);
    }

    @Test
    @FTMockUser
    @DisplayName("getAllRelationships 메서드는 구성원의 모든 관계를 조회하고 상태 코드 200을 반환해야 한다")
    void given_member_id_when_get_all_relationships_then_return_status_200() {
        // given
        Long memberId = 1L;
        Long familyId = 1L;
        
        FamilyRelationship relationship1 = FamilyRelationship.withId(
            2L,
            familyId,
            memberId,
            3L,
            FamilyRelationshipType.PARENT,
            null,
            "부모 관계",
            100L,
            LocalDateTime.now().minusDays(1),
            100L,
            LocalDateTime.now()
        );
        
        FamilyRelationship relationship2 = FamilyRelationship.withId(
            4L,
            familyId,
            memberId,
            5L,
            FamilyRelationshipType.SIBLING,
            null,
            "형제 관계",
            100L,
            LocalDateTime.now().minusDays(1),
            100L,
            LocalDateTime.now()
        );
        
        List<FamilyRelationship> relationships = List.of(relationship1, relationship2);
        
        when(findFamilyRelationshipUseCase.findAllRelationshipsByMember(any()))
            .thenReturn(relationships);

        // when & then
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/family-tree/members/{memberId}/relationships", memberId)
            .then()
            .statusCode(200)
            .body("$", hasSize(2))
            .body("[0].id", equalTo(2))
            .body("[0].fromMemberId", equalTo(memberId.intValue()))
            .body("[0].toMemberId", equalTo(3))
            .body("[0].relationshipType", equalTo("PARENT"))
            .body("[1].id", equalTo(4))
            .body("[1].fromMemberId", equalTo(memberId.intValue()))
            .body("[1].toMemberId", equalTo(5))
            .body("[1].relationshipType", equalTo("SIBLING"));
    }

    @Test
    @FTMockUser
    @DisplayName("getRelationship 메서드는 두 구성원 간의 관계를 조회하고 상태 코드 200을 반환해야 한다")
    void given_member_ids_when_get_relationship_then_return_status_200() {
        // given
        Long memberId = 1L;
        Long toMemberId = 2L;
        Long familyId = 1L;
        
        FamilyRelationship relationship = FamilyRelationship.withId(
            3L,
            familyId,
            memberId,
            toMemberId,
            FamilyRelationshipType.PARENT,
            null,
            "부모 관계",
            100L,
            LocalDateTime.now().minusDays(1),
            100L,
            LocalDateTime.now()
        );
        
        when(findFamilyRelationshipUseCase.findRelationship(any()))
            .thenReturn(Optional.of(relationship));

        // when & then
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/family-tree/members/{memberId}/relationships/{toMemberId}", memberId, toMemberId)
            .then()
            .statusCode(200)
            .body("id", equalTo(3))
            .body("fromMemberId", equalTo(memberId.intValue()))
            .body("toMemberId", equalTo(toMemberId.intValue()))
            .body("relationshipType", equalTo("PARENT"))
            .body("relationshipDisplayName", equalTo("부모"))
            .body("description", equalTo("부모 관계"));
    }

    @Test
    @FTMockUser
    @DisplayName("getRelationshipTypes 메서드는 모든 관계 타입을 조회하고 상태 코드 200을 반환해야 한다")
    void when_get_relationship_types_then_return_status_200() {
        // when & then
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/family-tree/members/1/relationships/types")
            .then()
            .statusCode(200)
            .body("$", hasSize(FamilyRelationshipType.values().length))
            .body("[0].code", is(notNullValue()))
            .body("[0].displayName", is(notNullValue()));
    }
}
