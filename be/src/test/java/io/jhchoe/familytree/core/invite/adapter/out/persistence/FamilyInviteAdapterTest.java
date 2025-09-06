package io.jhchoe.familytree.core.invite.adapter.out.persistence;

import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import io.jhchoe.familytree.core.invite.domain.FamilyInviteStatus;
import io.jhchoe.familytree.helper.TestcontainersDataJpaTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Adapter Test] FamilyInviteAdapter")
class FamilyInviteAdapterTest extends TestcontainersDataJpaTestBase {

    @Autowired
    private FamilyInviteJpaRepository familyInviteJpaRepository;

    private FamilyInviteAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new FamilyInviteAdapter(familyInviteJpaRepository);
    }

    @Test
    @DisplayName("새로운 초대를 저장하고 저장된 초대를 반환합니다")
    void save_returns_saved_invite() {
        // given
        FamilyInvite invite = FamilyInvite.newInvite(1L);

        // when
        FamilyInvite savedInvite = sut.save(invite);

        // then
        assertThat(savedInvite.getId()).isNotNull();
        assertThat(savedInvite.getRequesterId()).isEqualTo(1L);
        assertThat(savedInvite.getInviteCode()).isNotNull();
        assertThat(savedInvite.getStatus()).isEqualTo(FamilyInviteStatus.ACTIVE);
    }

    @Test
    @DisplayName("ID로 초대를 조회할 수 있습니다")
    void findById_returns_invite_when_exists() {
        // given
        FamilyInvite invite = FamilyInvite.newInvite(1L);
        FamilyInviteJpaEntity savedEntity = familyInviteJpaRepository.save(FamilyInviteJpaEntity.from(invite));

        // when
        Optional<FamilyInvite> foundInvite = sut.findById(savedEntity.getId());

        // then
        assertThat(foundInvite).isPresent();
        assertThat(foundInvite.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(foundInvite.get().getRequesterId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환합니다")
    void findById_returns_empty_when_not_exists() {
        // when
        Optional<FamilyInvite> foundInvite = sut.findById(999L);

        // then
        assertThat(foundInvite).isEmpty();
    }

    @Test
    @DisplayName("초대 코드로 초대를 조회할 수 있습니다")
    void findByInviteCode_returns_invite_when_exists() {
        // given
        FamilyInvite invite = FamilyInvite.newInvite(1L);
        FamilyInviteJpaEntity savedEntity = familyInviteJpaRepository.save(FamilyInviteJpaEntity.from(invite));
        String inviteCode = savedEntity.getInviteCode();

        // when
        Optional<FamilyInvite> foundInvite = sut.findByInviteCode(inviteCode);

        // then
        assertThat(foundInvite).isPresent();
        assertThat(foundInvite.get().getInviteCode()).isEqualTo(inviteCode);
    }

    @Test
    @DisplayName("요청자 ID로 초대 목록을 조회할 수 있습니다")
    void findByRequesterId_returns_invite_list() {
        // given
        Long requesterId = 1L;
        FamilyInvite invite1 = FamilyInvite.newInvite(requesterId);
        FamilyInvite invite2 = FamilyInvite.newInvite(requesterId);
        FamilyInvite invite3 = FamilyInvite.newInvite(2L); // 다른 요청자

        familyInviteJpaRepository.save(FamilyInviteJpaEntity.from(invite1));
        familyInviteJpaRepository.save(FamilyInviteJpaEntity.from(invite2));
        familyInviteJpaRepository.save(FamilyInviteJpaEntity.from(invite3));

        // when
        List<FamilyInvite> invites = sut.findByRequesterId(requesterId);

        // then
        assertThat(invites).hasSize(2);
        assertThat(invites).allMatch(invite -> invite.getRequesterId().equals(requesterId));
    }

    @Test
    @DisplayName("null 파라미터로 save 호출 시 NPE가 발생합니다")
    void save_throws_npe_when_invite_is_null() {
        // when & then
        assertThatThrownBy(() -> sut.save(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyInvite must not be null");
    }

    @Test
    @DisplayName("null 파라미터로 findById 호출 시 NPE가 발생합니다")
    void findById_throws_npe_when_id_is_null() {
        // when & then
        assertThatThrownBy(() -> sut.findById(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("id must not be null");
    }

    @Test
    @DisplayName("null 파라미터로 findByInviteCode 호출 시 NPE가 발생합니다")
    void findByInviteCode_throws_npe_when_code_is_null() {
        // when & then
        assertThatThrownBy(() -> sut.findByInviteCode(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("inviteCode must not be null");
    }

    @Test
    @DisplayName("null 파라미터로 findByRequesterId 호출 시 NPE가 발생합니다")
    void findByRequesterId_throws_npe_when_id_is_null() {
        // when & then
        assertThatThrownBy(() -> sut.findByRequesterId(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("requesterId must not be null");
    }
}