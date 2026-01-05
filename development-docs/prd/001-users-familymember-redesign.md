# PRD-001: Users & FamilyMember 테이블 재설계

## 문서 정보
- **작성일**: 2025-01-02
- **상태**: 🔄 진행 중 (1/2 단계 완료)
- **우선순위**: 높음

### 진행 상황
| 단계 | 내용 | 상태 |
|------|------|------|
| 1단계 | User 도메인 확장 (birthday, email nullable, AuthenticationType NONE) | ✅ 완료 (`30035a4`) |
| 2단계 | FamilyMember 테이블 정리 (컬럼 제거, user_id NOT NULL) | ⏳ 미진행 |

### 관련 작업
- PRD-002에서 AuthenticationType 필드 제거됨 (원래 NONE 추가 계획이었으나 전체 필드 제거로 변경)

---

## 1. 배경 및 문제점

### 1.1 현재 상황
- `users` 테이블: 로그인한 회원만 저장
- `family_member` 테이블: 모든 가족 구성원 저장 (비회원 포함)
- 두 테이블에 중복 컬럼 존재 (`kakao_id`, `name`, `profile_url`)

### 1.2 문제점
| 문제 | 설명 |
|------|------|
| **kakao_id 중복** | `users`와 `family_member` 양쪽에 존재 → 동기화 이슈 |
| **birthday 위치** | 사람 고유 속성인데 `family_member`에 있음 → 같은 사람이 여러 Family에 속하면 중복 |
| **정체성 혼란** | 카카오 로그인했는데 "회원"이 아닌 상태 존재 |
| **수동 등록 불가** | DB 제약조건으로 user_id/kakao_id 중 하나는 필수 → 애완동물 등록 불가 |

### 1.3 논의 결과
- 카카오 동의 시점에 이미 서비스 연동이 되므로, 초대링크 응답자도 회원가입 처리
- 수동 등록(애완동물, 아이 등)도 `users` 테이블에 저장
- `email` 대신 `kakaoId`로만 사용자 식별 (현재 카카오만 사용 중)

---

## 2. 목표

1. **단일 진실 원천**: 사람 고유 속성(`birthday`)은 `users`에만 저장
2. **데이터 정합성**: 중복 컬럼 제거로 동기화 문제 해결
3. **수동 등록 지원**: 애완동물, 아이 등 로그인 불가능한 구성원 등록 가능
4. **단순한 관계**: `family_member.user_id`가 항상 존재

---

## 3. 변경 범위

### 3.1 users 테이블

| 컬럼 | 현재 | 변경 |
|------|------|------|
| email | NOT NULL | **nullable로 변경** ✅ |
| birthday | 없음 | **추가** ✅ |
| authentication_type | FORM_LOGIN, OAUTH2, JWT | ~~NONE 추가~~ → **컬럼 제거** (PRD-002) ✅ |

> **Note**: 수동 등록 사용자는 `oauth2_provider IS NULL`로 구분 (`User.isLoginable()` 메서드)

### 3.2 family_member 테이블

| 컬럼 | 현재 | 변경 |
|------|------|------|
| user_id | nullable | **NOT NULL** |
| name | NOT NULL | **제거** (users.name 사용) |
| profile_url | nullable | **제거** (users.profile_url 사용) |
| kakao_id | nullable | **제거** (users에만 존재) |
| birthday | nullable | **제거** (users로 이동) |
| nationality | nullable | **제거** |

---

## 4. 새로운 데이터 모델

