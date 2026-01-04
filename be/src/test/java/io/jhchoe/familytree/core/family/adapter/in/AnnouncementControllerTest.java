package io.jhchoe.familytree.core.family.adapter.in;

import static org.hamcrest.Matchers.*;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.*;
import io.jhchoe.familytree.core.family.domain.*;
import io.jhchoe.familytree.test.fixture.FamilyFixture;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

/**
 * AnnouncementController의 인수 테스트입니다.
 */
@DisplayName("[Acceptance Test] AnnouncementControllerTest")
class AnnouncementControllerTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @Autowired
    private AnnouncementJpaRepository announcementJpaRepository;

    @WithMockOAuth2User
    @Test
    @DisplayName("OWNER 권한으로 공지사항 생성 요청 시 성공하면 201 상태코드를 반환합니다")
    void save_announcement_returns_201_when_user_is_owner() {
        // given
        LocalDateTime now = LocalDateTime.now();
        FamilyJpaEntity family = familyJpaRepository.save(
            FamilyJpaEntity.from(FamilyFixture.newFamily())
        );
        familyMemberJpaRepository.save(
            FamilyMemberJpaEntity.from(FamilyMember.newOwner(
                family.getId(), 1L, null, "소유자", null, now, "KR"
            ))
        );

        // when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .body("""
                {
                    "title": "중요 공지",
                    "content": "공지사항 내용입니다."
                }
                """)
            .when()
            .post("/api/families/{familyId}/announcements", family.getId())
            .then()
            .statusCode(201)
            .body("id", greaterThan(0));
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("공지사항 생성 시 제목이 빈 문자열이면 400 상태코드와 유효성 검증 오류를 반환합니다")
    void save_announcement_returns_400_when_title_is_empty() {
        // given
        LocalDateTime now = LocalDateTime.now();
        FamilyJpaEntity family = familyJpaRepository.save(
            FamilyJpaEntity.from(FamilyFixture.newFamily())
        );
        familyMemberJpaRepository.save(
            FamilyMemberJpaEntity.from(FamilyMember.newOwner(
                family.getId(), 1L, null, "소유자", null, now, "KR"
            ))
        );

        // when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .body("""
                {
                    "title": "",
                    "content": "내용"
                }
                """)
            .when()
            .post("/api/families/{familyId}/announcements", family.getId())
            .then()
            .statusCode(400)
            .body("validations.size()", greaterThan(0))
            .body("validations.field", hasItem("title"));
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("공지사항 목록 조회 시 성공하면 200 상태코드와 공지사항 목록을 반환합니다")
    void find_announcements_returns_200_and_list() {
        // given
        LocalDateTime now = LocalDateTime.now();
        FamilyJpaEntity family = familyJpaRepository.save(
            FamilyJpaEntity.from(FamilyFixture.newFamily())
        );
        familyMemberJpaRepository.save(
            FamilyMemberJpaEntity.from(FamilyMember.newOwner(
                family.getId(), 1L, null, "소유자", null, now, "KR"
            ))
        );
        
        announcementJpaRepository.save(
            AnnouncementJpaEntity.from(Announcement.create(
                family.getId(), "공지1", "내용1"
            ))
        );
        announcementJpaRepository.save(
            AnnouncementJpaEntity.from(Announcement.create(
                family.getId(), "공지2", "내용2"
            ))
        );

        // when & then
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{familyId}/announcements", family.getId())
            .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("[0].title", notNullValue())
            .body("[0].content", notNullValue())
            .body("[1].title", notNullValue())
            .body("[1].content", notNullValue());
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 공지사항 생성 요청 시 401 상태코드를 반환합니다")
    void save_announcement_returns_401_when_user_not_authenticated() {
        // given
        FamilyJpaEntity family = familyJpaRepository.save(
            FamilyJpaEntity.from(FamilyFixture.newFamily())
        );

        // when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .body("""
                {
                    "title": "제목",
                    "content": "내용"
                }
                """)
            .when()
            .post("/api/families/{familyId}/announcements", family.getId())
            .then()
            .statusCode(401);
    }
}
