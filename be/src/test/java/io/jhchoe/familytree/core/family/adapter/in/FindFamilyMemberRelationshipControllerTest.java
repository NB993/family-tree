package io.jhchoe.familytree.core.family.adapter.in;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberRelationshipJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberRelationshipJpaRepository;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@DisplayName("[Acceptance Test] FamilyRelationshipController")
class FindFamilyMemberRelationshipControllerTest extends AcceptanceTestBase {

    @Autowired
    private FamilyMemberRelationshipJpaRepository familyMemberRelationshipJpaRepository;

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        familyMemberRelationshipJpaRepository.deleteAll();
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("getAllRelationships 메서드는 구성원의 모든 관계를 조회하고 상태 코드 200을 반환해야 한다")
    void given_member_id_when_get_all_relationships_then_return_status_200() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 1L;
        Long toMemberId1 = 3L;
        Long toMemberId2 = 5L;

        FamilyMemberRelationship relationship1 = FamilyMemberRelationship.newRelationship(
            familyId,
            fromMemberId,
            toMemberId1,
            FamilyMemberRelationshipType.PARENT,
            null,
            "부모 관계"
        );

        FamilyMemberRelationship relationship2 = FamilyMemberRelationship.newRelationship(
            familyId,
            fromMemberId,
            toMemberId2,
            FamilyMemberRelationshipType.SIBLING,
            null,
            "형제 관계"
        );

        familyMemberRelationshipJpaRepository.save(FamilyMemberRelationshipJpaEntity.from(relationship1));
        familyMemberRelationshipJpaRepository.save(FamilyMemberRelationshipJpaEntity.from(relationship2));

        // when & then
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{familyId}/members/relationships", familyId)
            .then()
            .statusCode(200)
            .body("$", hasSize(2))
            .body("[0].fromMemberId", equalTo(fromMemberId.intValue()))
            .body("[0].relationshipType", is(notNullValue()))
            .body("[0].relationshipDisplayName", is("부모"))
            .body("[1].fromMemberId", equalTo(fromMemberId.intValue()))
            .body("[1].relationshipType", is(notNullValue()))
            .body("[1].relationshipDisplayName", is("형제자매"));
    }

    @Test
    @WithMockOAuth2User(id = 2L)
    @DisplayName("getRelationship 메서드는 두 구성원 간의 관계를 조회하고 상태 코드 200을 반환해야 한다")
    void given_member_ids_when_get_relationship_then_return_status_200() {
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FTUser ftUser = (FTUser) authentication.getPrincipal();

        Long familyId = 1L;
        Long fromMemberId = ftUser.getId();
        Long toMemberId = 3L;

        FamilyMemberRelationship relationship = FamilyMemberRelationship.newRelationship(
            familyId,
            fromMemberId,
            toMemberId,
            FamilyMemberRelationshipType.PARENT,
            null,
            "부모 관계"
        );

        familyMemberRelationshipJpaRepository.save(FamilyMemberRelationshipJpaEntity.from(relationship));

        // when & then
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{familyId}/members/{toMemberId}/relationships", familyId, toMemberId)
            .then()
            .statusCode(200)
            .body("fromMemberId", equalTo(fromMemberId.intValue()))
            .body("toMemberId", equalTo(toMemberId.intValue()))
            .body("relationshipType", equalTo("PARENT"))
            .body("relationshipDisplayName", equalTo("부모"))
            .body("description", equalTo("부모 관계"));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("getRelationshipTypes 메서드는 모든 관계 타입을 조회하고 상태 코드 200을 반환해야 한다")
    void when_get_relationship_types_then_return_status_200() {
        // given
        FamilyMemberRelationshipType[] allTypes = FamilyMemberRelationshipType.values();

        // when & then
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/members/relationship-types")
            .then()
            .statusCode(200)
            .body("$", hasSize(FamilyMemberRelationshipType.values().length))
            .body("code", hasItems(Arrays.stream(allTypes).map(Enum::name).toArray(String[]::new)))
            .body("displayName", hasItems(Arrays.stream(allTypes).map(FamilyMemberRelationshipType::getDisplayName).toArray(String[]::new)));
    }
}
