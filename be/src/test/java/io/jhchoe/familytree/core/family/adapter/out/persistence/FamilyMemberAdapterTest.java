package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.config.TestAuditConfig;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestAuditConfig.class)
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("[Unit Test] FamilyMemberAdapterTest")
public class FamilyMemberAdapterTest {

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    private FamilyMemberAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new FamilyMemberAdapter(familyMemberJpaRepository);
    }

    @Test
    @DisplayName("existsByFamilyIdAndUserId 메서드는 familyId가 null이면 예외를 발생시켜야 한다.")
    void given_null_family_id_then_exists_by_family_id_and_user_id_then_throw_exception() {
        Long familyId = null;
        Long userId = 1L;

        assertThatThrownBy(() -> sut.existsByFamilyIdAndUserId(familyId, userId))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("familyId must not be null");
    }

    @Test
    @DisplayName("existsByFamilyIdAndUserId 메서드는 userId가 null이면 예외를 발생시켜야 한다.")
    void given_null_user_id_then_exists_by_family_id_and_user_id_then_throw_exception() {
        Long familyId = 1L;
        Long userId = null;

        assertThatThrownBy(() -> sut.existsByFamilyIdAndUserId(familyId, userId))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("userId must not be null");
    }


    @Test
    @DisplayName("existsByFamilyIdAndUserId 메서드는 familyId, userId로 FamilyMember 조회에 성공하면 true를 응답해야 한다.")
    void given_family_id_and_user_id_then_exists_by_family_id_and_user_id_then_return_true() {
        // given
        Long familyId = 1L;
        Long userId = 1L;
        String name = "name";
        String profileUrl = "profileUrl";
        LocalDateTime birthday = LocalDateTime.now();
        String nationality = "nationality";

        FamilyMember familyMember = FamilyMember.newMember(
            familyId,
            userId,
            name,
            profileUrl,
            birthday,
            nationality
        );
        FamilyMemberJpaEntity familyMemberJpaEntity = FamilyMemberJpaEntity.from(familyMember);
        familyMemberJpaRepository.save(familyMemberJpaEntity);

        // when & then
        assertThat(sut.existsByFamilyIdAndUserId(familyId, userId))
            .isTrue();
    }

    @Test
    @DisplayName("existsByFamilyIdAndUserId 메서드는 familyId, userId로 FamilyMember 조회에 실패면 false를 응답해야 한다.")
    void given_invalid_family_id_and_user_id_then_exists_by_family_id_and_user_id_then_return_false() {
        // given
        Long invalidFamilyId = 1L;
        Long invalidUserId = 1L;

        // when & then
        assertThat(sut.existsByFamilyIdAndUserId(invalidFamilyId, invalidUserId))
            .isFalse();
    }

}
