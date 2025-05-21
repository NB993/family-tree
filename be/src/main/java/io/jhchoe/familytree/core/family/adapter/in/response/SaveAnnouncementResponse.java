package io.jhchoe.familytree.core.family.adapter.in.response;

import lombok.Getter;

/**
 * 공지사항 저장 응답 DTO입니다.
 */
@Getter
public class SaveAnnouncementResponse {
    
    private final Long id;
    
    /**
     * 응답 객체를 생성합니다.
     *
     * @param id 저장된 공지사항 ID
     */
    public SaveAnnouncementResponse(Long id) {
        this.id = id;
    }
}
