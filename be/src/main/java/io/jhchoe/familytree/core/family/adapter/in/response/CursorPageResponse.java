package io.jhchoe.familytree.core.family.adapter.in.response;

import java.util.List;
import lombok.Getter;

/**
 * 커서 기반 페이징 응답을 위한 DTO 클래스입니다.
 * 
 * <p>무한 스크롤에 최적화된 응답 구조로, 다음 페이지 조회를 위한 커서 정보를 제공합니다.
 * 클라이언트는 nextCursor 값을 사용하여 다음 페이지를 요청할 수 있습니다.</p>
 *
 * @param <T> 페이징할 데이터 타입
 */
@Getter
public class CursorPageResponse<T> {

    private final List<T> content;
    private final PaginationInfo pagination;

    /**
     * 커서 페이지 응답 객체를 생성합니다.
     *
     * @param content 현재 페이지의 데이터 목록
     * @param nextCursor 다음 페이지 조회를 위한 커서
     * @param hasNext 다음 페이지 존재 여부
     * @param size 요청된 페이지 크기
     */
    public CursorPageResponse(List<T> content, String nextCursor, boolean hasNext, int size) {
        this.content = content;
        this.pagination = new PaginationInfo(nextCursor, hasNext, size);
    }

    /**
     * 페이징 정보를 담는 내부 클래스입니다.
     */
    @Getter
    public static class PaginationInfo {
        private final String nextCursor;
        private final boolean hasNext;
        private final int size;

        public PaginationInfo(String nextCursor, boolean hasNext, int size) {
            this.nextCursor = nextCursor;
            this.hasNext = hasNext;
            this.size = size;
        }
    }
}
