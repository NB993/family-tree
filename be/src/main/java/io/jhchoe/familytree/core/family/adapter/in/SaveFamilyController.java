package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.request.SaveFamilyRequest;
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

@RequiredArgsConstructor
@RequestMapping("/api/family")
@RestController
public class SaveFamilyController {

    private final SaveFamilyUseCase saveFamilyUseCase;

    @PostMapping
    public ResponseEntity<Void> saveFamily(
        @AuthFTUser FTUser ftUser,
        @RequestBody @Valid SaveFamilyRequest saveFamilyRequest
    ) {
        SaveFamilyCommand command = new SaveFamilyCommand(
            saveFamilyRequest.name(),
            saveFamilyRequest.description(),
            saveFamilyRequest.profileUrl());

        saveFamilyUseCase.save(command);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
