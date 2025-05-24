package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.request.ProcessFamilyJoinRequestRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.ProcessFamilyJoinRequestResponse;
import io.jhchoe.familytree.core.family.application.port.in.ProcessFamilyJoinRequestCommand;
import io.jhchoe.familytree.core.family.application.port.in.ProcessFamilyJoinRequestUseCase;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Family 가입 신청 처리를 담당하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class ProcessFamilyJoinRequestController {

    private final ProcessFamilyJoinRequestUseCase processFamilyJoinRequestUseCase;

    /**
     * Family 가입 신청을 처리합니다.
     *
     * @param familyId Family ID
     * @param requestId 가입 신청 ID
     * @param ftUser 인증된 사용자 정보
     * @param request 가입 신청 처리 요청 정보
     * @return 가입 신청 처리 결과
     */
    @PatchMapping("/{familyId}/join-requests/{requestId}")
    public ResponseEntity<ProcessFamilyJoinRequestResponse> process(
        @PathVariable Long familyId,
        @PathVariable Long requestId,
        @AuthFTUser FTUser ftUser,
        @RequestBody @Valid ProcessFamilyJoinRequestRequest request
    ) {
        ProcessFamilyJoinRequestCommand command = new ProcessFamilyJoinRequestCommand(
            familyId,
            requestId,
            request.status(),
            request.message(),
            ftUser.getId()
        );

        FamilyJoinRequest processedRequest = processFamilyJoinRequestUseCase.process(command);
        
        return ResponseEntity.ok(ProcessFamilyJoinRequestResponse.from(processedRequest));
    }
}
