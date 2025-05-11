package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.request.SaveFamilyMemberRelationshipRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMemberRelationshipResponse;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberRelationshipCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberRelationshipUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 가족 관계 관리 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/families/{familyId}/members/{toMemberId}/relationships")
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
        // 2. Command 객체 생성
        SaveFamilyMemberRelationshipCommand command = new SaveFamilyMemberRelationshipCommand(
            request.familyId(),
            ftUser.getId(), // 현재 로그인한 사용자의 구성원 ID
            request.toMemberId(), // 관계를 정의할 대상 ID
            request.relationshipType(),
            request.customRelationship(),
            request.description()
        );
        
        // 3. 유스케이스 실행
        Long id = saveFamilyMemberRelationshipUseCase.save(command);

        // 5. 응답 반환
        String createdUri = String.format("/api/families/%d/members/%d/relationships/%d",
                request.familyId(), request.toMemberId(), id);
        return ResponseEntity
            .created(URI.create(createdUri))
            .body(FamilyMemberRelationshipResponse.saveResponse(id));
    }
}
