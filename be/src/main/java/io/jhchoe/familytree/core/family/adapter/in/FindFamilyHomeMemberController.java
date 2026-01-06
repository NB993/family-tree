package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMemberWithRelationshipResponse;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMembersWithRelationshipsResponse;
import io.jhchoe.familytree.core.family.application.port.in.FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberUseCase;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * Family 홈 구성원 목록 조회를 위한 REST 컨트롤러입니다.
 */
@RequiredArgsConstructor
@RequestMapping("/api/families")
@RestController
public class FindFamilyHomeMemberController {

    private final FindFamilyMemberUseCase findFamilyMemberUseCase;

    /**
     * Family 홈용 구성원 목록을 조회합니다.
     *
     * @param familyId 조회할 Family ID
     * @param ftUser 인증된 사용자 정보
     * @return Family 홈 구성원 목록
     */
    @GetMapping("/{familyId}/home/members")
    public ResponseEntity<List<FamilyMemberWithRelationshipResponse>> findFamilyHomeMembers(
        @PathVariable Long familyId,
        @AuthFTUser FTUser ftUser
    ) {
        Long currentUserId = ftUser.getId();

        // 1. 전체 구성원 조회 (내부에서 권한 검증 포함)
        FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery memberQuery =
            new FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery(familyId, currentUserId);
        List<FamilyMember> members = findFamilyMemberUseCase.findAll(memberQuery);

        // 2. 현재 사용자의 memberId 찾기
        Long currentMemberId = members.stream()
            .filter(m -> m.getUserId().equals(currentUserId))
            .findFirst()
            .map(FamilyMember::getId)
            .orElseThrow(() -> new IllegalStateException("현재 사용자의 구성원 정보를 찾을 수 없습니다."));

        // 3. 응답 변환 (관계 정보 없이)
        FamilyMembersWithRelationshipsResponse responseDTO =
            new FamilyMembersWithRelationshipsResponse(members, Collections.emptyList());

        List<FamilyMemberWithRelationshipResponse> response =
            responseDTO.toMemberWithRelationships(currentMemberId);

        return ResponseEntity.ok(response);
    }
}