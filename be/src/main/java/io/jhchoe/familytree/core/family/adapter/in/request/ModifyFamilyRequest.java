package io.jhchoe.familytree.core.family.adapter.in.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModifyFamilyRequest(
    @NotBlank(message = "Family 이름을 입력해주세요.")
    @Size(max = 100, message = "Family 이름은 100자 이내로 작성해주세요.")
    String name,

    String profileUrl,

    @Size(max = 200, message = "Family 설명은 200자 이내로 작성해주세요.")
    String description
) {
}
