package io.jhchoe.familytree.core.invite.application.port.in;

import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import java.util.List;

/**
 * FindFamilyInviteUseCase는 가족 초대 조회를 위한 유스케이스를 정의하는 인터페이스입니다.
 */
public interface FindFamilyInviteUseCase {
    
    /**
     * 초대 코드로 가족 초대를 조회합니다.
     *
     * @param query 가족 초대 조회에 필요한 입력 데이터를 포함하는 쿼리 객체
     * @return 조회된 가족 초대 객체
     */
    FamilyInvite find(FindFamilyInviteByCodeQuery query);
    
    /**
     * ID로 가족 초대를 조회합니다.
     *
     * @param query 가족 초대 조회에 필요한 입력 데이터를 포함하는 쿼리 객체
     * @return 조회된 가족 초대 객체
     */
    FamilyInvite find(FindFamilyInviteByIdQuery query);
    
    /**
     * 요청자 ID로 모든 초대를 조회합니다.
     *
     * @param query 초대 목록 조회에 필요한 입력 데이터를 포함하는 쿼리 객체
     * @return 조회된 초대 목록
     */
    List<FamilyInvite> findAll(FindFamilyInvitesByRequesterIdQuery query);
}