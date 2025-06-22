package io.jhchoe.familytree.helper;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Testcontainers MySQL 설정을 재사용 가능한 모듈로 분리
 * - AcceptanceTestBase와 IntegrationTestBase에서 공통으로 사용
 * - MySQL 8.0 + UTF8MB4 이모지 지원
 * - 테스트 환경과 프로덕션 환경 일치성 보장
 */
public class TestcontainersModule {
    
    /**
     * 공통 MySQL 컨테이너 설정
     * - UTF8MB4 문자 인코딩으로 이모지 지원
     * - 모든 테스트에서 동일한 MySQL 환경 사용
     */
    private static final MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("family_tree_test")
            .withUsername("test_user")
            .withPassword("test_password")
            .withCommand(
                    "--character-set-server=utf8mb4",
                    "--collation-server=utf8mb4_unicode_ci"
            );

    /**
     * 컨테이너 시작 여부를 추적하는 플래그
     * - mysql.isRunning() 호출 없이 빠르게 상태 확인
     * - 한 번 시작되면 JVM 종료까지 계속 실행 상태 유지
     */
    private static volatile boolean isContainerStarted = false;

    /**
     * MySQL 컨테이너를 시작하고 Spring 데이터소스 시스템 프로퍼티를 설정합니다.
     * 
     * 최적화:
     * - 자바 플래그로 먼저 체크 (mysql.isRunning() 호출 방지)
     * - 한 번 시작되면 재호출 시 즉시 반환
     * 
     * 이 메서드는 클래스 로딩 시점에 한 번만 실행되어야 하므로,
     * 각 베이스 클래스의 static 블록에서 호출해야 합니다.
     */
    public static void startMySQLContainer() {
        // 플래그 먼저 체크 - 컨테이너 호출 없이 빠르게 반환
        if (isContainerStarted) {
            return;
        }
        
        // Double-checked locking 패턴으로 동시성 안전성 보장
        synchronized (TestcontainersModule.class) {
            if (isContainerStarted) {
                return;
            }
            
            // 실제 컨테이너 시작 (최초 1회만)
            mysql.start();
            
            // Spring Boot가 사용할 데이터소스 연결 정보 설정
            System.setProperty("spring.datasource.url", mysql.getJdbcUrl());
            System.setProperty("spring.datasource.username", mysql.getUsername());
            System.setProperty("spring.datasource.password", mysql.getPassword());
            System.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
            
            // 플래그 설정 - 이후 호출들은 즉시 반환
            isContainerStarted = true;
            
            System.out.println("🐳 MySQL Testcontainer started: " + mysql.getJdbcUrl());
        }
    }
    
}
