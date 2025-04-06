package io.jhchoe.familytree.core.family.adapter.in.request;

import jakarta.validation.constraints.NotBlank;

public record CreateFamilyRequest(
    @NotBlank
    String name,
    String description,
    String profileUrl
) {
}
