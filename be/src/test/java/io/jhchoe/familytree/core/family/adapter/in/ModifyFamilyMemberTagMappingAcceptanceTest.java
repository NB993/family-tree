package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberTagJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberTagJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberTagMappingJpaRepository;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.jhchoe.familytree.test.fixture.FamilyFixture;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@DisplayName("[Acceptance Test] ModifyFamilyMemberTagMappingController")
class ModifyFamilyMemberTagMappingAcceptanceTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @Autowired
    private FamilyMemberTagJpaRepository familyMemberTagJpaRepository;

    @Autowired
    private FamilyMemberTagMappingJpaRepository familyMemberTagMappingJpaRepository;

    @AfterEach
    void tearDown() {
        familyMemberTagMappingJpaRepository.deleteAll();
        familyMemberTagJpaRepository.deleteAll();
        familyMemberJpaRepository.deleteAll();
        familyJpaRepository.deleteAll();
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("OWNER가 멤버에게 태그를 할당하면 200 OK와 할당된 태그 정보를 반환합니다")
    void modify_member_tags_returns_200_when_owner_assigns_tags() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        Long memberId = createMember(familyId, 2L, "김철수");
        Long tagId1 = createTag(familyId, "친가", userId);
        Long tagId2 = createTag(familyId, "외가", userId);

        String requestBody = """
            {
                "tagIds": [%d, %d]
            }
            """.formatted(tagId1, tagId2);

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/members/{memberId}/tags", familyId, memberId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("memberId", equalTo(memberId.intValue()))
            .body("memberName", equalTo("김철수"))
            .body("tags", hasSize(2))
            .body("tags.name", containsInAnyOrder("친가", "외가"));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("OWNER가 빈 태그 목록을 할당하면 200 OK와 빈 태그 배열을 반환합니다")
    void modify_member_tags_returns_200_with_empty_tags() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        Long memberId = createMember(familyId, 2L, "김철수");

        String requestBody = """
            {
                "tagIds": []
            }
            """;

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/members/{memberId}/tags", familyId, memberId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("memberId", equalTo(memberId.intValue()))
            .body("memberName", equalTo("김철수"))
            .body("tags", empty());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("OWNER가 아닌 구성원이 태그 할당 시 403 Forbidden을 반환합니다")
    void modify_member_tags_returns_403_when_non_owner() {
        // given
        Long userId = 1L;
        Long ownerUserId = 100L;
        Long familyId = createFamilyWithOwnerAndMember(ownerUserId, userId);
        Long memberId = findMemberId(familyId, userId);
        Long tagId = createTag(familyId, "친가", ownerUserId);

        String requestBody = """
            {
                "tagIds": [%d]
            }
            """.formatted(tagId);

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/members/{memberId}/tags", familyId, memberId)
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("존재하지 않는 멤버에게 태그 할당 시 404 Not Found를 반환합니다")
    void modify_member_tags_returns_404_when_member_not_found() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        Long tagId = createTag(familyId, "친가", userId);
        Long nonExistentMemberId = 999L;

        String requestBody = """
            {
                "tagIds": [%d]
            }
            """.formatted(tagId);

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/members/{memberId}/tags", familyId, nonExistentMemberId)
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("10개 초과 태그 할당 시 400 Bad Request를 반환합니다")
    void modify_member_tags_returns_400_when_exceeds_max_tags() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        Long memberId = createMember(familyId, 2L, "김철수");

        // 11개의 태그 생성
        List<Long> tagIds = IntStream.rangeClosed(1, 11)
            .mapToObj(i -> createTag(familyId, "태그" + i, userId))
            .toList();

        String tagIdsJson = tagIds.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(", "));

        String requestBody = """
            {
                "tagIds": [%s]
            }
            """.formatted(tagIdsJson);

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/members/{memberId}/tags", familyId, memberId)
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
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

    private Long createMember(Long familyId, Long userId, String name) {
        LocalDateTime now = LocalDateTime.now();
        FamilyMember member = FamilyMember.newMember(
            familyId, userId, name, "member.jpg",
            now.minusYears(25), null
        );
        FamilyMemberJpaEntity savedEntity = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member));
        return savedEntity.getId();
    }

    private Long findMemberId(Long familyId, Long userId) {
        return familyMemberJpaRepository.findByFamilyIdAndUserId(familyId, userId)
            .orElseThrow()
            .getId();
    }

    private Long createTag(Long familyId, String name, Long createdBy) {
        FamilyMemberTag tag = FamilyMemberTag.newTag(familyId, name, createdBy);
        FamilyMemberTagJpaEntity entity = FamilyMemberTagJpaEntity.from(tag);
        FamilyMemberTagJpaEntity savedEntity = familyMemberTagJpaRepository.save(entity);
        return savedEntity.getId();
    }
}
