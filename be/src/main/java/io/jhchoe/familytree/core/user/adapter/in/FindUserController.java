package io.jhchoe.familytree.core.user.adapter.in;

import io.jhchoe.familytree.core.user.adapter.in.response.FindUserResponse;
import io.jhchoe.familytree.core.user.application.port.in.FindUserByNameQuery;
import io.jhchoe.familytree.core.user.application.port.in.FindUserUseCase;
import io.jhchoe.familytree.core.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 조회 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FindUserController {

    private final FindUserUseCase findUserUseCase;

    /**
     * 이름으로 사용자를 검색합니다.
     *
     * @param name 검색할 사용자 이름
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 검색된 사용자 목록
     */
    @GetMapping("/search")
    public ResponseEntity<List<FindUserResponse>> findUsersByName(
        @RequestParam String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        // Request 파라미터를 Query 객체로 변환
        FindUserByNameQuery query = new FindUserByNameQuery(name, page, size);
        
        // UseCase 호출
        List<User> users = findUserUseCase.findByName(query);
        
        // 응답 DTO로 변환
        List<FindUserResponse> response = users.stream()
            .map(FindUserResponse::from)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
}
