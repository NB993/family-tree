package io.jhchoe.familytree.core.family.domain;

import java.util.List;
import java.util.Objects;
import lombok.Getter;

/**
 * 커서 기반 페이징 결과를 담는 도메인 객체입니다.
 * 
 * <p>무한 스크롤에 최적화된 페이징 방식으로, 다음 페이지 조회를 위한 커서 정보를 제공합니다.
 * 오프셋 기반 페이징보다 안정적이며, 데이터 변경 시에도 일관성을 유지합니다.</p>
 *
 * @param <T> 페이징할 데이터 타입
 */
@Getter
public class CursorPage<T> {

    private final List<T> content;
    private final String nextCursor;
    private final boolean hasNext;
    private final int size;

    /**
     * 커서 페이지 객체를 생성합니다.
     *
     * @param content 현재 페이지의 데이터 목록
     * @param nextCursor 다음 페이지 조회를 위한 커서 (Base64 인코딩된 값)
     * @param hasNext 다음 페이지 존재 여부
     * @param size 요청된 페이지 크기
     * @throws IllegalArgumentException content가 null이거나 size가 음수인 경우
     */
    public CursorPage(List<T> content, String nextCursor, boolean hasNext, int size) {
        this.content = Objects.requireNonNull(content, "content must not be null");
        this.nextCursor = nextCursor;
        this.hasNext = hasNext;
        
        if (size < 0) {
            throw new IllegalArgumentException("size must not be negative");
        }
        this.size = size;
    }

    /**
     * 빈 페이지를 생성합니다.
     *
     * @param size 요청된 페이지 크기
     * @param <T> 데이터 타입
     * @return 빈 커서 페이지
     */
    public static <T> CursorPage<T> empty(int size) {
        return new CursorPage<>(List.of(), null, false, size);
    }
}
