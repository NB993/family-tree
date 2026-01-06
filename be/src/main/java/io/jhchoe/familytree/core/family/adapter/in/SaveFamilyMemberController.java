package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.request.SaveFamilyMemberRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.SaveFamilyMemberResponse;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberCommand;
import io.jhchoe.familytree.core.family.application.service.SaveFamilyMemberService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 가족 구성원 수동 등록 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/families/{familyId}/members")
@RequiredArgsConstructor
public class SaveFamilyMemberController {

    private final SaveFamilyMemberService saveFamilyMemberService;

    /**
     * 가족 구성원을 수동으로 등록합니다.
     *
     * @param ftUser   현재 인증된 사용자
     * @param familyId 가족 ID
     * @param request  등록 요청 데이터
     * @return 저장된 구성원 ID
     */
    @PostMapping
    public ResponseEntity<SaveFamilyMemberResponse> save(
        @AuthFTUser FTUser ftUser,
        @PathVariable Long familyId,
        @Valid @RequestBody SaveFamilyMemberRequest request
    ) {
        // 1. Command 객체 생성
        SaveFamilyMemberCommand command = new SaveFamilyMemberCommand(
            familyId,
            request.name(),
            request.birthday(),
            request.relationshipType(),
            request.customRelationship()
        );

        // 2. 유스케이스 실행
        Long savedId = saveFamilyMemberService.save(command, ftUser.getId());

        // 3. 응답 반환
        String createdUri = String.format("/api/families/%d/members/%d", familyId, savedId);
        return ResponseEntity
            .created(URI.create(createdUri))
            .body(SaveFamilyMemberResponse.of(savedId));
    }
}
