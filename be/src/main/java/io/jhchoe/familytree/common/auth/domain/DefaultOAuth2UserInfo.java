package io.jhchoe.familytree.common.auth.domain;

import java.util.Map;

/**
 * 기본 OAuth2UserInfo 구현체
 * 속성 맵이 없거나 필요한 정보가 없는 경우를 위한 기본 구현
 */
public class DefaultOAuth2UserInfo implements OAuth2UserInfo {
    private final String name;
    private final String email;
    private final Map<String, Object> attributes;
    
    public DefaultOAuth2UserInfo(String name, String email, Map<String, Object> attributes) {
        this.name = name;
        this.email = email;
        this.attributes = attributes;
    }
    
    @Override
    public String getId() {
        Object id = attributes.get("sub");
        if (id == null) {
            id = attributes.get("id");
        }
        return id != null ? String.valueOf(id) : "";
    }
    
    @Override
    public String getName() {
        return name != null ? name : (String) attributes.getOrDefault("name", "");
    }
    
    @Override
    public String getEmail() {
        return email != null ? email : (String) attributes.getOrDefault("email", "");
    }
    
    @Override
    public String getImageUrl() {
        return (String) attributes.getOrDefault("picture", "");
    }
}
