package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.request.SaveFamilyRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.SaveFamilyResponse;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Family 생성을 위한 REST 컨트롤러입니다.
 */
@RequiredArgsConstructor
@RequestMapping("/api/family")
@RestController
public class SaveFamilyController {

    private final SaveFamilyUseCase saveFamilyUseCase;

    /**
     * 새로운 Family를 생성합니다.
     *
     * @param ftUser 현재 인증된 사용자
     * @param saveFamilyRequest Family 생성 요청 정보
     * @return 생성된 Family의 ID
     */
    @PostMapping
    public ResponseEntity<SaveFamilyResponse> saveFamily(
        @AuthFTUser FTUser ftUser,
        @RequestBody @Valid SaveFamilyRequest saveFamilyRequest
    ) {
        Boolean isPublic = "PUBLIC".equals(saveFamilyRequest.visibility());
        
        SaveFamilyCommand command = new SaveFamilyCommand(
            saveFamilyRequest.name(),
            saveFamilyRequest.profileUrl(),
            saveFamilyRequest.description(),
            isPublic
        );

        Long familyId = saveFamilyUseCase.save(command);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new SaveFamilyResponse(familyId));
    }
}
