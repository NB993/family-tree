package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.common.exception.FTException;

/**
 * 멤버에 태그를 할당/해제하기 위한 유스케이스 인터페이스입니다.
 */
public interface ModifyMemberTagsUseCase {

    /**
     * 멤버에 태그를 할당합니다.
     * <p>
     * 전체 교체 방식으로 동작합니다: 빈 tagIds 전달 시 모든 태그가 해제됩니다.
     *
     * @param command       태그 할당에 필요한 정보를 담은 커맨드 객체
     * @param currentUserId 현재 사용자 ID
     * @return 수정된 멤버 태그 정보
     * @throws FTException 권한이 없거나, 멤버/태그가 존재하지 않는 경우
     */
    MemberTagsInfo modify(final ModifyMemberTagsCommand command, final Long currentUserId);
}
