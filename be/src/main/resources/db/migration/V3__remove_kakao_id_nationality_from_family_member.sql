-- Family Member 테이블에서 kakao_id, nationality 필드 제거
-- 작성일: 2025-01-05
-- 목적: PRD-001 2단계 - FamilyMember 테이블 정리
--       name, profileUrl, birthday는 Family별로 독립 관리 (유지)
--       kakaoId, nationality는 제거
--       user_id는 nullable 유지 (수동 등록 지원)

-- 1. 체크 제약조건 제거 (user_id OR kakao_id 필수 -> 둘 다 NULL 허용)
ALTER TABLE family_member
DROP CONSTRAINT IF EXISTS chk_family_member_user_or_kakao;

-- 2. kakao_id 관련 유니크 제약조건 제거
ALTER TABLE family_member
DROP CONSTRAINT IF EXISTS uk_family_member_family_kakao;

-- 3. kakao_id 인덱스 제거 (PostgreSQL 문법)
DROP INDEX IF EXISTS idx_family_member_kakao_id;

-- 4. kakao_id 컬럼 제거
ALTER TABLE family_member
DROP COLUMN IF EXISTS kakao_id;

-- 5. nationality 컬럼 제거
ALTER TABLE family_member
DROP COLUMN IF EXISTS nationality;

-- 변경 사항 요약:
-- 1. chk_family_member_user_or_kakao 체크 제약조건 제거 (수동 등록 시 user_id, kakao_id 둘 다 NULL 허용)
-- 2. uk_family_member_family_kakao 유니크 제약조건 제거
-- 3. idx_family_member_kakao_id 인덱스 제거
-- 4. kakao_id 컬럼 제거 (User에만 존재)
-- 5. nationality 컬럼 제거

-- 유지되는 필드:
-- name, profile_url, birthday (Family별 독립 관리)
-- user_id (nullable, 수동 등록 시 NULL)
