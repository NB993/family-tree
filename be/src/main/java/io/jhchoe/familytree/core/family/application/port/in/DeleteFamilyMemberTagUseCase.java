package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.common.exception.FTException;

/**
 * 태그 삭제를 위한 유스케이스 인터페이스입니다.
 */
public interface DeleteFamilyMemberTagUseCase {

    /**
     * 태그를 삭제합니다.
     * 태그 삭제 시 연결된 멤버와의 매핑도 함께 삭제됩니다.
     *
     * @param command       태그 삭제에 필요한 정보를 담은 커맨드 객체
     * @param currentUserId 현재 사용자 ID
     * @throws FTException 가족이 존재하지 않거나, OWNER 권한이 없거나, 태그가 존재하지 않는 경우
     */
    void delete(DeleteFamilyMemberTagCommand command, Long currentUserId);
}
