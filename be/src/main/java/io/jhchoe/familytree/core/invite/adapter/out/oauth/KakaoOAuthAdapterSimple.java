package io.jhchoe.familytree.core.invite.adapter.out.oauth;

import io.jhchoe.familytree.common.auth.domain.KakaoUserInfo;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.invite.application.port.out.FindKakaoProfilePort;
import io.jhchoe.familytree.core.invite.exception.InviteExceptionCode;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 간소화된 카카오 OAuth 어댑터입니다.
 * Authorization Code로 Access Token을 발급받고, 사용자 정보를 조회합니다.
 * Spring Security OAuth2 설정값을 재사용하지만, 복잡한 OAuth2 Client 기능은 사용하지 않습니다.
 */
@Slf4j
@Primary  // 기존 KakaoOAuthAdapter 대신 이것을 사용
@Component
public class KakaoOAuthAdapterSimple implements FindKakaoProfilePort {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret:}")  // client-secret은 optional
    private String clientSecret;
    
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri:https://kauth.kakao.com/oauth/token}")
    private String tokenUri;
    
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri:https://kapi.kakao.com/v2/user/me}")
    private String userInfoUri;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public KakaoUserInfo findProfile(String authCode, String redirectUri) {
        Objects.requireNonNull(authCode, "authCode must not be null");
        Objects.requireNonNull(redirectUri, "redirectUri must not be null");
        
        try {
            // 1. Authorization Code로 Access Token 발급
            String accessToken = getAccessToken(authCode, redirectUri);
            
            // 2. Access Token으로 사용자 정보 조회
            return getUserInfo(accessToken);
            
        } catch (Exception e) {
            log.error("카카오 프로필 조회 실패: {}", e.getMessage(), e);
            throw new FTException(InviteExceptionCode.KAKAO_OAUTH_FAILED);
        }
    }
    
    /**
     * 카카오 인증 코드로 액세스 토큰을 발급받습니다.
     */
    private String getAccessToken(String authCode, String redirectUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        if (clientSecret != null && !clientSecret.isEmpty()) {
            params.add("client_secret", clientSecret);
        }
        params.add("redirect_uri", redirectUri);
        params.add("code", authCode);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);
        
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to get access token from Kakao");
        }
        
        String accessToken = (String) response.getBody().get("access_token");
        if (accessToken == null) {
            throw new RuntimeException("Access token is null");
        }
        
        log.debug("카카오 액세스 토큰 발급 성공");
        return accessToken;
    }
    
    /**
     * 액세스 토큰으로 카카오 사용자 정보를 조회합니다.
     */
    private KakaoUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            userInfoUri,
            HttpMethod.GET,
            request,
            Map.class
        );
        
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to get user info from Kakao");
        }
        
        log.debug("카카오 사용자 정보 조회 성공: {}", response.getBody().get("id"));
        return new KakaoUserInfo(response.getBody());
    }
}