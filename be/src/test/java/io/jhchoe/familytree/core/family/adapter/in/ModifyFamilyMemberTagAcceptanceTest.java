package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@DisplayName("[Acceptance Test] ModifyFamilyMemberTagController")
class ModifyFamilyMemberTagAcceptanceTest extends AcceptanceTestBase {

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
    @DisplayName("OWNER가 태그 수정 시 200 OK와 수정된 태그 정보를 반환합니다")
    void modify_returns_200_when_owner_modifies_tag() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        Long tagId = createTag(familyId, "친가", userId);

        String requestBody = """
            {
                "name": "외가"
            }
            """;

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/tags/{tagId}", familyId, tagId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(tagId.intValue()))
            .body("name", equalTo("외가"));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("OWNER가 태그 색상 수정 시 200 OK를 반환합니다")
    void modify_returns_200_when_owner_modifies_color() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        Long tagId = createTag(familyId, "친가", userId);

        String requestBody = """
            {
                "name": "친가",
                "color": "#D3E5EF"
            }
            """;

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/tags/{tagId}", familyId, tagId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo("친가"))
            .body("color", equalTo("#D3E5EF"));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("OWNER가 아닌 구성원이 태그 수정 시 403 Forbidden을 반환합니다")
    void modify_returns_403_when_non_owner_modifies_tag() {
        // given
        Long userId = 1L;
        Long ownerUserId = 100L;
        Long familyId = createFamilyWithOwnerAndMember(ownerUserId, userId);
        Long tagId = createTag(familyId, "친가", ownerUserId);

        String requestBody = """
            {
                "name": "외가"
            }
            """;

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/tags/{tagId}", familyId, tagId)
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("존재하지 않는 태그 수정 시 404 Not Found를 반환합니다")
    void modify_returns_404_when_tag_not_found() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        Long nonExistentTagId = 999L;

        String requestBody = """
            {
                "name": "외가"
            }
            """;

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/tags/{tagId}", familyId, nonExistentTagId)
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
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

    private Long createFamilyWithOwnerAndMember(Long ownerUserId, Long memberUserId) {
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        LocalDateTime now = LocalDateTime.now();

        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "Owner", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        FamilyMember normalMember = FamilyMember.newMember(
            familyId, memberUserId, "Member", "member.jpg",
            now.minusYears(25), null
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(normalMember));

        return familyId;
    }

    private Long createTag(Long familyId, String name, Long createdBy) {
        FamilyMemberTag tag = FamilyMemberTag.newTag(familyId, name, createdBy);
        FamilyMemberTagJpaEntity entity = FamilyMemberTagJpaEntity.from(tag);
        FamilyMemberTagJpaEntity savedEntity = familyMemberTagJpaRepository.save(entity);
        return savedEntity.getId();
    }
}
