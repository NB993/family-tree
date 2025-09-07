package io.jhchoe.familytree.core.invite.application.port.out;

import io.jhchoe.familytree.common.auth.domain.KakaoUserInfo;

/**
 * 카카오 사용자 프로필 조회 포트입니다.
 */
public interface FindKakaoProfilePort {
    
    /**
     * 카카오 인증 코드로 액세스 토큰을 발급받고 사용자 프로필을 조회합니다.
     *
     * @param authCode 카카오 인증 코드
     * @param redirectUri 리다이렉트 URI
     * @return 카카오 사용자 정보
     */
    KakaoUserInfo findProfile(String authCode, String redirectUri);
}