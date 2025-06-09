package io.jhchoe.familytree.core.family.application.port.in;

import lombok.Getter;
import java.util.regex.Pattern;

/**
 * Family 정보를 생성하기 위한 명령(Class)입니다.
 * 이 클래스는 Family의 이름, 프로필 URL, 설명, 공개 여부 정보를 포함합니다.
 */
@Getter
public class SaveFamilyCommand {

    /**
     * 가족명에 허용되지 않는 특수문자를 제외하는 정규표현식입니다.
     * 한글, 영문, 숫자, 공백, 이모지는 허용하고 일반적인 특수문자는 제외합니다.
     */
    private static final Pattern FAMILY_NAME_PATTERN = 
        Pattern.compile("^[^@#$%^&*()+=\\[\\]{}|\\\\:;\"'<>?,./!~`]+$");

    private final Long userId;
    private final String name;
    private String profileUrl;
    private String description;
    private final Boolean isPublic;

    /**
     * Family 생성 명령을 위한 생성자(Constructor)입니다.
     * 
     * @param userId 생성하는 사용자의 ID (필수)
     * @param name Family 이름 (필수, 최대 길이: 20자) - null 또는 공백일 수 없으며, 특수문자는 허용되지 않습니다.
     * @param profileUrl 프로필 URL (선택) - 반드시 "http://" 또는 "https://"로 시작해야 합니다.
     * @param description Family 설명 (선택, 최대 길이: 200자)
     * @param isPublic 공개 여부 (필수) - true: 공개, false: 비공개
     * @throws IllegalArgumentException 입력 값이 유효하지 않을 경우 예외가 발생합니다.
     */
    public SaveFamilyCommand(Long userId, String name, String profileUrl, String description, Boolean isPublic) {
        validateUserId(userId);
        validateFamilyName(name);
        validateProfileUrl(profileUrl);
        validateDescription(description);
        validateIsPublic(isPublic);

        this.userId = userId;
        this.name = name;
        this.profileUrl = profileUrl;
        this.description = description;
        this.isPublic = isPublic;
    }

    /**
     * 사용자 ID의 유효성을 검증합니다.
     * 
     * @param userId 검증할 사용자 ID
     * @throws IllegalArgumentException 사용자 ID가 유효하지 않을 경우
     */
    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("유효한 사용자 ID가 필요합니다.");
        }
    }

    /**
     * 가족명의 유효성을 검증합니다.
     * 
     * @param name 검증할 가족명
     * @throws IllegalArgumentException 가족명이 유효하지 않을 경우
     */
    private void validateFamilyName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("가족명을 입력해주세요.");
        }
        if (name.length() > 20) {
            throw new IllegalArgumentException("가족명은 20자 이하로 입력해주세요.");
        }
        if (!FAMILY_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("가족명에는 한글, 영문, 숫자, 공백, 이모지만 사용 가능합니다.");
        }
    }

    /**
     * 프로필 URL의 유효성을 검증합니다.
     * 
     * @param profileUrl 검증할 프로필 URL
     * @throws IllegalArgumentException 프로필 URL이 유효하지 않을 경우
     */
    private void validateProfileUrl(String profileUrl) {
        if (profileUrl != null && !profileUrl.isBlank()) {
            if (!profileUrl.startsWith("http://") && !profileUrl.startsWith("https://")) {
                throw new IllegalArgumentException("프로필 URL 형식이 유효하지 않습니다.");
            }
        }
    }

    /**
     * 설명의 유효성을 검증합니다.
     * 
     * @param description 검증할 설명
     * @throws IllegalArgumentException 설명이 유효하지 않을 경우
     */
    private void validateDescription(String description) {
        if (description != null && description.length() > 200) {
            throw new IllegalArgumentException("Family 설명은 200자 이내로 작성해주세요.");
        }
    }

    /**
     * 공개 여부의 유효성을 검증합니다.
     * 
     * @param isPublic 검증할 공개 여부
     * @throws IllegalArgumentException 공개 여부가 유효하지 않을 경우
     */
    private void validateIsPublic(Boolean isPublic) {
        if (isPublic == null) {
            throw new IllegalArgumentException("공개 여부를 선택해주세요.");
        }
    }
}
