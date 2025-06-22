-- Family Tree 데이터베이스 초기화 스크립트

-- 데이터베이스 문자셋을 UTF8MB4로 설정
ALTER DATABASE family_tree CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 사용자 권한 설정
GRANT ALL PRIVILEGES ON family_tree.* TO 'family_tree_user'@'%';
FLUSH PRIVILEGES;

-- 테이블 생성은 JPA에서 자동으로 처리됨 (ddl-auto: create)