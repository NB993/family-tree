package io.jhchoe.familytree.core.family.adapter.in.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 태그 생성 요청 DTO입니다.
 */
@Getter
@NoArgsConstructor
public class SaveFamilyMemberTagRequest {

    @NotBlank(message = "태그 이름은 필수입니다.")
    @Size(min = 1, max = 10, message = "태그 이름은 1자 이상 10자 이하여야 합니다.")
    private String name;

    /**
     * 요청 객체를 생성합니다.
     *
     * @param name 태그 이름
     */
    public SaveFamilyMemberTagRequest(final String name) {
        this.name = name;
    }
}
