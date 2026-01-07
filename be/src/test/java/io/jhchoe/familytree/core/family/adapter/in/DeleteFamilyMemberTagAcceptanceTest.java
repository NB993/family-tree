package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
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
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@DisplayName("[Acceptance Test] DeleteFamilyMemberTagController")
class DeleteFamilyMemberTagAcceptanceTest extends AcceptanceTestBase {

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
    @DisplayName("OWNER가 태그 삭제 시 204 No Content를 반환합니다")
    void delete_returns_204_when_owner_deletes_tag() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        Long tagId = createTag(familyId, "친가", userId);

        // when & then
        given()
            .postProcessors(csrf())
        .when()
            .delete("/api/families/{familyId}/tags/{tagId}", familyId, tagId)
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("OWNER가 아닌 구성원이 태그 삭제 시 403 Forbidden을 반환합니다")
    void delete_returns_403_when_non_owner_deletes_tag() {
        // given
        Long userId = 1L;
        Long ownerUserId = 100L;
        Long familyId = createFamilyWithOwnerAndMember(ownerUserId, userId);
        Long tagId = createTag(familyId, "친가", ownerUserId);

        // when & then
        given()
            .postProcessors(csrf())
        .when()
            .delete("/api/families/{familyId}/tags/{tagId}", familyId, tagId)
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("존재하지 않는 태그 삭제 시 404 Not Found를 반환합니다")
    void delete_returns_404_when_tag_not_found() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        Long nonExistentTagId = 999L;

        // when & then
        given()
            .postProcessors(csrf())
        .when()
            .delete("/api/families/{familyId}/tags/{tagId}", familyId, nonExistentTagId)
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
