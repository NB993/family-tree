package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.application.port.in.FamilyMemberTagMappingInfo;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberTagMappingCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberTagMappingUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 멤버 태그 할당 Controller입니다.
 */
@RequiredArgsConstructor
@RequestMapping("/api/families")
@RestController
public class ModifyFamilyMemberTagMappingController {

    private final ModifyFamilyMemberTagMappingUseCase modifyFamilyMemberTagMappingUseCase;

    /**
     * 멤버에게 태그를 할당합니다.
     *
     * @param familyId Family ID
     * @param memberId Member ID
     * @param request  태그 할당 요청
     * @param user     인증된 사용자 정보
     * @return 할당된 태그 정보
     */
    @PutMapping("/{familyId}/members/{memberId}/tags")
    public ResponseEntity<ModifyFamilyMemberTagMappingResponse> modify(
        @PathVariable final Long familyId,
        @PathVariable final Long memberId,
        @RequestBody @Valid final ModifyFamilyMemberTagMappingRequest request,
        @AuthFTUser final FTUser user
    ) {
        ModifyFamilyMemberTagMappingCommand command = new ModifyFamilyMemberTagMappingCommand(
            familyId,
            memberId,
            request.tagIds()
        );

        FamilyMemberTagMappingInfo result = modifyFamilyMemberTagMappingUseCase.modify(command, user.getId());
        ModifyFamilyMemberTagMappingResponse response = ModifyFamilyMemberTagMappingResponse.from(result);

        return ResponseEntity.ok(response);
    }
}
