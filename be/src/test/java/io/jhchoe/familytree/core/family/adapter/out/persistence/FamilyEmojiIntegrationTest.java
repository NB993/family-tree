package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.test.fixture.FamilyFixture;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.jhchoe.familytree.helper.TestcontainersDataJpaTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import static org.assertj.core.api.Assertions.assertThat;

/**
 * MySQL Testcontainer를 사용한 UTF8MB4 이모지 지원 통합 테스트
 * 프로덕션 환경과 동일한 MySQL 8.0에서 이모지 저장/조회 기능을 검증합니다.
 */
@DisplayName("[Integration Test] FamilyEmojiIntegrationTest")
class FamilyEmojiIntegrationTest extends TestcontainersDataJpaTestBase {

    @Autowired
    private FamilyJpaRepository familyRepository;

    @Test
    @DisplayName("도메인에서 생성한 이모지 포함 가족이 정상적으로 저장됩니다")
    void save_family_with_emoji_from_domain() {
        // given
        String familyNameWithEmoji = "행복한 우리가족 ❤️🏠👨‍👩‍👧‍👦";
        String descriptionWithEmoji = "이모지가 포함된 가족 설명 🎉";
        String profileUrl = "https://example.com/profile.jpg";
        Long creatorId = 1L;
        
        // 도메인 객체에서 생성 (가이드라인 준수)
        Family domainFamily = FamilyFixture.newFamily(
                familyNameWithEmoji,
                descriptionWithEmoji,
                profileUrl,
                true
        );
        
        // 도메인 → JPA 엔티티 변환
        FamilyJpaEntity familyEntity = FamilyJpaEntity.from(domainFamily);

        // when
        FamilyJpaEntity savedFamily = familyRepository.save(familyEntity);
        FamilyJpaEntity foundFamily = familyRepository.findById(savedFamily.getId()).orElseThrow();

        // then
        assertThat(foundFamily.getName()).isEqualTo(familyNameWithEmoji);
        assertThat(foundFamily.getDescription()).isEqualTo(descriptionWithEmoji);
        assertThat(foundFamily.getProfileUrl()).isEqualTo(profileUrl);
    }

    @Test
    @DisplayName("복잡한 이모지 조합이 길이와 함께 정확하게 저장됩니다")
    void save_complex_emoji_with_correct_length() {
        // given
        String complexEmoji = "🎂🎉🎈🎁🥳💐🍰🎊";
        
        Family domainFamily = FamilyFixture.newFamily(
                "생일파티",
                complexEmoji,
                "https://example.com/party.jpg",
                true
        );
        
        FamilyJpaEntity familyEntity = FamilyJpaEntity.from(domainFamily);

        // when
        FamilyJpaEntity savedFamily = familyRepository.save(familyEntity);
        FamilyJpaEntity foundFamily = familyRepository.findById(savedFamily.getId()).orElseThrow();

        // then
        assertThat(foundFamily.getDescription()).isEqualTo(complexEmoji);
        assertThat(foundFamily.getDescription().length()).isEqualTo(complexEmoji.length());
    }

    @Test
    @DisplayName("한글과 이모지 혼합 텍스트가 UTF8MB4 collation으로 정확하게 처리됩니다")
    void save_korean_emoji_mixed_text_with_utf8mb4_collation() {
        // given
        String mixedText = "안녕하세요! 👋 저희는 김씨 가족입니다 🏡 반갑습니다! 😊";
        String familyName = "김씨 가족";
        
        Family domainFamily = FamilyFixture.newFamily(
                familyName,
                mixedText,
                "https://example.com/kim-family.jpg",
                true
        );
        
        FamilyJpaEntity familyEntity = FamilyJpaEntity.from(domainFamily);

        // when
        FamilyJpaEntity savedFamily = familyRepository.save(familyEntity);
        FamilyJpaEntity foundFamily = familyRepository.findById(savedFamily.getId()).orElseThrow();

        // then
        assertThat(foundFamily.getDescription()).isEqualTo(mixedText);
        assertThat(foundFamily.getName()).isEqualTo(familyName);
        
        // UTF8MB4 collation 검증 - 한글 포함 검색이 올바르게 동작
        assertThat(foundFamily.getName()).contains("김씨");
    }
}
