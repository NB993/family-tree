package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.request.ModifyFamilyRequest;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Family 정보 수정을 위한 REST 컨트롤러입니다.
 * <p>
 * 이 컨트롤러는 클라이언트의 Family 수정 요청을 처리하고,
 * 요청을 적절한 유스케이스로 변환하여 전달하는 역할을 합니다.
 * </p>
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/families")
public class ModifyFamilyController {

    private final ModifyFamilyUseCase modifyFamilyUseCase;

    @PutMapping("/{id}")
    public ResponseEntity<Long> modifyFamily(
            @PathVariable Long id,
            @Valid @RequestBody ModifyFamilyRequest request,
            @AuthFTUser FTUser ftUser
    ) {
        ModifyFamilyCommand command = new ModifyFamilyCommand(
                id,
                request.name(),
                request.profileUrl(),
                request.description()
        );
        Long familyId = modifyFamilyUseCase.modify(command);

        return ResponseEntity.ok().build();
    }
}
