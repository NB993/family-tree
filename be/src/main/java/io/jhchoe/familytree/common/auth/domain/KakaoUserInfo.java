package io.jhchoe.familytree.common.auth.domain;

import java.util.Map;

/**
 * Kakao OAuth2 사용자 정보 처리
 * PropertyKeys
 * 이름	설명
 * kakao_account.profile	카카오계정의 프로필 소유 여부
 * 실시간 닉네임과 프로필 사진 URL
 * kakao_account.name	카카오계정의 이름 소유 여부, 이름 값
 * kakao_account.email	카카오계정의 이메일 소유 여부
 * 이메일 값, 이메일 인증 여부, 이메일 유효 여부
 * kakao_account.age_range	카카오계정의 연령대 소유 여부, 연령대 값
 * kakao_account.birthday	카카오계정의 생일 소유 여부, 생일 값
 * kakao_account.gender	카카오계정의 성별 소유 여부, 성별 값
 */
public class KakaoUserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;
    private final Map<String, Object> account;
    private final Map<String, Object> profile;
    private final Map<String, Object> properties;

    @SuppressWarnings("unchecked")
    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.account = (Map<String, Object>) attributes.get("kakao_account");
        this.profile = account != null ? (Map<String, Object>) account.get("profile") : null;
        this.properties = (Map<String, Object>) attributes.get("properties");
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getName() {
        if (profile != null && profile.get("nickname") != null) {
            return (String) profile.get("nickname");
        }
        
        // 카카오 계정 정보가 없으면 properties에서 찾아봄
        if (properties != null) {
            return (String) properties.get("nickname");
        }
        
        return "";
    }

    @Override
    public String getEmail() {
        return account != null ? (String) account.get("email") : "";
    }

    @Override
    public String getImageUrl() {
        if (profile != null && profile.get("profile_image_url") != null) {
            return (String) profile.get("profile_image_url");
        }

        // 카카오 계정 정보가 없으면 properties에서 찾아봄
        if (properties != null) {
            return (String) properties.get("profile_image");
        }

        return "";
    }

    /**
     * 카카오 계정의 생일 정보를 반환합니다.
     * 현재는 account_birthday scope가 준비 단계이므로 항상 null을 반환합니다.
     *
     * @return 생일 정보 (현재는 항상 null)
     */
    public String getBirthday() {
        // account_birthday scope는 준비만 하고 실제로 사용하지 않음
        // 향후 필요 시 구현: return account != null ? (String) account.get("birthday") : null;
        return null;
    }
}
