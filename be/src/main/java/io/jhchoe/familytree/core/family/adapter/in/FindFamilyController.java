package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.response.FindFamilyResponse;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyByIdQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyByNameContainingQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyUseCase;
import io.jhchoe.familytree.core.family.application.port.in.FindMyFamiliesQuery;
import io.jhchoe.familytree.core.family.domain.Family;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Family 조회 기능을 제공하는 REST Controller.
 * 
 * <p>헥사고날 아키텍처의 프레젠테이션 계층에서 Family 조회 HTTP 요청을 처리합니다.
 * 사용자 입력을 Query 객체로 변환하여 UseCase로 전달합니다.</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families")
public class FindFamilyController {

    private final FindFamilyUseCase findFamilyUseCase;

    /**
     * Family ID로 단일 Family를 조회합니다.
     * 
     * @param id 조회할 Family ID
     * @param ftUser 인증된 사용자 정보
     * @return 조회된 Family 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<FindFamilyResponse> findFamily(
        @PathVariable Long id,
        @AuthFTUser FTUser ftUser
    ) {
        FindFamilyByIdQuery query = new FindFamilyByIdQuery(id);
        Family family = findFamilyUseCase.find(query);

        return ResponseEntity.ok(FindFamilyResponse.from(family));
    }

    /**
     * 이름으로 Family 목록을 조회합니다.
     * 
     * @param ftUser 인증된 사용자 정보
     * @param name 검색할 Family 이름
     * @return 조회된 Family 목록
     */
    @GetMapping
    public ResponseEntity<List<FindFamilyResponse>> findFamilies(
        @AuthFTUser FTUser ftUser,
        @RequestParam String name
    ) {
        FindFamilyByNameContainingQuery query = new FindFamilyByNameContainingQuery(name);
        List<Family> families = findFamilyUseCase.findAll(query);
        List<FindFamilyResponse> results = families.stream()
            .map(FindFamilyResponse::from)
            .toList();

        return ResponseEntity.ok(results);
    }

    /**
     * 현재 사용자가 소속된 Family 목록을 조회합니다.
     * 
     * @param ftUser 인증된 사용자 정보
     * @return 조회된 내 소속 Family 목록
     */
    @GetMapping("/my")
    public ResponseEntity<List<FindFamilyResponse>> findMyFamilies(
        @AuthFTUser FTUser ftUser
    ) {
        FindMyFamiliesQuery query = new FindMyFamiliesQuery(ftUser.getId());
        List<Family> families = findFamilyUseCase.findAll(query);
        List<FindFamilyResponse> results = families.stream()
            .map(FindFamilyResponse::from)
            .toList();

        return ResponseEntity.ok(results);
    }
}
