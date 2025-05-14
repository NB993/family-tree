package io.jhchoe.familytree.core.family.adapter.in;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DisplayName("[Acceptance Test] FamilyRelationshipController")
class FamilyMemberRelationshipControllerTest extends AcceptanceTestBase {

    @MockitoBean
    private SaveFamilyMemberRelationshipUseCase saveFamilyMemberRelationshipUseCase;

    @MockitoBean
    private FindFamilyMemberRelationshipUseCase findFamilyMemberRelationshipUseCase;

    @Test
    @WithMockOAuth2User
    @DisplayName("getAllRelationships 메서드는 구성원의 모든 관계를 조회하고 상태 코드 200을 반환해야 한다")
    void given_member_id_when_get_all_relationships_then_return_status_200() {
        // given
        Long memberId = 1L;
        Long familyId = 1L;
        
        FamilyMemberRelationship relationship1 = FamilyMemberRelationship.withId(
            2L,
            familyId,
            memberId,
            3L,
            FamilyMemberRelationshipType.PARENT,
            null,
            "부모 관계",
            100L,
            LocalDateTime.now().minusDays(1),
            100L,
            LocalDateTime.now()
        );
        
        FamilyMemberRelationship relationship2 = FamilyMemberRelationship.withId(
            4L,
            familyId,
            memberId,
            5L,
            FamilyMemberRelationshipType.SIBLING,
            null,
            "형제 관계",
            100L,
            LocalDateTime.now().minusDays(1),
            100L,
            LocalDateTime.now()
        );
        
        List<FamilyMemberRelationship> relationships = List.of(relationship1, relationship2);
        
        when(findFamilyMemberRelationshipUseCase.findAll(any()))
            .thenReturn(relationships);

        // when & then
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/members/{memberId}/relationships", memberId)
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
    @WithMockOAuth2User
    @DisplayName("getRelationship 메서드는 두 구성원 간의 관계를 조회하고 상태 코드 200을 반환해야 한다")
    void given_member_ids_when_get_relationship_then_return_status_200() {
        // given
        Long memberId = 1L;
        Long toMemberId = 2L;
        Long familyId = 1L;
        
        FamilyMemberRelationship relationship = FamilyMemberRelationship.withId(
            3L,
            familyId,
            memberId,
            toMemberId,
            FamilyMemberRelationshipType.PARENT,
            null,
            "부모 관계",
            100L,
            LocalDateTime.now().minusDays(1),
            100L,
            LocalDateTime.now()
        );
        
        when(findFamilyMemberRelationshipUseCase.find(any()))
            .thenReturn(relationship);

        // when & then
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/members/{memberId}/relationships/{toMemberId}", memberId, toMemberId)
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
    @WithMockOAuth2User
    @DisplayName("getRelationshipTypes 메서드는 모든 관계 타입을 조회하고 상태 코드 200을 반환해야 한다")
    void when_get_relationship_types_then_return_status_200() {
        // when & then
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/members/1/relationships/types")
            .then()
            .statusCode(200)
            .body("$", hasSize(FamilyMemberRelationshipType.values().length))
            .body("[0].code", is(notNullValue()))
            .body("[0].displayName", is(notNullValue()));
    }
}
