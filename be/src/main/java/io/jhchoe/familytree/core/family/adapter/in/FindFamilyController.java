package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.request.ModifyFamilyRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.FindFamilyResponse;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyUseCase;
import io.jhchoe.familytree.core.family.application.service.FindFamilyService;
import io.jhchoe.familytree.core.family.domain.Family;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
