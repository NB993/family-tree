package io.jhchoe.familytree.core.family.adapter.in;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberRelationshipJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberRelationshipJpaRepository;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("[Acceptance Test] FamilyRelationshipController")
class FindFamilyMemberRelationshipControllerTest extends AcceptanceTestBase {

    @Autowired
    private FamilyMemberRelationshipJpaRepository familyMemberRelationshipJpaRepository;

    private Long familyId;
    private Long fromMemberId;
    private Long toMemberId1;
    private Long toMemberId2;
    private FamilyMemberRelationshipJpaEntity relationship1;
    private FamilyMemberRelationshipJpaEntity relationship2;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        familyId = 1L;
        fromMemberId = 1L;
        toMemberId1 = 3L;
        toMemberId2 = 5L;

        // 실제 데이터 생성
        FamilyMemberRelationship domain1 = FamilyMemberRelationship.withId(
            null,
            familyId,
            fromMemberId,
            toMemberId1,
            FamilyMemberRelationshipType.PARENT,
            null,
            "부모 관계",
            100L,
            LocalDateTime.now().minusDays(1),
            100L,
            LocalDateTime.now()
        );

        FamilyMemberRelationship domain2 = FamilyMemberRelationship.withId(
            null,
            familyId,
            fromMemberId,
            toMemberId2,
            FamilyMemberRelationshipType.SIBLING,
            null,
            "형제 관계",
            100L,
            LocalDateTime.now().minusDays(1),
            100L,
            LocalDateTime.now()
        );

        // 데이터베이스에 저장
        relationship1 = familyMemberRelationshipJpaRepository.save(FamilyMemberRelationshipJpaEntity.from(domain1));
        relationship2 = familyMemberRelationshipJpaRepository.save(FamilyMemberRelationshipJpaEntity.from(domain2));
    }

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        familyMemberRelationshipJpaRepository.deleteAll();
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("getAllRelationships 메서드는 구성원의 모든 관계를 조회하고 상태 코드 200을 반환해야 한다")
    void given_member_id_when_get_all_relationships_then_return_status_200() {
        // given - beforeEach에서 설정한 데이터 사용

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
            .body("[1].fromMemberId", equalTo(fromMemberId.intValue()))
            .body("[1].relationshipType", is(notNullValue()));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("getRelationship 메서드는 두 구성원 간의 관계를 조회하고 상태 코드 200을 반환해야 한다")
    void given_member_ids_when_get_relationship_then_return_status_200() {
        // given - beforeEach에서 설정한 데이터 사용

        // when & then
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{familyId}/members/{toMemberId}/relationships", familyId, toMemberId1)
            .then()
            .statusCode(200)
            .body("fromMemberId", equalTo(fromMemberId.intValue()))
            .body("toMemberId", equalTo(toMemberId1.intValue()))
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
            .get("/api/families/{familyId}/members/relationship-types", familyId)
            .then()
            .statusCode(200)
            .body("$", hasSize(FamilyMemberRelationshipType.values().length))
            .body("[0].code", is(notNullValue()))
            .body("[0].displayName", is(notNullValue()));
    }
}
