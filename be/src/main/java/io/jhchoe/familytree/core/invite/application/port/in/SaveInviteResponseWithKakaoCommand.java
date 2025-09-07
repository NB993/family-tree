package io.jhchoe.familytree.core.invite.application.port.in;

import java.util.Objects;
import lombok.Getter;

/**
 * 카카오 OAuth를 통한 초대 응답 저장 커맨드입니다.
 */
@Getter
public class SaveInviteResponseWithKakaoCommand {
    
    private final String inviteCode;
    private final String kakaoAuthCode;
    private final String redirectUri;
    
    /**
     * SaveInviteResponseWithKakaoCommand 생성자입니다.
     *
     * @param inviteCode 초대 코드
     * @param kakaoAuthCode 카카오 인증 코드
     * @param redirectUri 리다이렉트 URI
     */
    public SaveInviteResponseWithKakaoCommand(
        String inviteCode,
        String kakaoAuthCode,
        String redirectUri
    ) {
        Objects.requireNonNull(inviteCode, "inviteCode must not be null");
        Objects.requireNonNull(kakaoAuthCode, "kakaoAuthCode must not be null");
        Objects.requireNonNull(redirectUri, "redirectUri must not be null");
        
        if (inviteCode.isBlank()) {
            throw new IllegalArgumentException("inviteCode must not be blank");
        }
        if (kakaoAuthCode.isBlank()) {
            throw new IllegalArgumentException("kakaoAuthCode must not be blank");
        }
        if (redirectUri.isBlank()) {
            throw new IllegalArgumentException("redirectUri must not be blank");
        }
        
        this.inviteCode = inviteCode;
        this.kakaoAuthCode = kakaoAuthCode;
        this.redirectUri = redirectUri;
    }
}