package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.common.exception.FTException;

/**
 * 태그 생성을 위한 유스케이스 인터페이스입니다.
 */
public interface SaveFamilyMemberTagUseCase {

    /**
     * 새로운 태그를 생성합니다.
     *
     * @param command       태그 생성에 필요한 정보를 담은 커맨드 객체
     * @param currentUserId 현재 사용자 ID
     * @return 생성된 태그의 ID
     * @throws FTException 가족이 존재하지 않거나, OWNER 권한이 없거나, 태그 수 제한 초과 시
     */
    Long save(SaveFamilyMemberTagCommand command, Long currentUserId);
}
