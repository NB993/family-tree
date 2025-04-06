package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.request.CreateFamilyRequest;
import io.jhchoe.familytree.core.family.application.port.in.CreateFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.in.CreateFamilyUseCase;
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
public class CreateFamilyController {

    private final CreateFamilyUseCase createFamilyUseCase;

    @PostMapping
    public ResponseEntity<Void> createFamily(
        @AuthFTUser FTUser ftUser,
        @RequestBody @Valid CreateFamilyRequest createFamilyRequest
    ) {
        CreateFamilyCommand command = new CreateFamilyCommand(
            createFamilyRequest.name(),
            createFamilyRequest.description(),
            createFamilyRequest.profileUrl());

        createFamilyUseCase.create(command);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
