package io.jhchoe.familytree.core.user.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.exception.CommonExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.user.adapter.in.response.FindUserResponse;
import io.jhchoe.familytree.core.user.application.port.in.FindUserByNameQuery;
import io.jhchoe.familytree.core.user.application.port.in.FindUserUseCase;
import io.jhchoe.familytree.core.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 조회를 위한 REST 컨트롤러입니다.
 */
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class FindUserController {

    private final FindUserUseCase findUserUseCase;

    /**
     * ID로 사용자 정보를 조회합니다.
     *
     * @param id 조회할 사용자의 ID
     * @return 조회된 사용자 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<FindUserResponse> findUserById(@PathVariable Long id) {
        return findUserUseCase.findById(id)
            .map(FindUserResponse::from)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new FTException(CommonExceptionCode.NOT_FOUND, "user"));
    }

    /**
     * 이름으로 사용자를 조회합니다.
     *
     * @param name 조회할 사용자의 이름
     * @return 해당 이름을 가진 사용자 목록
     */
    @GetMapping("/search")
    public ResponseEntity<List<FindUserResponse>> findUsersByName(@RequestParam String name) {
        FindUserByNameQuery query = new FindUserByNameQuery(name);
        List<User> users = findUserUseCase.findByName(query);
        
        List<FindUserResponse> responses = users.stream()
            .map(FindUserResponse::from)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(responses);
    }

    /**
     * 모든 사용자를 조회합니다.
     *
     * @return 모든 사용자 목록
     */
    @GetMapping
    public ResponseEntity<List<FindUserResponse>> findAllUsers() {
        List<User> users = findUserUseCase.findAll();
        
        List<FindUserResponse> responses = users.stream()
            .map(FindUserResponse::from)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(responses);
    }

    /**
     * 현재 로그인한 사용자 정보를 조회합니다.
     *
     * @param ftUser 현재 인증된 사용자
     * @return 현재 로그인한 사용자 정보
     */
    @GetMapping("/me")
    public ResponseEntity<FindUserResponse> findCurrentUser(@AuthFTUser FTUser ftUser) {
        return findUserUseCase.findById(ftUser.getId())
            .map(FindUserResponse::from)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new FTException(CommonExceptionCode.NOT_FOUND, "user"));
    }
}