### 4.1 users (변경 후)
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    email VARCHAR(255),              -- nullable ✅
    name VARCHAR(255),
    profile_url VARCHAR(255),
    kakao_id VARCHAR(255) UNIQUE,    -- 유니크 인덱스
    birthday TIMESTAMP(6),           -- 신규 ✅
    -- authentication_type 제거됨 (PRD-002) ✅
    oauth2_provider VARCHAR(255),    -- NULL이면 수동 등록 사용자
    role VARCHAR(255) NOT NULL,
    deleted BOOLEAN,
    -- audit fields
);
```

### 4.2 family_member (변경 후)
```sql
CREATE TABLE family_member (
    id BIGINT PRIMARY KEY,
    family_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,         -- NOT NULL로 변경
    relationship VARCHAR(255),
    role VARCHAR(255),               -- OWNER, ADMIN, MEMBER
    status VARCHAR(255),             -- ACTIVE, SUSPENDED, BANNED
    -- audit fields

    UNIQUE (family_id, user_id)      -- 복합 유니크
);
```

---

## 5. 주요 시나리오

### 5.1 초대링크 응답 (변경)
```
현재:
1. 카카오 로그인
2. email로 users 조회 → 없으면 family_member에 kakaoId만 저장

변경 후:
1. 카카오 로그인
2. kakaoId로 users 조회
3. 없으면 users에 새 레코드 생성 (회원가입)
4. family_member 생성 시 user_id 필수
```

### 5.2 수동 등록 (신규 - 별도 PRD)
```
1. 관리자가 이름, 프로필, 생일 입력
2. users에 authentication_type=NONE으로 저장
3. family_member 생성 시 user_id 연결
```

### 5.3 가입 신청 승인 (변경)
```
현재:
- 더미 데이터로 family_member 생성 (TODO 상태)

변경 후:
- users에서 신청자 정보 조회
- family_member 생성 시 user_id 연결
```

---

## 6. 마이그레이션 전략

### 6.1 데이터 마이그레이션
1. kakaoId만 있는 family_member → users 레코드 생성
2. family_member.user_id 업데이트
3. birthday를 users로 복사
4. 제약조건 변경
5. 불필요한 컬럼 제거

### 6.2 롤백 계획
- 마이그레이션 전 백업 필수
- 롤백 스크립트 준비

---

## 7. 영향 범위

### 7.1 백엔드
| 계층 | 영향받는 파일 |
|------|--------------|
| 도메인 | User.java, FamilyMember.java |
| JPA Entity | UserJpaEntity.java, FamilyMemberJpaEntity.java |
| 서비스 | SaveInviteResponseWithKakaoService, ProcessFamilyJoinRequestService, SaveFamilyService |
| Port | FindUserPort, SaveUserPort |
| Repository | UserJpaRepository |

### 7.2 프론트엔드
| 파일 | 변경 |
|------|------|
| types/user.ts | birthday 추가, email optional |
| types/family.ts | FamilyMember에서 birthday, name 제거 |
| API 서비스 | 응답 구조 변경 대응 |

---

## 8. 리스크

| 리스크 | 대응 |
|--------|------|
| 데이터 손실 | 마이그레이션 전 백업, 롤백 스크립트 준비 |
| API 응답 변경 | FE에서 name, birthday를 User에서 조인 |
| NONE 타입 로그인 시도 | 인증 로직에서 차단 처리 |

---

## 9. 제외 범위 (별도 PRD)

- 수동 등록 기능 구현 (UI, API)
- 카카오 동의 항목에 생일 추가

---

## 10. 구현 순서

### 1단계: User 도메인 확장 ✅ 완료
1. ~~AuthenticationType에 NONE 추가~~ → PRD-002에서 AuthenticationType 전체 제거로 변경
2. User 도메인 확장 (birthday, email nullable) ✅
3. 수동 사용자 구분: `User.isLoginable()` 메서드 (oAuth2Provider != null) ✅

### 2단계: FamilyMember 테이블 정리 ⏳ 미진행
4. FamilyMember 도메인 정리 (name, profile_url, kakao_id, birthday, nationality 제거)
5. FamilyMember.user_id NOT NULL 변경
6. 서비스 로직 변경 (SaveInviteResponseWithKakaoService 등)
7. Repository/Port 변경
8. DB 마이그레이션
9. 테스트 변경
10. 프론트엔드 변경
