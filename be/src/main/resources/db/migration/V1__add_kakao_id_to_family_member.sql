-- Family Member 테이블에 카카오 ID 필드 추가 및 user_id nullable 변경
-- 작성일: 2025-09-06
-- 작성자: AI Assistant
-- 목적: 카카오 OAuth 인증을 통한 비회원 초대 수락 기능 지원

-- 1. kakao_id 컬럼 추가
ALTER TABLE family_member
ADD COLUMN kakao_id VARCHAR(100) NULL COMMENT '카카오 사용자 ID' AFTER user_id;

-- 2. user_id 컬럼을 nullable로 변경
ALTER TABLE family_member
MODIFY COLUMN user_id BIGINT NULL COMMENT '사용자 ID (nullable - 비회원인 경우 null)';

-- 3. kakao_id에 인덱스 추가 (빠른 조회를 위해)
CREATE INDEX idx_family_member_kakao_id ON family_member(kakao_id);

-- 4. 비즈니스 규칙 체크: user_id 또는 kakao_id 중 최소 하나는 있어야 함
ALTER TABLE family_member
ADD CONSTRAINT chk_family_member_user_or_kakao
CHECK (user_id IS NOT NULL OR kakao_id IS NOT NULL);

-- 5. 복합 유니크 제약조건 추가: 한 가족 내에서 같은 카카오 사용자는 한 명만 가능
ALTER TABLE family_member
ADD CONSTRAINT uk_family_member_family_kakao UNIQUE (family_id, kakao_id);

-- 변경 사항 요약:
-- 1. kakao_id VARCHAR(100) 컬럼 추가
-- 2. user_id를 nullable로 변경
-- 3. kakao_id 인덱스 추가
-- 4. user_id 또는 kakao_id 중 최소 하나는 필수 체크 제약조건 추가
-- 5. (family_id, kakao_id) 복합 유니크 제약조건 추가