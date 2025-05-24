package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMemberResponse;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMembersRoleQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMembersRoleUseCase;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Family 구성원의 역할 정보 조회를 담당하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families")
public class FindFamilyMemberRoleController {

    private final FindFamilyMembersRoleUseCase findFamilyMembersRoleUseCase;

    /**
     * 특정 Family의 모든 구성원과 역할 정보를 조회합니다.
     *
     * @param familyId Family ID
     * @param ftUser   인증된 사용자 정보
     * @return 구성원 목록과 역할 정보
     */
    @GetMapping("/{familyId}/members/roles")
    public ResponseEntity<List<FamilyMemberResponse>> findFamilyMembersRole(
        @PathVariable final Long familyId,
        @AuthFTUser final FTUser ftUser
    ) {
        // 1. 쿼리 객체 생성
        FindFamilyMembersRoleQuery query = new FindFamilyMembersRoleQuery(familyId, ftUser.getId());
        
        // 2. 유스케이스 실행
        List<FamilyMember> members = findFamilyMembersRoleUseCase.findAllByFamilyId(query);
        
        // 3. 응답 변환 및 반환
        List<FamilyMemberResponse> responses = members.stream()
            .map(FamilyMemberResponse::from)
            .toList();

        return ResponseEntity.ok(responses);
    }
}
