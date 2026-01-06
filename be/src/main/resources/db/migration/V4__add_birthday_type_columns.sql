-- Users 테이블과 Family Member 테이블에 birthday_type 컬럼 추가
-- 작성일: 2025-01-06
-- 목적: PRD-005 - 생일 표시 기능을 위한 birthday_type 컬럼 추가
--       양력(SOLAR)/음력(LUNAR) 생일 구분 저장

-- 1. Users 테이블에 birthday_type 컬럼 추가
ALTER TABLE users
ADD COLUMN IF NOT EXISTS birthday_type VARCHAR(10);

-- 2. Family Member 테이블에 birthday_type 컬럼 추가
ALTER TABLE family_member
ADD COLUMN IF NOT EXISTS birthday_type VARCHAR(10);

-- 변경 사항 요약:
-- 1. users.birthday_type: 사용자의 생일 유형 (SOLAR: 양력, LUNAR: 음력)
-- 2. family_member.birthday_type: 가족 구성원의 생일 유형 (SOLAR: 양력, LUNAR: 음력)
--
-- 참고:
-- - 카카오 OAuth 로그인 시 kakao_account.birthday_type에서 가져옴
-- - 기존 데이터는 NULL로 유지 (양력으로 기본 표시)
