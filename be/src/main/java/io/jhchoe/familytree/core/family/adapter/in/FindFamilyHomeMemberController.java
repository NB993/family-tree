package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMemberWithRelationshipResponse;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMembersWithRelationshipsResponse;
import io.jhchoe.familytree.core.family.application.port.in.FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindAllFamilyMemberRelationshipsQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberByIdQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberUseCase;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Family 홈 구성원 목록 조회를 위한 REST 컨트롤러입니다.
 * 기존 UseCase들을 조합하여 Family 홈에서 필요한 구성원+관계 정보를 제공합니다.
 */
@RequiredArgsConstructor
@RequestMapping("/api/families")
@RestController
public class FindFamilyHomeMemberController {

    private final FindFamilyMemberUseCase findFamilyMemberUseCase;
    private final FindFamilyMemberRelationshipUseCase findFamilyMemberRelationshipUseCase;

    /**
     * Family 홈용 구성원 목록을 조회합니다.
     * 현재 사용자 기준으로 가족 구성원과 관계 정보를 함께 제공합니다.
     *
     * @param familyId 조회할 Family ID
     * @param ftUser 인증된 사용자 정보
     * @return Family 홈 구성원 목록 (관계 정보 포함)
     */
    @GetMapping("/{familyId}/home/members")
    public ResponseEntity<List<FamilyMemberWithRelationshipResponse>> findFamilyHomeMembers(
        @PathVariable Long familyId,
        @AuthFTUser FTUser ftUser
    ) {
        Long currentUserId = ftUser.getId();
        
        // 1. 현재 사용자 조회 + 권한 검증 (효율적!)
        FindFamilyMemberByIdQuery currentUserQuery = 
            new FindFamilyMemberByIdQuery(familyId, currentUserId, currentUserId);
        FamilyMember currentMember = findFamilyMemberUseCase.find(currentUserQuery);
        Long currentMemberId = currentMember.getId();
        
        // 2. 전체 구성원 조회
        FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery memberQuery = 
            new FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery(familyId, currentUserId);
        List<FamilyMember> members = findFamilyMemberUseCase.findAll(memberQuery);
        
        // 3. 관계 정보 조회
        FindAllFamilyMemberRelationshipsQuery relationshipQuery = 
            new FindAllFamilyMemberRelationshipsQuery(familyId, currentMemberId);
        List<FamilyMemberRelationship> relationships = findFamilyMemberRelationshipUseCase.findAll(relationshipQuery);
        
        // 4. 일급객체로 조합 및 변환
        FamilyMembersWithRelationshipsResponse responseDTO = 
            new FamilyMembersWithRelationshipsResponse(members, relationships);
        
        List<FamilyMemberWithRelationshipResponse> response = 
            responseDTO.toMemberWithRelationships(currentMemberId);
        
        return ResponseEntity.ok(response);
    }
}
