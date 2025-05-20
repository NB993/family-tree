package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.request.SaveFamilyJoinRequestRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.SaveFamilyJoinRequestResponse;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyJoinRequestCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyJoinRequestUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Family 가입 신청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/family-join-requests")
@RequiredArgsConstructor
public class SaveFamilyJoinRequestController {

    private final SaveFamilyJoinRequestUseCase saveFamilyJoinRequestUseCase;

    /**
     * Family 가입 신청을 처리합니다.
     *
     * @param ftUser 인증된 사용자 정보
     * @param request 가입 신청 요청 정보
     * @return 가입 신청 결과
     */
    @PostMapping
    public ResponseEntity<SaveFamilyJoinRequestResponse> save(
        @AuthFTUser FTUser ftUser,
        @RequestBody @Valid SaveFamilyJoinRequestRequest request
    ) {
        SaveFamilyJoinRequestCommand command = new SaveFamilyJoinRequestCommand(
            request.familyId(),
            ftUser.getId()
        );

        Long savedId = saveFamilyJoinRequestUseCase.save(command);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(SaveFamilyJoinRequestResponse.from(savedId));
    }
}
