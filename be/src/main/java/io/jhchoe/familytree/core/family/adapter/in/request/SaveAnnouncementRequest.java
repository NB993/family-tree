package io.jhchoe.familytree.core.family.adapter.in.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공지사항 저장을 위한 요청 DTO입니다.
 */
@Getter
@NoArgsConstructor
public class SaveAnnouncementRequest {
    
    @NotBlank(message = "제목은 필수입니다.")
    private String title;
    
    private String content;
    
    /**
     * 요청 객체를 생성합니다.
     *
     * @param title   제목
     * @param content 내용
     */
    public SaveAnnouncementRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
