package io.jhchoe.familytree.core.family.domain;

import java.time.LocalDateTime;
import lombok.Getter;

/**
 * Family를 나타내는 클래스입니다. 이 클래스는 Family의 주요 구성 요소인 ID, 이름, 설명, 프로필 URL, 공개 여부, 생성자 정보, 생성 날짜, 수정자 정보, 수정 날짜를 포함하며, Family 객체 관리 및
 * 동작 수행을 위한 메서드도 제공합니다.
 */
@Getter
public class Family {

    private Long id;
    private String name;
    private String description;
    private String profileUrl;
    private Boolean isPublic;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long modifiedBy;
    private LocalDateTime modifiedAt;


    /**
     * Family 객체를 생성하는 생성자입니다.
     *
     * @param id          Family의 고유 식별자
     * @param name        Family의 이름
     * @param description Family의 간단한 설명
     * @param profileUrl  Family의 프로필 이미지 URL
     * @param isPublic    Family의 공개 여부 (true: 공개, false: 비공개)
     * @param createdBy   Family를 최초로 생성한 사용자의 ID
     * @param createdAt   Family가 생성된 날짜와 시간
     * @param modifiedBy  Family를 마지막으로 수정한 사용자의 ID
     * @param modifiedAt  Family가 마지막으로 수정된 날짜와 시간
     */
    private Family(
        Long id,
        String name,
        String description,
        String profileUrl,
        Boolean isPublic,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.profileUrl = profileUrl;
        this.isPublic = isPublic;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }


    /**
     * ID와 기타 상세 정보를 사용하여 Family 객체를 생성하는 정적 팩터리 메서드입니다.
     *
     * @param id          Family의 고유 식별자
     * @param name        Family의 이름
     * @param description Family의 간단한 설명
     * @param profileUrl  Family의 프로필 이미지 URL
     * @param isPublic    Family의 공개 여부 (true: 공개, false: 비공개)
     * @param createdBy   Family를 최초로 생성한 사용자의 ID
     * @param createdAt   Family가 생성된 날짜와 시간
     * @param modifiedBy  Family를 마지막으로 수정한 사용자의 ID
     * @param modifiedAt  Family가 마지막으로 수정된 날짜와 시간
     * @return 생성된 Family 객체
     */
    public static Family withId(
        Long id,
        String name,
        String description,
        String profileUrl,
        Boolean isPublic,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        return new Family(id, name, description, profileUrl, isPublic, createdBy, createdAt, modifiedBy, modifiedAt);
    }


    /**
     * 초기 값으로 Family 객체를 생성하는 정적 팩터리 메서드입니다. ID, 생성자 정보, 생성 날짜, 수정자 정보 및 수정 날짜는 null로 설정됩니다.
     *
     * @param name        Family의 이름
     * @param description Family의 간단한 설명
     * @param profileUrl  Family의 프로필 이미지 URL
     * @param isPublic    Family의 공개 여부 (true: 공개, false: 비공개)
     * @return 초기화된 새로운 Family 객체
     */
    public static Family newFamily(
        String name,
        String description,
        String profileUrl,
        Boolean isPublic
    ) {
        return new Family(null, name, description, profileUrl, isPublic, null, null, null, null);
    }


    /**
     * Family의 이름, 설명, 프로필 URL 및 공개 여부를 업데이트합니다.
     *
     * @param name        변경할 Family 이름
     * @param description 변경할 Family 설명
     * @param profileUrl  변경할 Family 프로필 이미지 URL
     * @param isPublic    변경할 Family 공개 여부
     */
    public void update(String name, String description, String profileUrl, Boolean isPublic) {
        this.name = name;
        this.description = description;
        this.profileUrl = profileUrl;
        this.isPublic = isPublic;
    }
}
