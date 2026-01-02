package io.jhchoe.familytree.config;

import io.jhchoe.familytree.common.auth.UserJpaEntity;
import io.jhchoe.familytree.common.auth.UserJpaRepository;
import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.domain.UserRole;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyUseCase;
import io.jhchoe.familytree.core.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로컬 개발 환경용 초기 데이터 로더
 * MySQL 데이터베이스가 비어있을 때 샘플 데이터를 자동으로 생성합니다.
 */
@Slf4j
@Component
@Profile("local") // local 프로필에서만 실행
@RequiredArgsConstructor
public class LocalDataInitializer implements ApplicationRunner {

    private final UserJpaRepository userRepository;
    private final SaveFamilyUseCase saveFamilyUseCase;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // 이미 데이터가 있으면 스킵 (Docker 볼륨으로 데이터 영속성 보장)
        long userCount = userRepository.count();
        if (userCount > 0) {
            log.info("기존 사용자 {}명이 발견되었습니다. 초기 데이터 생성을 건너뜁니다.", userCount);
            log.info("💡 Docker 볼륨으로 데이터가 보존되어 재시작해도 데이터가 유지됩니다.");
            log.info("🔄 초기 데이터를 다시 생성하려면: ./scripts/reset-db.sh 실행");
            return;
        }

        log.info("=== 로컬 개발 환경 초기 데이터 생성 시작 ===");
        log.info("📦 Docker 볼륨을 사용하므로 이 데이터는 컨테이너 재시작 후에도 보존됩니다.");

        // 1. 테스트 사용자 생성 (다양한 OAuth2 제공자 테스트)
        createUser("test@example.com", "테스트사용자", OAuth2Provider.KAKAO);
        UserJpaEntity testUser2 = createUser("kim@example.com", "김철수", OAuth2Provider.GOOGLE);
        UserJpaEntity testUser3 = createUser("park@example.com", "박영희", OAuth2Provider.KAKAO);

        // 2. 가족 생성
        Long kimFamily = createFamily(testUser2.getId(), "김씨네 가족", 
            "서울에 거주하는 김씨 가족입니다.", true);
        Long parkFamily = createFamily(testUser3.getId(), "박씨네 가족", 
            "부산에 거주하는 박씨 가족입니다.", true);

        // 3. 공지사항 생성은 나중에 웹 UI에서 테스트
        log.info("공지사항은 웹 애플리케이션에서 직접 생성해서 테스트해보세요.");

        log.info("=== 초기 데이터 생성 완료 ===");
        log.info("생성된 테스트 계정 (OAuth2 로그인용):");
        log.info("- test@example.com (테스트사용자) [Kakao]");
        log.info("- kim@example.com (김철수) [Google]");
        log.info("- park@example.com (박영희) [Kakao]");
    }

    private UserJpaEntity createUser(String email, String name, OAuth2Provider provider) {
        // User 도메인 객체 생성
        User user = User.newUser(
            email,
            name,
            "https://ui-avatars.com/api/?name=" + name + "&background=random",
            null, // kakaoId (테스트 데이터)
            AuthenticationType.OAUTH2,
            provider, // OAuth2Provider 명시적 설정
            UserRole.USER,
            false, // deleted
            null // birthday
        );
        
        UserJpaEntity userEntity = UserJpaEntity.ofOAuth2User(user);
        UserJpaEntity savedUser = userRepository.save(userEntity);
        log.info("사용자 생성: {} ({}) [Provider: {}]", savedUser.getName(), savedUser.getEmail(), provider);
        return savedUser;
    }

    private Long createFamily(Long userId, String familyName, String description, boolean isPublic) {
        SaveFamilyCommand command = new SaveFamilyCommand(
            userId,
            familyName,
            "https://ui-avatars.com/api/?name=" + familyName + "&background=f97316&color=fff",
            description,
            isPublic
        );
        
        Long familyId = saveFamilyUseCase.save(command);
        log.info("가족 생성: {} (ID: {})", familyName, familyId);
        return familyId;
    }

}
