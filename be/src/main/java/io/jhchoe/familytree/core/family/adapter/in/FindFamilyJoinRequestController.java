package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.response.FindFamilyJoinRequestResponse;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyJoinRequestQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyJoinRequestUseCase;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Family 가입 신청 목록 조회를 위한 REST 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class FindFamilyJoinRequestController {

    private final FindFamilyJoinRequestUseCase findFamilyJoinRequestUseCase;

    /**
     * 지정된 Family의 가입 신청 목록을 조회합니다.
     * 관리자(OWNER/ADMIN) 권한을 가진 사용자만 조회할 수 있습니다.
     *
     * @param ftUser 인증된 사용자 정보
     * @param familyId 조회할 Family의 ID
     * @return 가입 신청 목록
     */
    @GetMapping("/{familyId}/join-requests")
    public ResponseEntity<List<FindFamilyJoinRequestResponse>> findAllByFamilyId(
        @AuthFTUser final FTUser ftUser,
        @PathVariable final Long familyId
    ) {
        // 1. 쿼리 객체 생성
        FindFamilyJoinRequestQuery query = new FindFamilyJoinRequestQuery(
            familyId,
            ftUser.getId()
        );

        // 2. 유스케이스 실행
        List<FamilyJoinRequest> joinRequests = findFamilyJoinRequestUseCase.findAllByFamilyId(query);

        // 3. 응답 변환 및 반환
        List<FindFamilyJoinRequestResponse> response = joinRequests.stream()
            .map(FindFamilyJoinRequestResponse::from)
            .toList();
        
        return ResponseEntity.ok(response);
    }
}
