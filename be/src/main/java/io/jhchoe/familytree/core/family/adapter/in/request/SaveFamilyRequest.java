package io.jhchoe.familytree.core.family.adapter.in.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SaveFamilyRequest(
    @NotBlank(message = "가족명을 입력해주세요")
    @Size(max = 20, message = "가족명은 20자 이하로 입력해주세요")
    @Pattern(regexp = "^[^@#$%^&*()+=\\[\\]{}|\\\\:;\"'<>?,./!~`]+$", 
             message = "가족명에는 한글, 영문, 숫자, 공백, 이모지만 사용 가능합니다")
    String name,
    
    @Pattern(regexp = "^(PUBLIC|PRIVATE)$", message = "공개 여부는 PUBLIC 또는 PRIVATE만 가능합니다")
    String visibility,
    
    String description,
    String profileUrl
) {
}
