package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberTagJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberTagJpaRepository;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.jhchoe.familytree.test.fixture.FamilyFixture;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@DisplayName("[Acceptance Test] FindFamilyMemberTagController")
class FindFamilyMemberTagAcceptanceTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @Autowired
    private FamilyMemberTagJpaRepository familyMemberTagJpaRepository;

    @AfterEach
    void tearDown() {
        familyMemberTagJpaRepository.deleteAll();
        familyMemberJpaRepository.deleteAll();
        familyJpaRepository.deleteAll();
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("구성원이 태그 목록 조회 시 200 OK와 태그 목록을 반환합니다")
    void find_returns_200_and_tags_when_member_requests() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        createTag(familyId, "친가", userId);
        createTag(familyId, "외가", userId);
        createTag(familyId, "조카들", userId);

        // when & then
        given()
        .when()
            .get("/api/families/{familyId}/tags", familyId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("$", hasSize(3));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("태그가 없으면 빈 목록을 반환합니다")
    void find_returns_empty_list_when_no_tags() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);

        // when & then
        given()
        .when()
            .get("/api/families/{familyId}/tags", familyId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("$", hasSize(0));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("구성원이 아닌 사용자가 조회 시 403 Forbidden을 반환합니다")
    void find_returns_403_when_non_member_requests() {
        // given
        Long userId = 1L;
        Long ownerUserId = 100L;
        Long familyId = createFamilyWithOwner(ownerUserId);

        // when & then
        given()
        .when()
            .get("/api/families/{familyId}/tags", familyId)
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("태그 목록은 가나다순으로 정렬됩니다")
    void find_returns_tags_sorted_by_name() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        createTag(familyId, "조카들", userId);
        createTag(familyId, "가족", userId);
        createTag(familyId, "어른들", userId);

        // when & then
        given()
        .when()
            .get("/api/families/{familyId}/tags", familyId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("$", hasSize(3))
            .body("[0].name", equalTo("가족"))
            .body("[1].name", equalTo("어른들"))
            .body("[2].name", equalTo("조카들"));
    }

    private Long createFamilyWithOwner(Long ownerUserId) {
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        LocalDateTime now = LocalDateTime.now();
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "Owner", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        return familyId;
    }

    private Long createTag(Long familyId, String name, Long createdBy) {
        FamilyMemberTag tag = FamilyMemberTag.newTag(familyId, name, createdBy);
        FamilyMemberTagJpaEntity entity = FamilyMemberTagJpaEntity.from(tag);
        FamilyMemberTagJpaEntity savedEntity = familyMemberTagJpaRepository.save(entity);
        return savedEntity.getId();
    }
}
