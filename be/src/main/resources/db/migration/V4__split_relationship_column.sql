-- Family Member 테이블의 relationship 필드를 두 개로 분리
-- 작성일: 2025-01-06
-- 목적: PRD-003 3단계 - relationship 필드 분리 리팩토링
--       relationship(String) -> relationship_type(enum) + custom_relationship(String)

-- 1. 새 컬럼 추가
ALTER TABLE family_member
ADD COLUMN relationship_type VARCHAR(50);

ALTER TABLE family_member
ADD COLUMN custom_relationship VARCHAR(50);

-- 2. 기존 데이터 마이그레이션
-- enum name인 경우 relationship_type에 저장
-- 그 외(사용자 정의)인 경우 relationship_type = 'CUSTOM', custom_relationship에 저장
UPDATE family_member
SET relationship_type = CASE
    WHEN relationship IN ('FATHER', 'MOTHER', 'SON', 'DAUGHTER',
                          'GRANDFATHER', 'GRANDMOTHER', 'GRANDSON', 'GRANDDAUGHTER',
                          'ELDER_BROTHER', 'ELDER_SISTER', 'YOUNGER_BROTHER', 'YOUNGER_SISTER',
                          'HUSBAND', 'WIFE',
                          'UNCLE', 'AUNT', 'NEPHEW', 'NIECE', 'COUSIN',
                          'CUSTOM')
        THEN relationship
    WHEN relationship IS NOT NULL AND relationship != ''
        THEN 'CUSTOM'
    ELSE NULL
END,
custom_relationship = CASE
    WHEN relationship NOT IN ('FATHER', 'MOTHER', 'SON', 'DAUGHTER',
                              'GRANDFATHER', 'GRANDMOTHER', 'GRANDSON', 'GRANDDAUGHTER',
                              'ELDER_BROTHER', 'ELDER_SISTER', 'YOUNGER_BROTHER', 'YOUNGER_SISTER',
                              'HUSBAND', 'WIFE',
                              'UNCLE', 'AUNT', 'NEPHEW', 'NIECE', 'COUSIN',
                              'CUSTOM')
        AND relationship IS NOT NULL AND relationship != ''
        THEN relationship
    ELSE NULL
END
WHERE relationship IS NOT NULL;

-- 3. 기존 relationship 컬럼 삭제
ALTER TABLE family_member
DROP COLUMN relationship;

-- 변경 사항 요약:
-- 1. relationship_type 컬럼 추가 (VARCHAR(50), nullable) - enum name 저장
-- 2. custom_relationship 컬럼 추가 (VARCHAR(50), nullable) - CUSTOM일 때 사용자 입력값
-- 3. 기존 데이터 마이그레이션:
--    - enum name이면 relationship_type에 그대로 저장
--    - 사용자 정의 값이면 relationship_type='CUSTOM', custom_relationship에 값 저장
-- 4. 기존 relationship 컬럼 삭제
