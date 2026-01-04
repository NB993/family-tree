package io.jhchoe.familytree.common.auth.service;

import io.jhchoe.familytree.common.auth.UserJpaEntity;
import io.jhchoe.familytree.common.auth.UserJpaRepository;
import io.jhchoe.familytree.common.auth.domain.*;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.common.util.MaskingUtils;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyUseCase;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import io.jhchoe.familytree.core.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * OAuth2 인증 후 사용자 정보를 처리하는 서비스
 */
@Slf4j
@Service
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserJpaRepository userJpaRepository;
    private final SaveFamilyUseCase saveFamilyUseCase;

    public OAuth2UserServiceImpl(UserJpaRepository userJpaRepository, SaveFamilyUseCase saveFamilyUseCase) {
        super();
        this.userJpaRepository = userJpaRepository;
        this.saveFamilyUseCase = saveFamilyUseCase;
    }

    /**
     * OAuth2 인증 후 사용자 정보를 로드하고 필요한 경우 새로운 사용자를 생성합니다.
     *
     * @param userRequest OAuth2 인증 요청 정보를 포함한 객체
     * @return FTUser 사용자 정보
     * @throws OAuth2AuthenticationException 인증 과정에서 발생한 예외
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oAuth2User = super.loadUser(userRequest);

            // OAuth2 제공자 결정 (registrationId로 구분: "google", "kakao" 등)
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            OAuth2Provider provider = getProvider(registrationId);

            // OAuth2 제공자별 사용자 속성 파싱
            OAuth2UserInfo userInfo = extractUserInfo(registrationId, oAuth2User.getAttributes());

            // DB에서 사용자 조회 또는 생성
            UserJpaEntity userEntity = userJpaRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> {
                    log.info("새로운 OAuth2 사용자 생성: [Provider: {}] [Masked Email: {}] [Masked Name: {}]",
                        provider,
                        MaskingUtils.maskEmail(userInfo.getEmail()),
                        userInfo.getName() != null ? MaskingUtils.maskName(userInfo.getName()) : "없음");
                    return createUser(userInfo, provider);
                });

            // 다른 OAuth2 제공자로 이미 가입한 경우 기존에 가입한 제공자로 로그인 유도
            if (!userEntity.getOAuth2Provider().equals(provider)) {
                // todo 원티드처럼 기존에 가입한 provider 로 로그인할 수 있게 페이지를 전환해주는 방식?
                throw FTException.DUPLICATED;
            }

            log.info("OAuth2 로그인 성공: [User ID: {}] [Provider: {}]", userEntity.getId(), provider);
            return FTUser.ofOAuth2User(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                provider,
                oAuth2User.getAttributes()
            );
        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생: [Provider: {}] [Error: {}]",
                userRequest.getClientRegistration().getRegistrationId(),
                e.getMessage(),
                e);
            throw e;
        }
    }

    /**
     * 새로운 OAuth2 사용자를 생성하고 자동으로 Family를 생성합니다.
     */
    private UserJpaEntity createUser(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        // 1. User 생성 및 저장 (kakaoId, birthday 포함)
        String kakaoId = userInfo.getId();
        LocalDateTime birthday = extractBirthday(userInfo, provider);

        User user = User.newUser(
            userInfo.getEmail(),
            userInfo.getName(),
            userInfo.getImageUrl(),
            kakaoId,
            AuthenticationType.OAUTH2,
            provider,
            UserRole.USER,
            false,
            birthday
        );
        UserJpaEntity savedUser = userJpaRepository.save(UserJpaEntity.ofOAuth2User(user));

        // 2. Family 자동 생성
        try {
            String familyName = savedUser.getName() + "의 가족";
            String familyDescription = savedUser.getName() + "님의 가족 공간입니다";
            
            SaveFamilyCommand familyCommand = new SaveFamilyCommand(
                savedUser.getId(),
                familyName,
                savedUser.getProfileUrl(),
                familyDescription,
                false  // 기본값: 비공개
            );
            
            Long familyId = saveFamilyUseCase.save(familyCommand);
            log.info("회원가입 시 Family 자동 생성 완료: userId={}, familyId={}", 
                savedUser.getId(), familyId);
        } catch (Exception e) {
            log.error("Family 자동 생성 실패: userId={}, error={}", 
                savedUser.getId(), e.getMessage());
            throw new FTException(FamilyExceptionCode.FAMILY_AUTO_CREATION_FAILED);
        }
        
        return savedUser;
    }

    /**
     * 제공자 ID(registrationId)를 기반으로 OAuth2Provider 열거형을 반환합니다.
     */
    private OAuth2Provider getProvider(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> OAuth2Provider.GOOGLE;
            case "kakao" -> OAuth2Provider.KAKAO;
            default -> {
                log.warn("지원하지 않는 OAuth2 제공자 요청: {}", registrationId);
                throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
            }
        };
    }

    /**
     * OAuth2 제공자별로 다른 사용자 정보 구조를 처리합니다.
     */
    private OAuth2UserInfo extractUserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleUserInfo(attributes);
            case "kakao" -> new KakaoUserInfo(attributes);
            default -> {
                log.warn("지원하지 않는 OAuth2 제공자의 사용자 정보 처리 요청: {}", registrationId);
                throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
            }
        };
    }

    /**
     * OAuth2 제공자별로 생년월일 정보를 추출합니다.
     * 카카오 로그인의 경우 생년월일을 반환하고, 그 외에는 null을 반환합니다.
     *
     * @param userInfo OAuth2 사용자 정보
     * @param provider OAuth2 제공자
     * @return 생년월일 (LocalDateTime) 또는 null
     */
    private LocalDateTime extractBirthday(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        if (provider == OAuth2Provider.KAKAO && userInfo instanceof KakaoUserInfo kakaoUserInfo) {
            LocalDate birthDate = kakaoUserInfo.getBirthDate();
            if (birthDate != null) {
                return birthDate.atStartOfDay();
            }
        }
        return null;
    }
}

