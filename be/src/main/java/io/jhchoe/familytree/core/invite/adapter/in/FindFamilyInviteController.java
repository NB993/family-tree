package io.jhchoe.familytree.core.invite.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.invite.adapter.in.response.FindFamilyInviteResponse;
import io.jhchoe.familytree.core.invite.application.port.in.FindFamilyInviteByCodeQuery;
import io.jhchoe.familytree.core.invite.application.port.in.FindFamilyInvitesByRequesterIdQuery;
import io.jhchoe.familytree.core.invite.application.port.in.FindFamilyInviteUseCase;
import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 초대 링크 조회를 위한 REST 컨트롤러입니다.
 */
@RequiredArgsConstructor
@RequestMapping("/api/invites")
@RestController
public class FindFamilyInviteController {

    private final FindFamilyInviteUseCase findFamilyInviteUseCase;

    /**
     * 초대 코드로 초대 정보를 조회합니다.
     * 프론트엔드에서 초대 링크 접속 시 호출하여 유효성을 확인합니다.
     *
     * @param inviteCode 초대 코드
     * @return 초대 정보
     */
    @GetMapping("/{inviteCode}")
    public ResponseEntity<FindFamilyInviteResponse> findByInviteCode(
        @PathVariable final String inviteCode
    ) {
        final FindFamilyInviteByCodeQuery query = new FindFamilyInviteByCodeQuery(inviteCode);
        final FamilyInvite familyInvite = findFamilyInviteUseCase.find(query);
        final FindFamilyInviteResponse response = FindFamilyInviteResponse.from(familyInvite);
        return ResponseEntity.ok(response);
    }

    /**
     * 현재 사용자가 생성한 초대 목록을 조회합니다.
     *
     * @param ftUser 현재 인증된 사용자
     * @return 초대 목록
     */
    @GetMapping("/my")
    public ResponseEntity<List<FindFamilyInviteResponse>> findMyInvites(
        @AuthFTUser FTUser ftUser
    ) {
        final FindFamilyInvitesByRequesterIdQuery query = new FindFamilyInvitesByRequesterIdQuery(ftUser.getId());
        final List<FamilyInvite> invites = findFamilyInviteUseCase.findAll(query);
        final List<FindFamilyInviteResponse> responses = invites.stream()
            .map(FindFamilyInviteResponse::from)
            .toList();
        return ResponseEntity.ok(responses);
    }
}