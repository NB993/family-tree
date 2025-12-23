package io.jhchoe.familytree.core.invite.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.invite.adapter.in.response.SaveFamilyInviteResponse;
import io.jhchoe.familytree.core.invite.application.port.in.SaveFamilyInviteCommand;
import io.jhchoe.familytree.core.invite.application.port.in.SaveFamilyInviteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 초대 링크 생성을 위한 REST 컨트롤러입니다.
 */
@RequiredArgsConstructor
@RequestMapping("/api/families/{familyId}/invites")
@RestController
public class SaveFamilyInviteController {

    private final SaveFamilyInviteUseCase saveFamilyInviteUseCase;

    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;

    /**
     * 새로운 초대 링크를 생성합니다.
     *
     * @param familyId 초대를 생성할 가족 ID
     * @param ftUser 현재 인증된 사용자
     * @return 생성된 초대 코드와 URL
     */
    @PostMapping
    public ResponseEntity<SaveFamilyInviteResponse> save(
        @PathVariable Long familyId,
        @AuthFTUser FTUser ftUser
    ) {
        final SaveFamilyInviteCommand command = new SaveFamilyInviteCommand(familyId, ftUser.getId());
        final String inviteCode = saveFamilyInviteUseCase.save(command);
        final String inviteUrl = baseUrl + "/invite/" + inviteCode;
        final SaveFamilyInviteResponse response = new SaveFamilyInviteResponse(inviteCode, inviteUrl);
        return ResponseEntity.ok(response);
    }
}