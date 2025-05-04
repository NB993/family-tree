package io.jhchoe.familytree.common.auth.domain;

/**
 * OAuth2 사용자 정보 추상화 인터페이스
 */
public interface OAuth2UserInfo {

    String getId();
    String getName();
    String getEmail();
    String getImageUrl();
}
