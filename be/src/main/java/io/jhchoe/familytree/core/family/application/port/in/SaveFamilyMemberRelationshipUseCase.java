package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.common.exception.FTException;

/**
 * 가족 관계 정의를 위한 유스케이스 인터페이스입니다.
 */
public interface SaveFamilyMemberRelationshipUseCase {

    /**
     * 가족 관계를 저장합니다.
     *
     * @param command 가족 관계 저장에 필요한 정보를 담은 커맨드 객체
     * @return 저장된 가족 관계의 ID
     * @throws FTException 현재 사용자가 가족의 구성원이 아닌 경우 또는 관계 저장에 실패한 경우
     */
    Long save(SaveFamilyMemberRelationshipCommand command);
}
