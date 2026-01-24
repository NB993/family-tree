package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.request.ModifyFamilyMemberRelationshipRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.ModifyFamilyMemberRelationshipResponse;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberRelationshipCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberRelationshipUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 구성원 관계 변경을 위한 REST 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families/{familyId}/members")
public class ModifyFamilyMemberRelationshipController {

    private final ModifyFamilyMemberRelationshipUseCase modifyFamilyMemberRelationshipUseCase;

    /**
     * 구성원의 관계 정보를 변경합니다.
     *
     * @param familyId 가족 ID
     * @param memberId 구성원 ID
     * @param request  관계 변경 요청
     * @param ftUser   인증된 사용자 정보
     * @return 변경된 구성원 ID
     */
    @PatchMapping("/{memberId}/relationship")
    public ResponseEntity<ModifyFamilyMemberRelationshipResponse> modifyRelationship(
        @PathVariable Long familyId,
        @PathVariable Long memberId,
        @Valid @RequestBody ModifyFamilyMemberRelationshipRequest request,
        @AuthFTUser FTUser ftUser
    ) {
        Long updatedMemberId = modifyFamilyMemberRelationshipUseCase.modify(
            new ModifyFamilyMemberRelationshipCommand(
                familyId,
                memberId,
                ftUser.getId(),
                request.getRelationshipType(),
                request.getCustomRelationship()
            )
        );

        return ResponseEntity.ok(new ModifyFamilyMemberRelationshipResponse(updatedMemberId));
    }
}
