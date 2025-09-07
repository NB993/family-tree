-- Family Invite 테이블에 초대 링크 사용 횟수 관련 필드 추가
-- 작성일: 2025-09-06
-- 작성자: AI Assistant
-- 목적: 초대 링크 사용 횟수 제한 기능 지원

-- 1. max_uses 컬럼 추가 (NULL이면 무제한)
ALTER TABLE family_invite
ADD COLUMN max_uses INT NULL COMMENT '최대 사용 가능 횟수 (NULL이면 무제한)' AFTER expires_at;

-- 2. used_count 컬럼 추가 (기본값 0)
ALTER TABLE family_invite
ADD COLUMN used_count INT NOT NULL DEFAULT 0 COMMENT '사용된 횟수' AFTER max_uses;

-- 3. 기존 데이터에 대해 used_count를 0으로 설정 (이미 DEFAULT 0으로 처리됨)

-- 변경 사항 요약:
-- 1. max_uses INT 컬럼 추가 (nullable)
-- 2. used_count INT 컬럼 추가 (NOT NULL, DEFAULT 0)