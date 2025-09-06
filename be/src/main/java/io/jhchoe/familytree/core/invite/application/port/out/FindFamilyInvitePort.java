package io.jhchoe.familytree.core.invite.application.port.out;

import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import java.util.List;
import java.util.Optional;

/**
 * FindFamilyInvitePort는 가족 초대를 조회하기 위한 아웃바운드 포트 인터페이스입니다.
 */
public interface FindFamilyInvitePort {
    
    /**
     * ID로 가족 초대를 조회합니다.
     *
     * @param id 조회할 초대 ID
     * @return 조회된 가족 초대 옵셔널 객체
     */
    Optional<FamilyInvite> findById(Long id);
    
    /**
     * 초대 코드로 가족 초대를 조회합니다.
     *
     * @param inviteCode 조회할 초대 코드
     * @return 조회된 가족 초대 옵셔널 객체
     */
    Optional<FamilyInvite> findByInviteCode(String inviteCode);
    
    /**
     * 요청자 ID로 모든 초대를 조회합니다.
     *
     * @param requesterId 조회할 요청자 ID
     * @return 조회된 초대 목록
     */
    List<FamilyInvite> findByRequesterId(Long requesterId);
}