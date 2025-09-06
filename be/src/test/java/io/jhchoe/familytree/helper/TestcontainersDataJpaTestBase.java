package io.jhchoe.familytree.helper;

import io.jhchoe.familytree.config.TestAuditConfig;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * DataJpaTest + Testcontainers PostgreSQL 조합을 위한 베이스 클래스
 * 
 * 특징:
 * - @DataJpaTest의 빠른 슬라이스 테스트
 * - PostgreSQL Testcontainer로 프로덕션 환경과 동일한 데이터베이스
 * - JPA Repository, Entity 매핑, 쿼리 테스트에 최적화
 * - 트랜잭션 롤백으로 테스트 격리
 * 
 * 사용 예시:
 * @Import(TestAuditConfig.class)  // 필요한 경우에만
 * @DisplayName("[Repository Test] FamilyRepositoryTest")
 * class FamilyRepositoryTest extends TestcontainersDataJpaTestBase {
 *     @Autowired
 *     private FamilyJpaRepository familyRepository;
 *     
 *     @Test
 *     void 이모지_포함_가족_저장_테스트() {
 *         // 테스트 로직
 *     }
 * }
 */
@Import(TestAuditConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // H2 대신 실제 DB 사용
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class TestcontainersDataJpaTestBase {
    
    // TestcontainersModule을 통해 PostgreSQL 컨테이너 설정 재사용
    static {
        TestcontainersModule.startPostgreSQLContainer();
    }
    
    // Repository 테스트에 유용한 공통 메서드들을 여기에 추가할 수 있음
}
