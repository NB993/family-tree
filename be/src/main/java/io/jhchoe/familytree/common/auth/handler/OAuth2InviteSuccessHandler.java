package io.jhchoe.familytree.common.auth.handler;

import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.config.CorsProperties;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.invite.application.port.in.SaveInviteResponseWithKakaoCommand;
import io.jhchoe.familytree.core.invite.application.port.in.SaveInviteResponseWithKakaoUseCase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 초대 수락용 OAuth2 인증 성공 핸들러입니다.
 *
 * JWT 토큰을 발급하지 않고, 사용자 정보를 임시 쿠키에 저장한 후
 * 프론트엔드 초대 콜백 페이지로 리다이렉트합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2InviteSuccessHandler implements AuthenticationSuccessHandler {

    private final CorsProperties corsProperties;
    private final SaveInviteResponseWithKakaoUseCase saveInviteResponseWithKakaoUseCase;

    /**
     * OAuth2 인증 성공 시 초대 수락 프로세스를 진행합니다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authentication 인증 정보
     * @throws IOException 리다이렉트 실패 시
     */
    @Override
    public void onAuthenticationSuccess(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final Authentication authentication
    ) throws IOException {

        // 1. FTUser 추출
        final FTUser ftUser = (FTUser) authentication.getPrincipal();

        // 2. OAuth2AuthorizationRequest에서 초대 코드 읽기
        final String inviteCode = extractInviteCodeFromAuthorizationRequest(request);

        if (inviteCode == null || inviteCode.isBlank()) {
            log.error("초대 코드가 없음. 에러 페이지로 리다이렉트");
            handleError(response, null, "초대 코드를 찾을 수 없습니다.");
            return;
        }

        try {
            // 3. Kakao OAuth attributes에서 정보 추출
            final String kakaoId = extractKakaoId(ftUser);
            final String profileUrl = extractProfileUrl(ftUser);

            // 4. SaveInviteResponseWithKakaoCommand 생성
            final SaveInviteResponseWithKakaoCommand command =
                new SaveInviteResponseWithKakaoCommand(
                    inviteCode,
                    kakaoId,
                    ftUser.getEmail(),
                    ftUser.getName(),
                    profileUrl
                );

            // 5. 초대 수락 처리 (UseCase 호출)
            final Long memberId = saveInviteResponseWithKakaoUseCase.save(command);

            log.info("초대 수락 완료 [inviteCode: {}] [memberId: {}] [email: {}]",
                     inviteCode, memberId, ftUser.getEmail());

            // 6. 성공 리다이렉트
            final String redirectUrl = corsProperties.getFrontendUrl()
                + "/invite/" + inviteCode + "/callback?success=true";

            response.sendRedirect(redirectUrl);

        } catch (FTException e) {
            // 비즈니스 예외 처리
            log.error("초대 수락 실패 [inviteCode: {}] [error: {}]",
                      inviteCode, e.getCode(), e);
            handleError(response, inviteCode, e.getMessage());

        } catch (Exception e) {
            // 시스템 예외 처리
            log.error("초대 수락 중 시스템 오류 발생 [inviteCode: {}]", inviteCode, e);
            handleError(response, inviteCode, "초대 수락 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * Kakao OAuth attributes에서 Kakao ID를 추출합니다.
     *
     * @param ftUser 인증된 사용자 정보
     * @return Kakao ID
     */
    private String extractKakaoId(final FTUser ftUser) {
        final Object id = ftUser.getAttributes().get("id");
        if (id != null) {
            return id.toString();
        }
        log.warn("Kakao ID를 찾을 수 없습니다. 빈 문자열 반환");
        return "";
    }

    /**
     * Kakao OAuth attributes에서 프로필 이미지 URL을 추출합니다.
     *
     * @param ftUser 인증된 사용자 정보
     * @return 프로필 이미지 URL
     */
    @SuppressWarnings("unchecked")
    private String extractProfileUrl(final FTUser ftUser) {
        final Object kakaoAccount = ftUser.getAttributes().get("kakao_account");
        if (kakaoAccount instanceof java.util.Map) {
            final java.util.Map<String, Object> accountMap = (java.util.Map<String, Object>) kakaoAccount;
            final Object profile = accountMap.get("profile");

            if (profile instanceof java.util.Map) {
                final java.util.Map<String, Object> profileMap = (java.util.Map<String, Object>) profile;
                final Object profileImageUrl = profileMap.get("profile_image_url");

                if (profileImageUrl != null) {
                    return profileImageUrl.toString();
                }
            }
        }

        log.debug("프로필 이미지 URL을 찾을 수 없습니다. 빈 문자열 반환");
        return "";
    }

    /**
     * OAuth2AuthorizationRequest의 additionalParameters에서 invite_code를 추출합니다.
     *
     * @param request HTTP 요청
     * @return 초대 코드 (없으면 null)
     */
    private String extractInviteCodeFromAuthorizationRequest(final HttpServletRequest request) {
        try {
            // 1. 쿠키에서 oauth2_auth_request 추출
            final Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                return null;
            }

            Cookie authRequestCookie = null;
            for (Cookie cookie : cookies) {
                if ("oauth2_auth_request".equals(cookie.getName())) {
                    authRequestCookie = cookie;
                    break;
                }
            }

            if (authRequestCookie == null) {
                log.debug("oauth2_auth_request 쿠키를 찾을 수 없습니다");
                return null;
            }

            // 2. OAuth2AuthorizationRequest 역직렬화
            final String value = authRequestCookie.getValue();
            if (value == null || value.isEmpty()) {
                return null;
            }

            final byte[] decoded = Base64.getUrlDecoder().decode(value);
            final OAuth2AuthorizationRequest authorizationRequest =
                (OAuth2AuthorizationRequest) SerializationUtils.deserialize(decoded);

            if (authorizationRequest == null) {
                return null;
            }

            // 3. additionalParameters에서 invite_code 추출
            final Object inviteCode = authorizationRequest
                .getAdditionalParameters()
                .get("invite_code");

            if (inviteCode != null) {
                log.debug("OAuth2 인증 요청에서 초대 코드 추출 성공 [inviteCode: {}]", inviteCode);
                return inviteCode.toString();
            }

            return null;

        } catch (Exception e) {
            log.error("OAuth2 인증 요청에서 초대 코드 추출 실패", e);
            return null;
        }
    }

    /**
     * 에러 발생 시 프론트엔드 콜백 페이지로 에러와 함께 리다이렉트합니다.
     *
     * @param response HTTP 응답
     * @param inviteCode 초대 코드 (없으면 null)
     * @param errorMessage 에러 메시지
     * @throws IOException 리다이렉트 실패 시
     */
    private void handleError(
        final HttpServletResponse response,
        final String inviteCode,
        final String errorMessage
    ) throws IOException {
        final String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // inviteCode가 있으면 해당 초대 페이지로, 없으면 일반 콜백으로
        final String errorUrl = inviteCode != null && !inviteCode.isBlank()
            ? corsProperties.getFrontendUrl() + "/invite/" + inviteCode + "/callback?error=" + encodedMessage
            : corsProperties.getFrontendUrl() + "/invite/callback?error=" + encodedMessage;

        log.warn("초대 수락 OAuth 실패. 에러 페이지 리다이렉트: {} [error: {}]",
                 errorUrl, errorMessage);
        response.sendRedirect(errorUrl);
    }
}
