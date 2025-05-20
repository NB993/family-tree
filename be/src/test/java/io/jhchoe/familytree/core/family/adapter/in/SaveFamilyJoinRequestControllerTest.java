package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.in.request.SaveFamilyJoinRequestRequest;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJoinRequestJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJoinRequestJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import java.lang.reflect.Field;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("[Acceptance Test] SaveFamilyJoinRequestControllerTest")
class SaveFamilyJoinRequestControllerTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @Autowired
    private FamilyJoinRequestJpaRepository familyJoinRequestJpaRepository;

    @Test
    @WithMockOAuth2User(id = 1L)
    @DisplayName("Family 가입 신청을 성공적으로 처리할 수 있다")
    void save_family_join_request_successfully() {
        // given
        Long familyId = createFamily("테스트 가족", "테스트 설명");
        SaveFamilyJoinRequestRequest request = new SaveFamilyJoinRequestRequest(familyId);

        // when & then
        given()
            .contentType(JSON)
            .body(request)
            .when()
            .post("/api/family-join-requests")
            .then()
            .status(CREATED)
            .body("id", notNullValue());
    }

    @Test
    @WithMockOAuth2User(id = 1L)
    @DisplayName("존재하지 않는 Family에 가입 신청 시 404 응답을 반환한다")
    void return_not_found_when_family_not_exists() {
        // given
        Long nonExistentFamilyId = 9999L;
        SaveFamilyJoinRequestRequest request = new SaveFamilyJoinRequestRequest(nonExistentFamilyId);

        // when & then
        given()
            .contentType(JSON)
            .body(request)
            .when()
            .post("/api/family-join-requests")
            .then()
            .status(NOT_FOUND)
            .body("code", is(FamilyExceptionCode.FAMILY_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockOAuth2User(id = 1L)
    @DisplayName("이미 가입한 Family에 가입 신청 시 409 응답을 반환한다")
    void return_conflict_when_already_joined_family() {
        // given
        Long familyId = createFamily("테스트 가족", "테스트 설명");
        createFamilyMember(familyId, 1L, "이미 가입한 멤버", FamilyMemberStatus.ACTIVE);
        SaveFamilyJoinRequestRequest request = new SaveFamilyJoinRequestRequest(familyId);

        // when & then
        given()
            .contentType(JSON)
            .body(request)
            .when()
            .post("/api/family-join-requests")
            .then()
            .status(CONFLICT)
            .body("code", is(FamilyExceptionCode.ALREADY_JOINED_FAMILY.getCode()));
    }

    @Test
    @WithMockOAuth2User(id = 1L)
    @DisplayName("대기 상태의 가입 신청이 있는 경우 409 응답을 반환한다")
    void return_conflict_when_pending_request_exists() {
        // given
        Long familyId = createFamily("테스트 가족", "테스트 설명");
        createFamilyJoinRequest(familyId, 1L, FamilyJoinRequestStatus.PENDING);
        SaveFamilyJoinRequestRequest request = new SaveFamilyJoinRequestRequest(familyId);

        // when & then
        given()
            .contentType(JSON)
            .body(request)
            .when()
            .post("/api/family-join-requests")
            .then()
            .status(CONFLICT)
            .body("code", is(FamilyExceptionCode.JOIN_REQUEST_ALREADY_PENDING.getCode()));
    }

    @Test
    @WithMockOAuth2User(id = 1L)
    @DisplayName("승인 상태의 가입 신청이 있는 경우 409 응답을 반환한다")
    void return_conflict_when_approved_request_exists() {
        // given
        Long familyId = createFamily("테스트 가족", "테스트 설명");
        createFamilyJoinRequest(familyId, 1L, FamilyJoinRequestStatus.APPROVED);
        SaveFamilyJoinRequestRequest request = new SaveFamilyJoinRequestRequest(familyId);

        // when & then
        given()
            .contentType(JSON)
            .body(request)
            .when()
            .post("/api/family-join-requests")
            .then()
            .status(CONFLICT)
            .body("code", is(FamilyExceptionCode.ALREADY_JOINED_FAMILY.getCode()));
    }

    @Test
    @WithMockOAuth2User(id = 1L)
    @DisplayName("거절된 가입 신청이 있는 경우 새로운 가입 신청을 할 수 있다")
    void can_request_again_when_rejected_request_exists() {
        // given
        Long familyId = createFamily("테스트 가족", "테스트 설명");
        createFamilyJoinRequest(familyId, 1L, FamilyJoinRequestStatus.REJECTED);
        SaveFamilyJoinRequestRequest request = new SaveFamilyJoinRequestRequest(familyId);

        // when & then
        given()
            .contentType(JSON)
            .body(request)
            .when()
            .post("/api/family-join-requests")
            .then()
            .status(CREATED)
            .body("id", notNullValue());
    }

    @Test
    @WithMockOAuth2User(id = 1L)
    @DisplayName("최대 가입 가능 수를 초과하면 403 응답을 반환한다")
    void return_forbidden_when_exceed_max_join_limit() {
        // given
        // 5개의 가족에 이미 가입한 상태를 만듦
        for (int i = 0; i < 5; i++) {
            Long familyId = createFamily("테스트 가족 " + i, "테스트 설명 " + i);
            createFamilyMember(familyId, 1L, "멤버 " + i, FamilyMemberStatus.ACTIVE);
        }
        
        // 새로운 가족에 가입 신청
        Long newFamilyId = createFamily("새 가족", "새 설명");
        SaveFamilyJoinRequestRequest request = new SaveFamilyJoinRequestRequest(newFamilyId);

        // when & then
        given()
            .contentType(JSON)
            .body(request)
            .when()
            .post("/api/family-join-requests")
            .then()
            .status(FORBIDDEN)
            .body("code", is(FamilyExceptionCode.EXCEEDED_FAMILY_JOIN_LIMIT.getCode()));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("familyId가 null이면 400 응답을 반환한다")
    void return_bad_request_when_family_id_is_null() {
        // given: 요청 객체를 null 값으로 생성 시도 (JSON 직접 전송)
        String requestWithNullId = "{\"familyId\": null}";

        // when & then
        given()
            .contentType(JSON)
            .body(requestWithNullId)
            .when()
            .post("/api/family-join-requests")
            .then()
            .status(BAD_REQUEST);
    }

    /**
     * Family 엔티티를 생성하고 ID를 반환합니다.
     * 테스트 목적으로만 사용되는 메서드입니다.
     */
    private Long createFamily(String name, String description) {
        // 테스트 코드 내에서는 리플렉션을 사용하여 엔티티를 생성합니다
        try {
            FamilyJpaEntity entity = new FamilyJpaEntity();
            
            Field nameField = FamilyJpaEntity.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(entity, name);
            
            Field descriptionField = FamilyJpaEntity.class.getDeclaredField("description");
            descriptionField.setAccessible(true);
            descriptionField.set(entity, description);
            
            return familyJpaRepository.save(entity).getId();
        } catch (Exception e) {
            throw new RuntimeException("테스트 데이터 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }

    /**
     * FamilyMember 엔티티를 생성합니다.
     * 테스트 목적으로만 사용되는 메서드입니다.
     */
    private void createFamilyMember(Long familyId, Long userId, String name, FamilyMemberStatus status) {
        try {
            FamilyMemberJpaEntity entity = new FamilyMemberJpaEntity();
            
            Field familyIdField = FamilyMemberJpaEntity.class.getDeclaredField("familyId");
            familyIdField.setAccessible(true);
            familyIdField.set(entity, familyId);
            
            Field userIdField = FamilyMemberJpaEntity.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(entity, userId);
            
            Field nameField = FamilyMemberJpaEntity.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(entity, name);
            
            Field statusField = FamilyMemberJpaEntity.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(entity, status);
            
            familyMemberJpaRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("테스트 데이터 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }

    /**
     * FamilyJoinRequest 엔티티를 생성합니다.
     * 테스트 목적으로만 사용되는 메서드입니다.
     */
    private void createFamilyJoinRequest(Long familyId, Long requesterId, FamilyJoinRequestStatus status) {
        try {
            FamilyJoinRequestJpaEntity entity = new FamilyJoinRequestJpaEntity();
            
            Field familyIdField = FamilyJoinRequestJpaEntity.class.getDeclaredField("familyId");
            familyIdField.setAccessible(true);
            familyIdField.set(entity, familyId);
            
            Field requesterIdField = FamilyJoinRequestJpaEntity.class.getDeclaredField("requesterId");
            requesterIdField.setAccessible(true);
            requesterIdField.set(entity, requesterId);
            
            Field statusField = FamilyJoinRequestJpaEntity.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(entity, status);
            
            familyJoinRequestJpaRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("테스트 데이터 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
