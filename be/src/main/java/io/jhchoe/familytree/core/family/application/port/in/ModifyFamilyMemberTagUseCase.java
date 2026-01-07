package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.common.exception.FTException;

/**
 * 태그 수정을 위한 유스케이스 인터페이스입니다.
 */
public interface ModifyFamilyMemberTagUseCase {

    /**
     * 태그 정보를 수정합니다 (이름, 색상).
     *
     * @param command       태그 수정에 필요한 정보를 담은 커맨드 객체
     * @param currentUserId 현재 사용자 ID
     * @return 수정된 태그 정보
     * @throws FTException 가족이 존재하지 않거나, OWNER 권한이 없거나, 태그가 존재하지 않는 경우
     */
    FamilyMemberTagInfo modify(ModifyFamilyMemberTagCommand command, Long currentUserId);
}
