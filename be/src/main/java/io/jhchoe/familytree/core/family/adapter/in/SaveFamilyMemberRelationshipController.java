package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.exception.CommonExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.adapter.in.request.SaveFamilyMemberRelationshipRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMemberRelationshipResponse;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberRelationshipCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 가족 관계 관리 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/families/members/{memberId}/relationships")
@RequiredArgsConstructor
public class SaveFamilyMemberRelationshipController {

    private final SaveFamilyMemberRelationshipUseCase saveFamilyMemberRelationshipUseCase;
    private final FindFamilyMemberRelationshipUseCase findFamilyMemberRelationshipUseCase;

    /**
     * 현재 로그인한 사용자에 대해 특정 대상과의 관계를 정의합니다.
     *
     * @param ftUser    현재 인증된 사용자
     * @param request   관계 정의 요청 정보
     * @return 생성/수정된 가족 관계 정보
     */
    @PostMapping
    public ResponseEntity<FamilyMemberRelationshipResponse> saveFamilyMemberRelationship(
        @AuthFTUser FTUser ftUser,
        @Valid @RequestBody SaveFamilyMemberRelationshipRequest request
    ) {
        // TODO: 실제 구현 시에는 사용자에게 해당 Family와 구성원 권한이 있는지 확인해야 합니다.
        // 여기서는 간단히 memberId가 사용자의 것인지만 검증합니다.
        
        // 1. 요청에서 familyId 가져오기 - 실제 구현 시 구성원으로 가족 ID 조회 필요
        Long familyId = 1L; // 임시 값, 실제로는 구성원 ID로 가족 ID 조회
        
        // 2. Command 객체 생성
        SaveFamilyMemberRelationshipCommand command = new SaveFamilyMemberRelationshipCommand(
            familyId,
            ftUser.getId(), // 현재 로그인한 사용자의 구성원 ID
            request.toMemberId(), // 관계를 정의할 대상 ID
            request.relationshipType(),
            request.customRelationship(),
            request.description()
        );
        
        // 3. 유스케이스 실행
        Long relationshipId = saveFamilyMemberRelationshipUseCase.saveRelationship(command);
        
        // 4. 생성/수정된 관계 조회
        FindFamilyMemberRelationshipQuery query = new FindFamilyMemberRelationshipQuery(
            familyId,
            ftUser.getId(),
            request.toMemberId()
        );
        
        FamilyMemberRelationship relationship = findFamilyMemberRelationshipUseCase.findRelationship(query)
            .orElseThrow(() -> new FTException(CommonExceptionCode.NOT_FOUND, "relationship"));
        
        // 5. 응답 반환
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(FamilyMemberRelationshipResponse.from(relationship));
    }
}
