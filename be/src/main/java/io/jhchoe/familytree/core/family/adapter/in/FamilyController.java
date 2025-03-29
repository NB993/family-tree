package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.request.FamilyCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/family")
@RestController
public class FamilyController {

    @PostMapping
    public ResponseEntity<Void> createFamily(
        @AuthFTUser FTUser ftUser,
        @RequestBody @Valid FamilyCreateRequest familyCreateRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
