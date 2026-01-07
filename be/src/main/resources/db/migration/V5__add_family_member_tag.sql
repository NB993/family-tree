-- FamilyMember 태그 기능 테이블 생성
-- 작성일: 2026-01-07
-- 목적: PRD-006 Sprint 1-BE - 태그 CRUD 기능 지원

-- family_member_tag 테이블 생성
CREATE TABLE family_member_tag (
    id BIGSERIAL PRIMARY KEY,
    family_id BIGINT NOT NULL,
    name VARCHAR(10) NOT NULL,
    color VARCHAR(7) NOT NULL DEFAULT '#E3E2E0',
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_by BIGINT,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tag_family FOREIGN KEY (family_id) REFERENCES family(id),
    CONSTRAINT uk_family_tag_name UNIQUE (family_id, name)
);

-- 인덱스 생성
CREATE INDEX idx_tag_family_id ON family_member_tag(family_id);

-- 테이블 및 컬럼 코멘트
COMMENT ON TABLE family_member_tag IS 'FamilyMember 분류를 위한 태그';
COMMENT ON COLUMN family_member_tag.family_id IS '소속 Family ID';
COMMENT ON COLUMN family_member_tag.name IS '태그 이름 (1~10자)';
COMMENT ON COLUMN family_member_tag.color IS '태그 색상 HEX 코드 (예: #E3E2E0)';
COMMENT ON COLUMN family_member_tag.created_by IS '생성자 ID';
COMMENT ON COLUMN family_member_tag.created_at IS '생성 일시';
COMMENT ON COLUMN family_member_tag.modified_by IS '수정자 ID';
COMMENT ON COLUMN family_member_tag.modified_at IS '수정 일시';

-- 변경 사항 요약:
-- 1. family_member_tag 테이블 생성
--    - id: PK (자동 생성)
--    - family_id: FK → family(id)
--    - name: 태그 이름 (최대 10자)
--    - color: 태그 색상 HEX 코드 (기본값: #E3E2E0)
--    - created_by, created_at, modified_by, modified_at: 감사 컬럼
-- 2. UNIQUE 제약조건: (family_id, name) - 같은 Family 내 태그명 중복 방지
-- 3. 인덱스: idx_tag_family_id - Family별 태그 조회 최적화
