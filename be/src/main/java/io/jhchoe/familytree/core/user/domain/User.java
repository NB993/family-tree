package io.jhchoe.familytree.core.user.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

/**
 * 사용자 도메인 모델입니다.
 * <p>
 * 이 클래스는 사용자의 기본 정보를 포함하며, 불변 객체로 구현되어 있습니다.
 * </p>
 */
@Getter
public class User {

    private final Long id;
    private final String email;
    private final String name;
    private final String profileUrl;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final Long modifiedBy;
    private final LocalDateTime modifiedAt;

    private User(
        Long id,
        String email,
        String name,
        String profileUrl,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.profileUrl = profileUrl;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }

    /**
     * ID와 함께 User 객체를 생성하는 정적 팩토리 메서드입니다.
     */
    public static User withId(
        Long id,
        String email,
        String name,
        String profileUrl,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(email, "email must not be null");
        
        return new User(
            id,
            email,
            name,
            profileUrl,
            createdBy,
            createdAt,
            modifiedBy,
            modifiedAt
        );
    }

    /**
     * 이름으로 사용자 조회 시 값 비교를 위한 메서드
     * 
     * @param name 비교할 이름
     * @return 이름이 일치하면 true, 그렇지 않으면 false
     */
    public boolean hasName(String name) {
        if (this.name == null && name == null) {
            return true;
        }
        if (this.name == null || name == null) {
            return false;
        }
        return this.name.equals(name);
    }
}
