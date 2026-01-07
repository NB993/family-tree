-- FamilyMember 태그 매핑 테이블 생성
-- 작성일: 2026-01-07
-- 목적: PRD-006 Sprint 2-BE - 멤버-태그 다대다 매핑 지원

-- family_member_tag_mapping 테이블 생성
CREATE TABLE family_member_tag_mapping (
    id BIGSERIAL PRIMARY KEY,
    tag_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_mapping_tag FOREIGN KEY (tag_id) REFERENCES family_member_tag(id) ON DELETE CASCADE,
    CONSTRAINT fk_mapping_member FOREIGN KEY (member_id) REFERENCES family_member(id) ON DELETE CASCADE,
    CONSTRAINT uk_tag_member UNIQUE (tag_id, member_id)
);

-- 인덱스 생성
CREATE INDEX idx_mapping_tag_id ON family_member_tag_mapping(tag_id);
CREATE INDEX idx_mapping_member_id ON family_member_tag_mapping(member_id);

-- 테이블 및 컬럼 코멘트
COMMENT ON TABLE family_member_tag_mapping IS 'FamilyMember와 Tag의 다대다 매핑 테이블';
COMMENT ON COLUMN family_member_tag_mapping.tag_id IS '태그 ID (FK → family_member_tag)';
COMMENT ON COLUMN family_member_tag_mapping.member_id IS '멤버 ID (FK → family_member)';
COMMENT ON COLUMN family_member_tag_mapping.created_at IS '생성 일시';

-- 변경 사항 요약:
-- 1. family_member_tag_mapping 테이블 생성
--    - id: PK (자동 생성)
--    - tag_id: FK → family_member_tag(id) (ON DELETE CASCADE)
--    - member_id: FK → family_member(id) (ON DELETE CASCADE)
--    - created_at: 생성 일시
-- 2. UNIQUE 제약조건: (tag_id, member_id) - 동일 태그-멤버 중복 매핑 방지
-- 3. 인덱스: idx_mapping_tag_id, idx_mapping_member_id - 조회 최적화
-- 4. ON DELETE CASCADE: 태그 또는 멤버 삭제 시 매핑도 자동 삭제
