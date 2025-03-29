package io.jhchoe.familytree.core.family.domain;

import java.time.LocalDateTime;
import lombok.Getter;

/**
 * Family를 나타내는 클래스입니다.
 * Family 정보에는 ID, 이름, 설명, 프로필 URL, 생성자 정보, 생성 날짜,
 * 수정자 정보, 수정 날짜가 포함됩니다.
 */
@Getter
public class Family {

    private final Long id;
    private final String name;
    private final String description;
    private final String profileUrl;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final Long modifiedBy;
    private final LocalDateTime modifiedAt;

    
    /**
     * Family 생성자.
     *
     * @param id 고유 식별자(ID)
     * @param name Family 이름
     * @param description Family에 대한 설명
     * @param profileUrl Family의 프로필 이미지 URL
     * @param createdBy 생성자의 ID
     * @param createdAt 생성된 날짜와 시간
     * @param modifiedBy 마지막 수정자의 ID
     * @param modifiedAt 마지막 수정된 날짜와 시간
     */
    private Family(
        Long id,
        String name,
        String description,
        String profileUrl,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.profileUrl = profileUrl;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }

    
    /**
     * ID와 함께 Family를 생성하는 정적 팩터리 메서드입니다.
     *
     * @param id 고유 식별자(ID)
     * @param name Family 이름
     * @param description Family에 대한 설명
     * @param profileUrl Family의 프로필 이미지 URL
     * @param createdBy 생성자의 ID
     * @param createdAt 생성된 날짜와 시간
     * @param modifiedBy 마지막 수정자의 ID
     * @param modifiedAt 마지막 수정된 날짜와 시간
     * @return Family 객체
     */
    public static Family withId(
        Long id,
        String name,
        String description,
        String profileUrl,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        return new Family(id, name, description, profileUrl, createdBy, createdAt, modifiedBy, modifiedAt);
    }


    /**
     * 새로운 Family를 생성하는 정적 팩터리 메서드입니다.
     *
     * @param name Family 이름
     * @param description Family에 대한 설명
     * @param profileUrl Family의 프로필 이미지 URL
     * @return Family 객체
     */
    public static Family newFamily(
        String name,
        String description,
        String profileUrl
    ) {
        return new Family(null, name, description, profileUrl, null, null, null, null);
    }
}
