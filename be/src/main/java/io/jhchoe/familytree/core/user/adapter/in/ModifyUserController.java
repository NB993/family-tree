package io.jhchoe.familytree.core.user.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.user.adapter.in.request.ModifyUserRequest;
import io.jhchoe.familytree.core.user.adapter.in.response.ModifyUserResponse;
import io.jhchoe.familytree.core.user.application.port.in.ModifyUserCommand;
import io.jhchoe.familytree.core.user.application.port.in.ModifyUserUseCase;
import io.jhchoe.familytree.core.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User 프로필 수정 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ModifyUserController {

    private final ModifyUserUseCase modifyUserUseCase;

    /**
     * 현재 로그인한 사용자의 프로필을 수정합니다.
     *
     * @param request 수정 요청 DTO
     * @param user    현재 인증된 사용자
     * @return 수정된 사용자 정보
     */
    @PatchMapping("/me")
    public ResponseEntity<ModifyUserResponse> modify(
        @Valid @RequestBody ModifyUserRequest request,
        @AuthFTUser FTUser user
    ) {
        Long userId = user.getId();

        ModifyUserCommand command = new ModifyUserCommand(
            userId,
            request.name(),
            request.birthday(),
            request.birthdayType()
        );

        User modifiedUser = modifyUserUseCase.modify(command);

        return ResponseEntity.ok(ModifyUserResponse.from(modifiedUser));
    }
}