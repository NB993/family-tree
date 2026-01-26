-- =====================================================
-- 로컬 개발용 더미 데이터
-- 초대 받은 멤버(userId 있음) vs 수동 등록 멤버(userId null) 테스트용
-- =====================================================

-- 테스트용 사용자 (초대 받은 멤버용)
INSERT INTO users (id, email, name, profile_url, oauth2_provider, role, deleted, kakao_id, birthday, birthday_type, created_by, created_at, modified_by, modified_at)
SELECT 900, 'invited@test.com', '초대멤버', 'https://example.com/invited.jpg', 'KAKAO', 'USER', false, 'kakao_invited_900', '1990-05-20 00:00:00', 'LUNAR', 900, NOW(), 900, NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 900);

-- 가족 멤버 데이터 (family_id = 1 에 추가)
-- 1. 초대 받은 멤버 (user_id 있음) - 생일 수정 불가 테스트용
INSERT INTO family_member (id, family_id, user_id, name, profile_url, birthday, birthday_type, relationship_type, custom_relationship, status, role, created_by, created_at, modified_by, modified_at)
SELECT 900, 1, 900, '초대된 가족', 'https://example.com/invited.jpg', '1990-05-20 00:00:00', 'LUNAR', 'YOUNGER_BROTHER', NULL, 'ACTIVE', 'MEMBER', 1, NOW(), 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM family_member WHERE id = 900);

-- 2. 수동 등록 멤버 (user_id NULL) - 생일 수정 가능 테스트용 (반려동물)
INSERT INTO family_member (id, family_id, user_id, name, profile_url, birthday, birthday_type, relationship_type, custom_relationship, status, role, created_by, created_at, modified_by, modified_at)
SELECT 901, 1, NULL, '우리집 강아지', NULL, '2020-03-10 00:00:00', 'SOLAR', 'CUSTOM', '반려동물', 'ACTIVE', 'MEMBER', 1, NOW(), 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM family_member WHERE id = 901);

-- 3. 수동 등록 멤버 (user_id NULL) - 생일 수정 가능 테스트용 (아이)
INSERT INTO family_member (id, family_id, user_id, name, profile_url, birthday, birthday_type, relationship_type, custom_relationship, status, role, created_by, created_at, modified_by, modified_at)
SELECT 902, 1, NULL, '막내아이', NULL, '2018-12-25 00:00:00', 'LUNAR', 'SON', NULL, 'ACTIVE', 'MEMBER', 1, NOW(), 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM family_member WHERE id = 902);
