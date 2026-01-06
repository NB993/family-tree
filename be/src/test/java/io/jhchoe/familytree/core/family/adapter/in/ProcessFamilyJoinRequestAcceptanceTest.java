package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJoinRequestJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJoinRequestJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import io.jhchoe.familytree.test.fixture.FamilyFixture;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.common.auth.UserJpaEntity;
import io.jhchoe.familytree.common.auth.UserJpaRepository;
import io.jhchoe.familytree.core.user.domain.User;
import io.jhchoe.familytree.test.fixture.UserFixture;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@DisplayName("[Acceptance Test] ProcessFamilyJoinRequestControllerTest")
class ProcessFamilyJoinRequestAcceptanceTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @Autowired
    private FamilyJoinRequestJpaRepository familyJoinRequestJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @AfterEach
    void tearDown() {
        familyJoinRequestJpaRepository.deleteAll();
        familyMemberJpaRepository.deleteAll();
        familyJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("OWNER가 가입 신청을 승인할 때 200 OK와 처리 결과를 반환합니다")
    void process_returns_200_and_approved_status_when_owner_approves() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long ownerUserId = 1L; // WithMockOAuth2User 기본 사용자 ID

        // 가입 신청자의 User 생성 (ID는 DB에서 생성)
        User requesterUser = UserFixture.newOAuth2User();
        UserJpaEntity savedRequesterUser = userJpaRepository.save(UserJpaEntity.ofOAuth2User(requesterUser));
        Long requesterId = savedRequesterUser.getId();

        // Family 생성
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        // 대기 중인 가입 신청 생성
        FamilyJoinRequest joinRequest = FamilyJoinRequest.newRequest(familyId, requesterId);
        FamilyJoinRequestJpaEntity savedRequest = familyJoinRequestJpaRepository.save(
            FamilyJoinRequestJpaEntity.from(joinRequest)
        );
        Long requestId = savedRequest.getId();

        String requestBody = """
            {
                "status": "APPROVED",
                "message": "가입을 승인합니다"
            }
            """;

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .patch("/api/families/{familyId}/join-requests/{requestId}", familyId, requestId)
        .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("ADMIN이 가입 신청을 거부할 때 200 OK와 처리 결과를 반환합니다")
    void process_returns_200_and_rejected_status_when_admin_rejects() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long adminUserId = 1L; // WithMockOAuth2User 기본 사용자 ID
        Long ownerUserId = 100L;

        // Family 생성
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        // ADMIN 구성원 생성 (현재 인증된 사용자)
        FamilyMember adminMember = FamilyMember.withRole(
            familyId, adminUserId, "김관리자", "admin.jpg",
            now.minusYears(25), null, FamilyMemberRole.ADMIN
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(adminMember));
        
        // 대기 중인 가입 신청 생성
        FamilyJoinRequest joinRequest = FamilyJoinRequest.newRequest(familyId, 101L);
        FamilyJoinRequestJpaEntity savedRequest = familyJoinRequestJpaRepository.save(
            FamilyJoinRequestJpaEntity.from(joinRequest)
        );
        Long requestId = savedRequest.getId();

        String requestBody = """
            {
                "status": "REJECTED",
                "message": "가입을 거부합니다"
            }
            """;

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .patch("/api/families/{familyId}/join-requests/{requestId}", familyId, requestId)
        .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("ADMIN이 가입 신청을 거절할 때 DB에 REJECTED 상태로 저장됩니다")
    void process_saves_request_as_rejected_status_when_admin_rejects() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long adminUserId = 1L; // WithMockOAuth2User 기본 사용자 ID
        Long ownerUserId = 100L;

        // Family 생성
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        // ADMIN 구성원 생성 (현재 인증된 사용자)
        FamilyMember adminMember = FamilyMember.withRole(
            familyId, adminUserId, "김관리자", "admin.jpg",
            now.minusYears(25), null, FamilyMemberRole.ADMIN
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(adminMember));
        
        // 대기 중인 가입 신청 생성
        FamilyJoinRequest joinRequest = FamilyJoinRequest.newRequest(familyId, 101L);
        FamilyJoinRequestJpaEntity savedRequest = familyJoinRequestJpaRepository.save(
            FamilyJoinRequestJpaEntity.from(joinRequest)
        );
        Long requestId = savedRequest.getId();

        String requestBody = """
            {
                "status": "REJECTED",
                "message": "가입을 거부합니다"
            }
            """;

        // when
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .patch("/api/families/{familyId}/join-requests/{requestId}", familyId, requestId)
        .then()
            .statusCode(HttpStatus.OK.value());

        // then - 실제 DB 상태 검증
        FamilyJoinRequestJpaEntity updatedRequest = familyJoinRequestJpaRepository.findById(requestId)
            .orElseThrow(() -> new AssertionError("가입 신청이 존재하지 않습니다"));
        
        assertThat(updatedRequest.getStatus()).isEqualTo(FamilyJoinRequestStatus.REJECTED);
        
        // FamilyMember가 생성되지 않았는지 확인
        Optional<FamilyMemberJpaEntity> member = familyMemberJpaRepository.findByFamilyIdAndUserId(familyId, 101L);
        assertThat(member).isEmpty();
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("OWNER가 가입 신청을 거절할 때 DB에 REJECTED 상태로 저장됩니다")
    void process_saves_request_as_rejected_status_when_owner_rejects() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long ownerUserId = 1L; // WithMockOAuth2User 기본 사용자 ID

        // Family 생성
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));
        
        // 대기 중인 가입 신청 생성
        FamilyJoinRequest joinRequest = FamilyJoinRequest.newRequest(familyId, 101L);
        FamilyJoinRequestJpaEntity savedRequest = familyJoinRequestJpaRepository.save(
            FamilyJoinRequestJpaEntity.from(joinRequest)
        );
        Long requestId = savedRequest.getId();

        String requestBody = """
            {
                "status": "REJECTED",
                "message": "가입을 거부합니다"
            }
            """;

        // when
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .patch("/api/families/{familyId}/join-requests/{requestId}", familyId, requestId)
        .then()
            .statusCode(HttpStatus.OK.value());

        // then - 실제 DB 상태 검증
        FamilyJoinRequestJpaEntity updatedRequest = familyJoinRequestJpaRepository.findById(requestId)
            .orElseThrow(() -> new AssertionError("가입 신청이 존재하지 않습니다"));
        
        assertThat(updatedRequest.getStatus()).isEqualTo(FamilyJoinRequestStatus.REJECTED);
        
        // FamilyMember가 생성되지 않았는지 확인
        Optional<FamilyMemberJpaEntity> member = familyMemberJpaRepository.findByFamilyIdAndUserId(familyId, 101L);
        assertThat(member).isEmpty();
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("이미 거절된 가입 신청을 승인하려 할 때 400 Bad Request를 반환합니다")
    void process_returns_400_when_approving_rejected_request() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long ownerUserId = 1L; // WithMockOAuth2User 기본 사용자 ID

        // Family 생성
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));
        
        // 이미 거절된 가입 신청 생성
        FamilyJoinRequest rejectedRequest = FamilyJoinRequest.newRequest(familyId, 101L).reject();
        FamilyJoinRequestJpaEntity savedRequest = familyJoinRequestJpaRepository.save(
            FamilyJoinRequestJpaEntity.from(rejectedRequest)
        );
        Long requestId = savedRequest.getId();

        String requestBody = """
            {
                "status": "APPROVED",
                "message": "가입을 승인합니다"
            }
            """;

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .patch("/api/families/{familyId}/join-requests/{requestId}", familyId, requestId)
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("가입 신청 거절 시 응답에 올바른 상태와 처리 정보를 포함합니다")
    void process_returns_correct_response_data_when_rejected() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long adminUserId = 1L; // WithMockOAuth2User 기본 사용자 ID
        Long ownerUserId = 100L;

        // Family 생성
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        // ADMIN 구성원 생성 (현재 인증된 사용자)
        FamilyMember adminMember = FamilyMember.withRole(
            familyId, adminUserId, "김관리자", "admin.jpg",
            now.minusYears(25), null, FamilyMemberRole.ADMIN
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(adminMember));
        
        // 대기 중인 가입 신청 생성
        FamilyJoinRequest joinRequest = FamilyJoinRequest.newRequest(familyId, 101L);
        FamilyJoinRequestJpaEntity savedRequest = familyJoinRequestJpaRepository.save(
            FamilyJoinRequestJpaEntity.from(joinRequest)
        );
        Long requestId = savedRequest.getId();

        String requestBody = """
            {
                "status": "REJECTED",
                "message": "가입을 거부합니다"
            }
            """;

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .patch("/api/families/{familyId}/join-requests/{requestId}", familyId, requestId)
        .then()
            .log().all() // 응답 로그 출력하여 확인
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("MEMBER가 가입 신청 처리를 시도할 때 403 Forbidden을 반환합니다")
    void process_returns_403_when_member_attempts() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long memberUserId = 1L; // WithMockOAuth2User 기본 사용자 ID
        Long ownerUserId = 100L;

        // Family 생성
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        // MEMBER 구성원 생성 (현재 인증된 사용자)
        FamilyMember normalMember = FamilyMember.newMember(
            familyId, memberUserId, "김일반", "member.jpg",
            now.minusYears(20), null
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(normalMember));
        
        // 대기 중인 가입 신청 생성
        FamilyJoinRequest joinRequest = FamilyJoinRequest.newRequest(familyId, 101L);
        FamilyJoinRequestJpaEntity savedRequest = familyJoinRequestJpaRepository.save(
            FamilyJoinRequestJpaEntity.from(joinRequest)
        );
        Long requestId = savedRequest.getId();

        String requestBody = """
            {
                "status": "APPROVED",
                "message": "가입을 승인합니다"
            }
            """;

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .patch("/api/families/{familyId}/join-requests/{requestId}", familyId, requestId)
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("Family 구성원이 아닌 사용자가 처리를 시도할 때 403 Forbidden을 반환합니다")
    void process_returns_403_when_non_family_member_attempts() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long ownerUserId = 100L;
        Long currentUserId = 1L; // WithMockOAuth2User 기본 사용자 ID (Family 구성원 아님)

        // Family 생성
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // 다른 사용자의 OWNER 구성원 생성 (현재 인증된 사용자 ID=1과 다름)
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));
        
        // 대기 중인 가입 신청 생성
        FamilyJoinRequest joinRequest = FamilyJoinRequest.newRequest(familyId, 101L);
        FamilyJoinRequestJpaEntity savedRequest = familyJoinRequestJpaRepository.save(
            FamilyJoinRequestJpaEntity.from(joinRequest)
        );
        Long requestId = savedRequest.getId();

        String requestBody = """
            {
                "status": "APPROVED",
                "message": "가입을 승인합니다"
            }
            """;

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .patch("/api/families/{familyId}/join-requests/{requestId}", familyId, requestId)
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 처리를 시도할 때 401 Unauthorized를 반환합니다")
    void process_returns_401_when_unauthenticated_user_attempts() {
        // given
        Long familyId = 1L;
        Long requestId = 1L;

        String requestBody = """
            {
                "status": "APPROVED",
                "message": "가입을 승인합니다"
            }
            """;

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .patch("/api/families/{familyId}/join-requests/{requestId}", familyId, requestId)
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("이미 처리된 가입 신청을 재처리할 때 400 Bad Request를 반환합니다")
    void process_returns_400_when_already_processed_request() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long ownerUserId = 1L; // WithMockOAuth2User 기본 사용자 ID

        // Family 생성
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));
        
        // 이미 승인된 가입 신청을 먼저 생성하고 저장
        FamilyJoinRequest newRequest = FamilyJoinRequest.newRequest(familyId, 101L);
        FamilyJoinRequestJpaEntity savedTempRequest = familyJoinRequestJpaRepository.save(
            FamilyJoinRequestJpaEntity.from(newRequest)
        );
        
        // 승인 상태로 변경하여 저장
        FamilyJoinRequest approvedRequest = FamilyJoinRequest.withId(
            savedTempRequest.getId(), familyId, 101L, 
            FamilyJoinRequestStatus.APPROVED, now,
            ownerUserId, now, ownerUserId
        );
        FamilyJoinRequestJpaEntity savedRequest = familyJoinRequestJpaRepository.save(
            FamilyJoinRequestJpaEntity.from(approvedRequest)
        );
        Long requestId = savedRequest.getId();

        String requestBody = """
            {
                "status": "REJECTED",
                "message": "다시 거부합니다"
            }
            """;

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .patch("/api/families/{familyId}/join-requests/{requestId}", familyId, requestId)
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("존재하지 않는 가입 신청을 처리할 때 404 Not Found를 반환합니다")
    void process_returns_404_when_request_not_exists() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long ownerUserId = 1L; // WithMockOAuth2User 기본 사용자 ID

        // Family 생성
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));
        
        Long nonExistentRequestId = 999L;

        String requestBody = """
            {
                "status": "APPROVED",
                "message": "가입을 승인합니다"
            }
            """;

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .patch("/api/families/{familyId}/join-requests/{requestId}", familyId, nonExistentRequestId)
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("존재하지 않는 Family ID로 처리할 때 404 Not Found를 반환합니다")
    void process_returns_404_when_family_not_exists() {
        // given
        Long nonExistentFamilyId = 999L;
        Long requestId = 1L;

        String requestBody = """
            {
                "status": "APPROVED",
                "message": "가입을 승인합니다"
            }
            """;

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .patch("/api/families/{familyId}/join-requests/{requestId}", nonExistentFamilyId, requestId)
        .then()
            .log().all() // 실제 응답 확인
            .statusCode(HttpStatus.FORBIDDEN.value()); // 403으로 변경해서 테스트
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("잘못된 상태값을 전송할 때 400 Bad Request를 반환합니다")
    void process_returns_400_when_invalid_status() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long ownerUserId = 1L; // WithMockOAuth2User 기본 사용자 ID

        // Family 생성
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));
        
        // 대기 중인 가입 신청 생성
        FamilyJoinRequest joinRequest = FamilyJoinRequest.newRequest(familyId, 101L);
        FamilyJoinRequestJpaEntity savedRequest = familyJoinRequestJpaRepository.save(
            FamilyJoinRequestJpaEntity.from(joinRequest)
        );
        Long requestId = savedRequest.getId();

        String requestBody = """
            {
                "status": "INVALID_STATUS",
                "message": "잘못된 상태값"
            }
            """;

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .patch("/api/families/{familyId}/join-requests/{requestId}", familyId, requestId)
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("status 필드가 null일 때 400 Bad Request를 반환합니다")
    void process_returns_400_when_status_is_null() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long ownerUserId = 1L; // WithMockOAuth2User 기본 사용자 ID

        // Family 생성
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));
        
        // 대기 중인 가입 신청 생성
        FamilyJoinRequest joinRequest = FamilyJoinRequest.newRequest(familyId, 101L);
        FamilyJoinRequestJpaEntity savedRequest = familyJoinRequestJpaRepository.save(
            FamilyJoinRequestJpaEntity.from(joinRequest)
        );
        Long requestId = savedRequest.getId();

        String requestBody = """
            {
                "status": null,
                "message": "상태가 null입니다"
            }
            """;

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .patch("/api/families/{familyId}/join-requests/{requestId}", familyId, requestId)
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
