package io.jhchoe.familytree.core.invite.application.port.in;

/**
 * SaveFamilyInviteUseCase는 가족 초대 생성을 위한 유스케이스를 정의하는 인터페이스입니다.
 */
public interface SaveFamilyInviteUseCase {
    
    /**
     * 새로운 가족 초대를 생성합니다.
     *
     * @param command 가족 초대 생성에 필요한 입력 데이터를 포함하는 커맨드 객체
     * @return 생성된 초대 코드
     */
    String save(SaveFamilyInviteCommand command);
}