package io.jhchoe.familytree.core.invite.application.port.in;

import java.util.Objects;
import lombok.Getter;

/**
 * 카카오 OAuth를 통한 초대 응답 저장 커맨드입니다.
 *
 * OAuth 인증 후 프론트엔드에서 받은 사용자 정보를 포함합니다.
 */
@Getter
public class SaveInviteResponseWithKakaoCommand {

    private final String inviteCode;
    private final String kakaoId;
    private final String email;
    private final String name;
    private final String profileUrl;

    /**
     * SaveInviteResponseWithKakaoCommand 생성자입니다.
     *
     * @param inviteCode 초대 코드
     * @param kakaoId 카카오 사용자 ID
     * @param email 사용자 이메일
     * @param name 사용자 이름
     * @param profileUrl 프로필 이미지 URL (nullable)
     */
    public SaveInviteResponseWithKakaoCommand(
        String inviteCode,
        String kakaoId,
        String email,
        String name,
        String profileUrl
    ) {
        Objects.requireNonNull(inviteCode, "inviteCode must not be null");
        Objects.requireNonNull(kakaoId, "kakaoId must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(name, "name must not be null");

        if (inviteCode.isBlank()) {
            throw new IllegalArgumentException("inviteCode must not be blank");
        }
        if (kakaoId.isBlank()) {
            throw new IllegalArgumentException("kakaoId must not be blank");
        }
        if (email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        this.inviteCode = inviteCode;
        this.kakaoId = kakaoId;
        this.email = email;
        this.name = name;
        this.profileUrl = profileUrl;
    }
}