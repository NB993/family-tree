package io.jhchoe.familytree.core.invite.adapter.out.oauth;

import io.jhchoe.familytree.common.auth.domain.KakaoUserInfo;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.invite.application.port.out.FindKakaoProfilePort;
import io.jhchoe.familytree.core.invite.exception.InviteExceptionCode;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 카카오 OAuth 어댑터입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuthAdapter implements FindKakaoProfilePort {
    
    private final RestTemplate restTemplate;
    
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;
    
    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public KakaoUserInfo findProfile(String authCode, String redirectUri) {
        Objects.requireNonNull(authCode, "authCode must not be null");
        Objects.requireNonNull(redirectUri, "redirectUri must not be null");
        
        try {
            // 1. 인증 코드로 액세스 토큰 발급
            String accessToken = getAccessToken(authCode, redirectUri);
            
            // 2. 액세스 토큰으로 사용자 정보 조회
            return getUserInfo(accessToken);
            
        } catch (Exception e) {
            log.error("카카오 프로필 조회 실패: {}", e.getMessage(), e);
            throw new FTException(InviteExceptionCode.KAKAO_OAUTH_FAILED);
        }
    }
    
    /**
     * 카카오 인증 코드로 액세스 토큰을 발급받습니다.
     *
     * @param authCode 인증 코드
     * @param redirectUri 리다이렉트 URI
     * @return 액세스 토큰
     */
    private String getAccessToken(String authCode, String redirectUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", authCode);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);
        
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("Failed to get access token from Kakao");
        }
        
        String accessToken = (String) response.getBody().get("access_token");
        if (accessToken == null) {
            throw new RuntimeException("Access token is null");
        }
        
        log.info("카카오 액세스 토큰 발급 성공");
        return accessToken;
    }
    
    /**
     * 액세스 토큰으로 카카오 사용자 정보를 조회합니다.
     *
     * @param accessToken 액세스 토큰
     * @return 카카오 사용자 정보
     */
    private KakaoUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            USER_INFO_URL, 
            HttpMethod.GET, 
            request, 
            Map.class
        );
        
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("Failed to get user info from Kakao");
        }
        
        log.info("카카오 사용자 정보 조회 성공");
        return new KakaoUserInfo(response.getBody());
    }
}