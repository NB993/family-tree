package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.request.SaveFamilyMemberTagRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.SaveFamilyMemberTagResponse;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberTagCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberTagUseCase;
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
 * FamilyMemberTag 생성 API 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families/{familyId}/tags")
public class SaveFamilyMemberTagController {

    private final SaveFamilyMemberTagUseCase saveFamilyMemberTagUseCase;

    /**
     * 태그를 생성합니다.
     *
     * @param familyId Family ID
     * @param request  태그 생성 요청 정보
     * @param user     인증된 사용자 정보
     * @return 생성된 태그의 ID
     */
    @PostMapping
    public ResponseEntity<SaveFamilyMemberTagResponse> saveTag(
        @PathVariable final Long familyId,
        @Valid @RequestBody final SaveFamilyMemberTagRequest request,
        @AuthFTUser final FTUser user
    ) {
        Long userId = user.getId();

        SaveFamilyMemberTagCommand command = new SaveFamilyMemberTagCommand(
            familyId,
            request.getName()
        );

        Long savedId = saveFamilyMemberTagUseCase.save(command, userId);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new SaveFamilyMemberTagResponse(savedId));
    }
}
