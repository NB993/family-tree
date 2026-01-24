package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.BirthdayType;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Family 홈 API용 구성원과 관계 정보를 담는 응답 DTO입니다.
 * 구성원의 기본 정보와 관계 정보를 포함합니다.
 *
 * <p>포함 정보:</p>
 * <ul>
 *   <li>구성원 기본 정보 (이름, 나이, 생년월일, 연락처 등)</li>
 *   <li>관계 정보 (FamilyMember의 relationshipType 사용)</li>
 *   <li>관계 표시명 (설정된 관계가 있는 경우)</li>
 *   <li>태그 정보 목록</li>
 * </ul>
 */
public class FamilyMemberWithRelationshipResponse {

    /**
     * 가족 구성원 정보
     */
    private final FamilyMember member;

    /**
     * 관계 표시명 (관계가 설정된 경우 표시할 한글명)
     */
    private final String relationshipDisplayName;

    /**
     * 관계 설정 여부
     */
    private final boolean hasRelationship;

    /**
     * 멤버에 할당된 태그 목록
     */
    private final List<TagInfo> tags;

    /**
     * FamilyMemberWithRelationshipResponse 생성자입니다.
     *
     * @param member 가족 구성원 정보 (null 불허)
     */
    public FamilyMemberWithRelationshipResponse(final FamilyMember member) {
        this(member, Collections.emptyList());
    }

    /**
     * FamilyMemberWithRelationshipResponse 생성자입니다.
     *
     * @param member 가족 구성원 정보 (null 불허)
     * @param tags 멤버에 할당된 태그 목록
     */
    public FamilyMemberWithRelationshipResponse(
        final FamilyMember member,
        final List<TagInfo> tags
    ) {
        Objects.requireNonNull(member, "member must not be null");

        this.member = member;
        this.hasRelationship = member.getRelationshipType() != null;
        this.relationshipDisplayName = member.getRelationshipDisplayName();
        this.tags = tags != null ? tags : Collections.emptyList();
    }

    /**
     * 태그 간단 정보 DTO입니다.
     */
    public record TagInfo(Long id, String name, String color) {
        public static TagInfo from(FamilyMemberTag tag) {
            return new TagInfo(tag.getId(), tag.getName(), tag.getColor());
        }
    }

    /**
     * 가족 구성원 정보를 반환합니다.
     *
     * @return 가족 구성원 정보
     */
    public FamilyMember getMember() {
        return member;
    }

    /**
     * 관계 표시명을 반환합니다.
     *
     * @return 관계 표시명
     */
    public String getRelationshipDisplayName() {
        return relationshipDisplayName;
    }

    /**
     * 관계 설정 여부를 반환합니다.
     *
     * @return 관계 설정 여부
     */
    public boolean hasRelationship() {
        return hasRelationship;
    }

    /**
     * 멤버에 할당된 태그 목록을 반환합니다.
     *
     * @return 태그 목록
     */
    public List<TagInfo> getTags() {
        return tags;
    }

    /**
     * 구성원 ID를 반환합니다.
     *
     * @return 구성원 ID
     */
    public Long getMemberId() {
        return member.getId();
    }

    /**
     * 구성원 이름을 반환합니다.
     *
     * @return 구성원 이름
     */
    public String getMemberName() {
        return member.getName();
    }

    /**
     * 구성원 나이를 반환합니다.
     * 생년월일이 없는 경우 null을 반환할 수 있습니다.
     *
     * @return 구성원 나이
     */
    public Integer getMemberAge() {
        if (member.getBirthday() == null) {
            return null;
        }
        return LocalDateTime.now().getYear() - member.getBirthday().getYear();
    }

    /**
     * 구성원 생년월일을 반환합니다.
     *
     * @return 구성원 생년월일
     */
    public LocalDateTime getMemberBirthday() {
        return member.getBirthday();
    }

    /**
     * 구성원 생년월일 유형(양력/음력)을 반환합니다.
     *
     * @return 구성원 생년월일 유형 (SOLAR: 양력, LUNAR: 음력)
     */
    public BirthdayType getMemberBirthdayType() {
        return member.getBirthdayType();
    }

    /**
     * 구성원 연락처를 반환합니다.
     *
     * @return 구성원 연락처
     */
    public String getMemberPhoneNumber() {
        // FamilyMember에 phoneNumber 필드가 없으므로 일단 null 반환
        // 향후 도메인 확장 시 추가 예정
        return null;
    }

    /**
     * 구성원 프로필 이미지 URL을 반환합니다.
     *
     * @return 프로필 이미지 URL
     */
    public String getMemberProfileImageUrl() {
        return member.getProfileUrl();
    }

    /**
     * 관계 타입을 반환합니다.
     * 관계가 설정되지 않은 경우 null을 반환합니다.
     *
     * @return 관계 타입
     */
    public FamilyMemberRelationshipType getRelationshipType() {
        return member.getRelationshipType();
    }

    /**
     * 커스텀 관계명을 반환합니다.
     * CUSTOM 타입 관계인 경우에만 값이 있습니다.
     *
     * @return 커스텀 관계명
     */
    public String getCustomRelationshipName() {
        return member.getCustomRelationship();
    }

    /**
     * 관계 설정이 필요한지 여부를 반환합니다.
     * 관계가 설정되지 않은 경우 true를 반환합니다.
     *
     * @return 관계 설정 필요 여부
     */
    public boolean isRelationshipSetupRequired() {
        return !hasRelationship;
    }

    /**
     * 관계 안내 메시지를 반환합니다.
     * 관계가 설정된 경우 관계명을, 설정되지 않은 경우 안내 메시지를 반환합니다.
     *
     * @return 관계 안내 메시지
     */
    public String getRelationshipGuideMessage() {
        if (hasRelationship) {
            return relationshipDisplayName;
        } else {
            return "관계를 설정해주세요";
        }
    }

    /**
     * 연락처 표시 정보를 반환합니다.
     * 연락처가 없는 경우 적절한 안내 메시지를 반환합니다.
     *
     * @return 연락처 표시 정보
     */
    public String getPhoneNumberDisplay() {
        // FamilyMember에 phoneNumber 필드가 없으므로 일단 "연락처 없음" 반환
        // 향후 도메인 확장 시 수정 예정
        return "연락처 없음";
    }

    @Override
    public String toString() {
        return String.format("FamilyMemberWithRelationshipResponse{memberId=%d, memberName='%s', relationshipDisplayName='%s', hasRelationship=%s}",
            getMemberId(), getMemberName(), relationshipDisplayName, hasRelationship);
    }
}
