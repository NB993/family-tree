package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import java.util.Objects;
import lombok.Getter;

/**
 * Family 구성원의 역할을 변경하는 유스케이스 인터페이스입니다.
 */
public interface UpdateFamilyMemberRoleUseCase {
    /**
     * 구성원의 역할을 변경합니다.
     *
     * @param command 역할 변경 명령 객체
     * @return 업데이트된 구성원의 ID
     */
    Long updateRole(UpdateFamilyMemberRoleCommand command);
    
    /**
     * 구성원 역할 변경을 위한 명령 클래스입니다.
     */
    @Getter
    class UpdateFamilyMemberRoleCommand {
        private final Long familyId;
        private final Long memberId;
        private final Long currentUserId;
        private final FamilyMemberRole newRole;
        
        /**
         * 구성원 역할 변경 명령 객체를 생성합니다.
         *
         * @param familyId     Family ID
         * @param memberId     변경할 구성원 ID
         * @param currentUserId 현재 요청하는 사용자 ID
         * @param newRole      새 역할
         */
        public UpdateFamilyMemberRoleCommand(
            Long familyId,
            Long memberId,
            Long currentUserId,
            FamilyMemberRole newRole
        ) {
            this.familyId = Objects.requireNonNull(familyId, "familyId must not be null");
            this.memberId = Objects.requireNonNull(memberId, "memberId must not be null");
            this.currentUserId = Objects.requireNonNull(currentUserId, "currentUserId must not be null");
            this.newRole = Objects.requireNonNull(newRole, "newRole must not be null");
            
            validate();
        }
        
        /**
         * 명령 객체의 유효성을 검증합니다.
         */
        private void validate() {
            if (familyId <= 0) {
                throw new IllegalArgumentException("familyId must be positive");
            }
            
            if (memberId <= 0) {
                throw new IllegalArgumentException("memberId must be positive");
            }
            
            if (currentUserId <= 0) {
                throw new IllegalArgumentException("currentUserId must be positive");
            }
        }
    }
}