package io.jhchoe.familytree.core.family.application.port.in;

import lombok.Getter;

/**
 * Family 정보를 생성하기 위한 명령(Class)입니다.
 * 이 클래스는 Family의 이름, 프로필 URL, 설명, 공개 여부 정보를 포함합니다.
 */
@Getter
public class SaveFamilyCommand {

    private final String name;
    private String profileUrl;
    private String description;
    private final Boolean isPublic;

    /**
     * Family 생성 명령을 위한 생성자(Constructor)입니다.
     * 
     * @param name Family 이름 (필수, 최대 길이: 20자) - null 또는 공백일 수 없습니다.
     * @param profileUrl 프로필 URL (선택) - 반드시 "http://" 또는 "https://"로 시작해야 합니다.
     * @param description Family 설명 (선택, 최대 길이: 200자)
     * @param isPublic 공개 여부 (필수) - true: 공개, false: 비공개
     * @throws IllegalArgumentException 입력 값이 유효하지 않을 경우 예외가 발생합니다.
     */
    public SaveFamilyCommand(String name, String profileUrl, String description, Boolean isPublic) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Family 이름을 입력해주세요.");
        }
        if (name.length() > 20) {
            throw new IllegalArgumentException("Family 이름은 20자 이내로 작성해주세요.");
        }

        if (profileUrl != null && !profileUrl.isBlank()) {
            if (!profileUrl.startsWith("http://") && !profileUrl.startsWith("https://")) {
                throw new IllegalArgumentException("프로필 URL 형식이 유효하지 않습니다.");
            }
        }

        if (description != null && description.length() > 200) {
            throw new IllegalArgumentException("Family 설명은 200자 이내로 작성해주세요.");
        }

        if (isPublic == null) {
            throw new IllegalArgumentException("공개 여부를 선택해주세요.");
        }

        this.name = name;
        this.profileUrl = profileUrl;
        this.description = description;
        this.isPublic = isPublic;
    }
}
