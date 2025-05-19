package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.response.FindFamilyResponse;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyByNameContainingQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyUseCase;
import io.jhchoe.familytree.core.family.domain.Family;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families")
public class FindFamilyController {

    private final FindFamilyUseCase findFamilyUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<FindFamilyResponse> findFamily(
        @PathVariable Long id,
        @AuthFTUser FTUser ftUser
    ) {
        Family family = findFamilyUseCase.findById(id);

        return ResponseEntity.ok(FindFamilyResponse.from(family));
    }

    @GetMapping
    public ResponseEntity<List<FindFamilyResponse>> findFamilies(
        @AuthFTUser FTUser ftUser,
        @RequestParam String name
    ) {
        FindFamilyByNameContainingQuery query = new FindFamilyByNameContainingQuery(name);
        List<Family> families = findFamilyUseCase.findByNameContaining(query);
        List<FindFamilyResponse> results = families.stream()
            .map(FindFamilyResponse::from)
            .toList();

        return ResponseEntity.ok(results);
    }
}
