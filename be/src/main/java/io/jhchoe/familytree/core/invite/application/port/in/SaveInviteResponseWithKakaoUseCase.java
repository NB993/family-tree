package io.jhchoe.familytree.core.invite.application.port.in;

/**
 * 카카오 OAuth를 통한 초대 응답 저장 유스케이스입니다.
 */
public interface SaveInviteResponseWithKakaoUseCase {
    
    /**
     * 카카오 OAuth 인증을 통해 초대에 응답하고 FamilyMember를 생성합니다.
     *
     * @param command 초대 응답 저장에 필요한 정보
     * @return 생성된 FamilyMember의 ID
     */
    Long save(SaveInviteResponseWithKakaoCommand command);
}