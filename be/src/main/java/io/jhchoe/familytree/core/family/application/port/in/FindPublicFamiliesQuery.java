package io.jhchoe.familytree.core.family.application.port.in;

import java.util.Objects;
import lombok.Getter;

/**
 * 공개 Family 검색을 위한 쿼리 객체입니다.
 * 
 * <p>키워드 기반 검색과 무한 스크롤을 지원하며, 공개된 Family만 조회합니다.
 * 커서 기반 페이징을 사용하여 안정적인 무한 스크롤을 제공합니다.</p>
 */
@Getter
public class FindPublicFamiliesQuery {

    private final String keyword;
    private final String cursor;
    private final int size;
    private final Long currentUserId;

    /**
     * 공개 Family 검색 쿼리 객체를 생성합니다.
     *
     * @param keyword 검색할 키워드 (가족명 일부 검색)
     * @param cursor 무한 스크롤을 위한 커서 값 (Base64 인코딩된 페이징 정보)
     * @param size 조회할 Family 개수 (1~50 범위)
     * @param currentUserId 현재 사용자 ID (인증된 사용자만 접근 가능)
     * @throws IllegalArgumentException 유효하지 않은 파라미터인 경우
     */
    public FindPublicFamiliesQuery(String keyword, String cursor, int size, Long currentUserId) {
        if (keyword != null && keyword.isBlank()) {
            throw new IllegalArgumentException("키워드는 빈 문자열일 수 없습니다");
        }
        if (size <= 0 || size > 50) {
            throw new IllegalArgumentException("조회 개수는 1~50 사이여야 합니다");
        }
        
        this.keyword = keyword;
        this.cursor = cursor;
        this.size = size;
        this.currentUserId = Objects.requireNonNull(currentUserId, "currentUserId must not be null");
    }
}
