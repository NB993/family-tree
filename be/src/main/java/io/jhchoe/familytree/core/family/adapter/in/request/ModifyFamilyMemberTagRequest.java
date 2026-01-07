package io.jhchoe.familytree.core.family.adapter.in.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 태그 수정 요청 DTO입니다.
 */
@Getter
@NoArgsConstructor
public class ModifyFamilyMemberTagRequest {

    @Size(min = 1, max = 10, message = "태그 이름은 1자 이상 10자 이하여야 합니다.")
    private String name;

    private String color;

    /**
     * 요청 객체를 생성합니다.
     *
     * @param name  태그 이름 (수정할 경우)
     * @param color 태그 색상 (수정할 경우)
     */
    public ModifyFamilyMemberTagRequest(final String name, final String color) {
        this.name = name;
        this.color = color;
    }
}
