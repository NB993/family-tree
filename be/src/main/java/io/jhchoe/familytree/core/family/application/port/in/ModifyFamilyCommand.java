package io.jhchoe.familytree.core.family.application.port.in;

import lombok.Getter;

/**
 * Family 엔티티 수정을 위한 커맨드 클래스입니다.
 * <p>
 * 이 클래스는 사용자의 Family 수정 요청 데이터를 캡슐화하고,
 * 입력 데이터의 유효성을 검증하는 책임을 가집니다.
 * </p>
 */

@Getter
public class ModifyFamilyCommand {

    private final Long id;
    private final String name;
    private final String profileUrl;
    private final String description;

    public ModifyFamilyCommand(Long id, String name, String profileUrl, String description) {
        validateId(id);
        validateName(name);
        validateProfileUrl(profileUrl);
        validateDescription(description);

        this.id = id;
        this.name = name;
        this.profileUrl = profileUrl;
        this.description = description;
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Family ID는 필수입니다.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("유효하지 않은 Family ID입니다.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Family 이름을 입력해주세요.");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Family 이름은 100자 이내로 작성해주세요.");
        }
    }

    private void validateProfileUrl(String profileUrl) {
        if (profileUrl != null && !profileUrl.isBlank()) {
            try {
                new java.net.URL(profileUrl);
            } catch (Exception e) {
                throw new IllegalArgumentException("프로필 URL 형식이 유효하지 않습니다.");
            }
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > 200) {
            throw new IllegalArgumentException("Family 설명은 200자 이내로 작성해주세요.");
        }
    }
}
