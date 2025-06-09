package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.response.CursorPageResponse;
import io.jhchoe.familytree.core.family.adapter.in.response.FindFamilyResponse;
import io.jhchoe.familytree.core.family.adapter.in.response.PublicFamilyResponse;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyByIdQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyByNameContainingQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyUseCase;
import io.jhchoe.familytree.core.family.application.port.in.FindMyFamiliesQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindPublicFamiliesQuery;
import io.jhchoe.familytree.core.family.domain.CursorPage;
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

    /**
     * 공개된 Family를 키워드로 검색하여 커서 기반 페이징으로 조회합니다.
     * 
     * @param ftUser 인증된 사용자 정보
     * @param keyword 검색할 키워드 (선택적, null인 경우 모든 공개 Family 조회)
     * @param cursor 무한 스크롤을 위한 커서 값 (선택적, null인 경우 첫 페이지)
     * @param size 조회할 Family 개수 (기본값: 20, 최대: 50)
     * @return 커서 기반 페이징된 공개 Family 목록
     */
    @GetMapping("/public")
    public ResponseEntity<CursorPageResponse<PublicFamilyResponse>> findPublicFamilies(
        @AuthFTUser FTUser ftUser,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "20") int size
    ) {
        FindPublicFamiliesQuery query = new FindPublicFamiliesQuery(keyword, cursor, size, ftUser.getId());
        CursorPage<Family> familyPage = findFamilyUseCase.findAll(query);

        // PublicFamilyResponse로 변환 (구성원 수는 임시로 0 설정, 추후 최적화 필요)
        List<PublicFamilyResponse> publicFamilies = familyPage.getContent().stream()
            .map(family -> PublicFamilyResponse.from(family, 0)) // TODO: 실제 구성원 수 계산 로직 추가
            .toList();

        CursorPageResponse<PublicFamilyResponse> response = new CursorPageResponse<>(
            publicFamilies,
            familyPage.getNextCursor(),
            familyPage.isHasNext(),
            familyPage.getSize()
        );

        return ResponseEntity.ok(response);
    }
}
