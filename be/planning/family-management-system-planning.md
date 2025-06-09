# Family 생성/수정/삭제 기능 기획서 (기존 구현 반영 수정판)

## 문서 정보
- **프로젝트명**: Family Tree - Family 관리 시스템 (기존 구현 확장)
- **작성일**: 2025-06-09
- **버전**: v2.0 (기존 구현 분석 반영)
- **작성자**: 기획자 AI

---

## 🔍 기존 구현 분석 결과

### ✅ 이미 구현된 기능들
- **SaveFamilyController**: Family 생성 API (`POST /api/family`)
- **Family 엔티티**: 기본 정보 관리 (`name`, `description`, `profileUrl`)
- **소프트 딜리트**: `deleted` 필드로 논리적 삭제 지원
- **권한 관리**: FamilyMember와 FamilyMemberRole로 권한 체계 구축
- **기본 CRUD**: 생성, 조회, 수정 기능의 기본 틀 완성

### ❌ 미구현/수정 필요사항
1. **엔드포인트 변경**: `/api/family` → `/api/families` (REST 표준)
2. **가족명 검증 강화**: 20자 제한, 특수문자 제한, 전체 시스템 중복 체크
3. **공개/비공개 설정**: `isPublic` 필드 추가 및 관련 로직
4. **가족명 중복 체크 API**: 실시간 중복 확인 기능
5. **공개 Family 검색 API**: 키워드 기반 검색 및 무한 스크롤
6. **사용자별 소속 Family 목록 API**: 내 소속 Family 조회
7. **권한 기반 접근 제어**: OWNER만 수정/삭제 가능한 로직

---

## 1. 목표 및 배경 (Why)

### 1.1 프로젝트 목적
```
기존 Family 생성 기능을 확장하여 완전한 Family 관리 시스템을 구축

해결하려는 문제: 
- 기존 구현에는 기본 CRUD만 있고 비즈니스 로직이 부족
- 가족명 중복 체크 및 검증 로직 미구현
- 공개/비공개 Family 개념 및 검색 기능 부재
- REST API 표준 준수 필요 (/api/families)

기대 효과:
- 기존 코드 최대한 활용하여 개발 시간 단축
- 체계적인 Family 관리 및 검색 시스템 구현
- REST API 표준을 준수한 일관된 엔드포인트 제공
```

---

## 2. 기존 구현 재사용 전략

### 2.1 재사용 가능한 기존 컴포넌트

#### ✅ 그대로 활용할 수 있는 것들
```java
// Port 인터페이스들 (기능 확장만 필요)
- SaveFamilyPort: Family 저장 로직 재사용
- FindFamilyPort: Family 조회 로직 재사용  
- ModifyFamilyPort: Family 수정 로직 재사용
- SaveFamilyMemberPort: 구성원 권한 관리 재사용
- FindFamilyMemberPort: 권한 확인 로직 재사용

// UseCase들 (기본 틀 재사용)
- SaveFamilyUseCase: Family 생성 로직 확장
- FindFamilyUseCase: Family 조회 로직 확장

// 도메인 모델 (필드 추가만 필요)
- Family: isPublic 필드 추가
- FamilyMember: 기존 권한 구조 활용
- FamilyMemberRole: OWNER, ADMIN, MEMBER 권한 활용
```

#### 🔄 수정/확장이 필요한 것들
```java
// Controller 계층
- SaveFamilyController: /api/family → /api/families로 변경
- 새로운 Controller들 추가 (검색, 중복체크 등)

// 검증 로직
- SaveFamilyRequest: @Size(max=20) 추가, 특수문자 검증 강화
- 새로운 Validation 어노테이션 적용

// 비즈니스 로직  
- Family 생성 시 OWNER 권한 자동 부여
- 가족명 중복 체크 로직
- 공개/비공개 설정에 따른 접근 제어
```

### 2.2 기존 코드 활용 우선순위
```
1순위: 기존 Port/Adapter 패턴 그대로 활용
2순위: 기존 Domain 모델 확장 (isPublic 필드 추가)
3순위: 기존 UseCase 로직 확장 및 새 UseCase 추가
4순위: 기존 테스트 패턴 재사용
5순위: 기존 예외 처리 및 응답 형식 준수
```

---

## 3. 미구현 기능 상세 명세

### 3.1 엔드포인트 변경
```
변경 사항:
- 기존: POST /api/family
- 신규: POST /api/families

이유: REST API 표준 준수 (복수형 리소스명)

영향도:
- Controller 경로 변경
- 프론트엔드 API 호출 부분 수정 필요
- API 문서 업데이트
```

### 3.2 가족명 검증 강화

#### 3.2.1 현재 검증 로직
```java
// 기존: SaveFamilyRequest
public record SaveFamilyRequest(
    @NotBlank String name,  // null, 빈값만 체크
    String description,
    String profileUrl
) {}
```

#### 3.2.2 강화된 검증 로직
```java
// 신규: 강화된 SaveFamilyRequest  
public record SaveFamilyRequest(
    @NotBlank(message = "가족명을 입력해주세요")
    @Size(max = 20, message = "가족명은 20자 이하로 입력해주세요")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\p{IsEmoji}]+$", 
             message = "가족명에는 한글, 영문, 숫자, 이모지만 사용 가능합니다")
    String name,
    
    @Pattern(regexp = "^(PUBLIC|PRIVATE)$", message = "공개 여부는 PUBLIC 또는 PRIVATE만 가능합니다")
    String visibility,  // 신규 추가
    
    String description,
    String profileUrl
) {}
```

### 3.3 공개/비공개 설정 추가

#### 3.3.1 Family 도메인 확장
```java
// Family.java에 추가할 필드
private Boolean isPublic;  // true: 공개, false: 비공개

// 생성자 메서드 확장
public static Family newFamily(
    String name,
    String description, 
    String profileUrl,
    Boolean isPublic  // 신규 추가
) {
    return new Family(null, name, description, profileUrl, isPublic, null, null, null, null);
}
```

#### 3.3.2 FamilyJpaEntity 확장
```java
// FamilyJpaEntity.java에 추가할 필드
@Column(name = "is_public", nullable = false)
private Boolean isPublic = false;  // 기본값: 비공개
```

### 3.4 신규 API 구현 필요사항

#### 3.4.1 가족명 중복 체크 API
```
GET /api/families/check-name?name={familyName}

응답:
{
  "available": true,
  "message": "사용 가능한 가족명입니다"
}

또는

{
  "available": false, 
  "message": "이미 사용 중인 가족명입니다"
}
```

#### 3.4.2 내 소속 Family 목록 조회 API
```
GET /api/families/my

응답:
{
  "families": [
    {
      "id": 1,
      "name": "우리가족",
      "description": "행복한 우리 가족",
      "profileUrl": "https://example.com/profile.jpg",
      "isPublic": false,
      "role": "OWNER",
      "memberCount": 5,
      "recentActivityAt": "2025-06-09T10:30:00"
    }
  ],
  "totalCount": 1
}
```

#### 3.4.3 공개 Family 검색 API (키워드 검색 + 무한 스크롤)
```
GET /api/families/public?keyword={keyword}&cursor={cursor}&size={size}

기존 검색 로직 확장:
- 기존 findByNameContaining(keyword) 활용
- isPublic=true 필터링 추가  
- 커서 기반 페이징 적용

응답:
{
  "families": [
    {
      "id": 2,
      "name": "행복한 대가족",
      "description": "모두가 환영하는 가족",
      "profileUrl": "https://example.com/profile2.jpg",
      "memberCount": 12,
      "canJoin": true
    }
  ],
  "pagination": {
    "nextCursor": "eyJpZCI6MiwibWVtYmVyQ291bnQiOjEyfQ==",
    "hasNext": true,
    "size": 20
  }
}

기존 검색과의 차이점:
- 기존: GET /api/families?name={name} (모든 Family, 권한 체크)
- 신규: GET /api/families/public?keyword={keyword} (공개 Family만, 무한 스크롤)
```

#### 3.4.4 Family 수정 API (권한 강화)
```
PUT /api/families/{familyId}

권한 제한: OWNER만 수정 가능
수정 가능 항목: name, description, profileUrl, isPublic

요청:
{
  "name": "새로운 가족명",
  "description": "수정된 설명", 
  "profileUrl": "https://example.com/new-profile.jpg",
  "visibility": "PUBLIC"
}
```

#### 3.4.5 Family 삭제 API (권한 강화)
```
DELETE /api/families/{familyId}

권한 제한: OWNER만 삭제 가능
처리 방식: 소프트 딜리트 (deleted = true)
삭제 시 처리: 모든 구성원 자동 탈퇴, 관련 데이터 30일 보관 후 완전 삭제
```

---

## 4. 구현 계획

### 4.1 Phase 1: 기존 기능 확장 (1주차)
```
✅ 우선순위 1 (즉시 구현):
- SaveFamilyController 경로 변경: /api/family → /api/families
- SaveFamilyRequest 검증 로직 강화 (20자 제한, 패턴 검증)
- Family 도메인 및 JpaEntity에 isPublic 필드 추가
- Family 생성 시 생성자에게 OWNER 권한 자동 부여 로직

예상 작업 시간: 1-2일
완료 조건: 기존 테스트 통과 + 새로운 검증 로직 테스트 통과
```

### 4.2 Phase 2: 중복 체크 및 검색 기능 (1주차)
```
✅ 우선순위 2:
- 가족명 중복 체크 API 구현
- FindFamilyPort에 findByName 메서드 추가
- 실시간 중복 체크를 위한 새로운 UseCase 구현

예상 작업 시간: 1일
완료 조건: 중복 체크 API 테스트 통과
```

### 4.3 Phase 3: 목록 조회 기능 (2주차)
```
✅ 우선순위 3 (기존 구현 활용):
- 내 소속 Family 목록 조회 API (신규)
- 공개 Family 검색 API (기존 키워드 검색 + 공개 필터링 + 무한 스크롤)
- 기존 findByNameContaining 로직 확장 및 재사용
- 권한별 접근 제어 로직 구현

Phase 3-1: 내 소속 Family 목록 (1일)
- FindMyFamiliesUseCase 구현 (기존 FamilyMemberPort 활용)
- GET /api/families/my 엔드포인트 추가

Phase 3-2: 공개 Family 검색 + 무한 스크롤 (2일)
- 기존 findByNameContaining 로직 확장
- 공개 여부 필터링 (isPublic=true) 추가
- 커서 기반 페이징 유틸리티 구현
- GET /api/families/public?keyword={keyword}&cursor={cursor}&size={size}

예상 작업 시간: 3일 (기존 로직 재사용으로 단축)
완료 조건: 목록/검색 API 테스트 통과 + 권한 테스트 통과
```

### 4.4 Phase 4: 수정/삭제 권한 강화 (2주차)
```
✅ 우선순위 4:
- 기존 수정/삭제 API에 OWNER 권한 체크 로직 추가
- 삭제 시 구성원 처리 로직 구현
- 권한 기반 접근 제어 강화

예상 작업 시간: 1-2일
완료 조건: 권한 기반 수정/삭제 테스트 통과
```

---

## 5. 데이터베이스 스키마 변경

### 5.1 Family 테이블 수정
```sql
-- 기존 Family 테이블에 컬럼 추가
ALTER TABLE family 
ADD COLUMN is_public BOOLEAN NOT NULL DEFAULT FALSE;

-- 가족명 유니크 제약 추가  
ALTER TABLE family 
ADD CONSTRAINT uk_family_name UNIQUE (name);

-- 인덱스 추가 (검색 성능 향상)
CREATE INDEX idx_family_public_name ON family (is_public, name);
CREATE INDEX idx_family_created_at ON family (created_at);
```

### 5.2 마이그레이션 스크립트
```sql
-- V2__add_family_public_setting.sql
ALTER TABLE family 
ADD COLUMN is_public BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE family 
ADD CONSTRAINT uk_family_name UNIQUE (name);

CREATE INDEX idx_family_public_name ON family (is_public, name);
CREATE INDEX idx_family_created_at ON family (created_at);

-- 기존 데이터 처리: 모든 기존 Family를 비공개로 설정
UPDATE family SET is_public = FALSE WHERE is_public IS NULL;
```

---

## 6. 테스트 전략

### 6.1 기존 테스트 영향도 분석
```
✅ 영향 없는 테스트:
- SaveFamilyControllerTest: 요청/응답 구조는 동일 (경로만 변경)
- Family 도메인 테스트: isPublic 필드 추가는 기존 로직에 영향 없음
- FamilyAdapter 테스트: 기본 CRUD 로직 변경 없음

⚠️ 수정 필요한 테스트:
- API 테스트의 요청 경로: /api/family → /api/families
- SaveFamilyRequest 검증 테스트: 새로운 제약사항 반영
- Family 생성 테스트: OWNER 권한 자동 부여 검증 추가
```

### 6.2 신규 테스트 작성 필요사항
```java
// 1. 가족명 검증 테스트
@Test
void 가족명_20자_초과_시_실패() {
    // given: 21자 가족명
    // when: Family 생성 요청
    // then: 검증 실패
}

@Test  
void 가족명_특수문자_포함_시_실패() {
    // given: 특수문자 포함 가족명
    // when: Family 생성 요청  
    // then: 검증 실패
}

// 2. 중복 체크 테스트
@Test
void 가족명_중복_체크_성공() {
    // given: 기존에 없는 가족명
    // when: 중복 체크 요청
    // then: available=true 응답
}

// 3. 권한 테스트
@Test
void OWNER만_Family_수정_가능() {
    // given: OWNER 권한 사용자
    // when: Family 수정 요청
    // then: 성공
}

@Test
void MEMBER는_Family_수정_불가() {
    // given: MEMBER 권한 사용자
    // when: Family 수정 요청
    // then: 권한 없음 예외
}
```

---

## 7. API 설계 (최종)

### 7.1 변경된 엔드포인트 목록
```
✅ 수정된 기존 API:
POST   /api/families              # Family 생성 (경로 변경)
GET    /api/families/{id}         # Family 단건 조회 
PUT    /api/families/{id}         # Family 수정 (OWNER 권한 체크 추가)
DELETE /api/families/{id}         # Family 삭제 (OWNER 권한 체크 추가)

✅ 신규 추가 API:
GET    /api/families/check-name   # 가족명 중복 체크
GET    /api/families/my           # 내 소속 Family 목록 (전체 조회)
GET    /api/families/public       # 공개 Family 검색 (키워드, 무한 스크롤)
```

### 7.2 요청/응답 형식 표준화
```json
// 성공 응답 (기존 유지)
{
  "data": { ... },
  "message": "성공 메시지"
}

// 에러 응답 (기존 유지)  
{
  "error": {
    "code": "FAMILY_NAME_DUPLICATED",
    "message": "이미 사용 중인 가족명입니다",
    "field": "name"
  }
}

// 무한 스크롤 응답 (신규)
{
  "data": [...],
  "pagination": {
    "nextCursor": "base64encodedstring", // 다음 데이터 요청용 커서
    "hasNext": true,                     // 다음 데이터 존재 여부
    "size": 20                          // 현재 요청 사이즈
  }
}

// 전체 목록 응답 (내 소속 Family - 일반적으로 15개 이하)
{
  "data": [...],
  "totalCount": 5
}
```

---

## 8. 예외 처리 강화

### 8.1 새로운 예외 코드 정의
```java
// FamilyExceptionCode에 추가
public enum FamilyExceptionCode {
    // 기존 코드들...
    
    // 신규 추가
    FAMILY_NAME_TOO_LONG("가족명은 20자 이하로 입력해주세요"),
    FAMILY_NAME_INVALID_PATTERN("가족명에는 한글, 영문, 숫자, 이모지만 사용 가능합니다"),
    FAMILY_NAME_DUPLICATED("이미 사용 중인 가족명입니다"),
    FAMILY_ACCESS_DENIED("이 Family에 접근할 권한이 없습니다"),
    FAMILY_MODIFY_PERMISSION_DENIED("OWNER만 Family 정보를 수정할 수 있습니다"),
    FAMILY_DELETE_PERMISSION_DENIED("OWNER만 Family를 삭제할 수 있습니다");
}
```

### 8.2 사용자 친화적 에러 메시지
```
기존 메시지 개선:
- 기술적 용어 → 일반 사용자 이해 가능한 용어
- 구체적 해결 방법 제시
- 긍정적이고 도움이 되는 톤

예시:
❌ "Validation failed for field 'name'"
✅ "가족명을 확인해주세요. 20자 이하, 한글/영문/숫자/이모지만 사용 가능합니다"
```

---

## 9. 성능 고려사항

### 9.1 데이터베이스 최적화
```sql
-- 검색 성능 향상을 위한 인덱스
CREATE INDEX idx_family_public_name ON family (is_public, name);
CREATE INDEX idx_family_created_at ON family (created_at DESC);

-- 가족명 중복 체크 성능 향상
CREATE UNIQUE INDEX uk_family_name ON family (name) WHERE deleted = false;
```

### 9.2 API 응답 시간 목표
```
- 가족명 중복 체크: < 200ms (실시간 검증)
- Family 목록 조회: < 500ms (사용자별 소속 Family, 전체 조회)
- 공개 Family 검색: < 800ms (키워드 검색 + 무한 스크롤)
- Family 생성: < 1000ms (권한 부여 포함)
```

---

## 10. 보안 강화

### 10.1 권한 기반 접근 제어
```java
// 권한 체크 로직 예시
@PreAuthorize("hasRole('USER')")
public Family findFamily(Long familyId, Long currentUserId) {
    Family family = findFamilyPort.findById(familyId);
    
    // 비공개 Family는 구성원만 조회 가능
    if (!family.getIsPublic()) {
        validateFamilyMember(familyId, currentUserId);
    }
    
    return family;
}

@PreAuthorize("@familyPermissionChecker.isOwner(#familyId, authentication.name)")
public void modifyFamily(Long familyId, ModifyFamilyCommand command) {
    // OWNER 권한 사용자만 수정 가능
}
```

### 10.2 데이터 접근 제한
```
- 비공개 Family: 구성원만 조회 가능
- Family 수정: OWNER만 가능
- Family 삭제: OWNER만 가능  
- 구성원 정보: Family 구성원만 조회 가능
- 소프트 딜리트: 실제 데이터는 30일 보관 후 완전 삭제
```

---

## 11. 개발 전달 사항

### 11.1 기존 코드 재사용 우선순위 (최우선)
```java
✅ 필수 재사용 컴포넌트:
// 1. 기존 Port 인터페이스 (확장하여 사용)
- SaveFamilyPort: 그대로 활용
- FindFamilyPort: findByName() 메서드 추가
- ModifyFamilyPort: 그대로 활용
- SaveFamilyMemberPort: 권한 부여 로직에 활용
- FindFamilyMemberPort: 권한 체크에 활용

// 2. 기존 Service 패턴 (확장하여 사용)  
- SaveFamilyService: OWNER 권한 부여 로직 추가
- FindFamilyService: 권한 체크 로직 추가
- ModifyFamilyService: OWNER 권한 검증 추가

// 3. 기존 도메인 모델 (필드 추가)
- Family: isPublic 필드 추가
- FamilyJpaEntity: isPublic 컬럼 추가
- FamilyMember, FamilyMemberRole: 그대로 활용

// 4. 기존 테스트 패턴 (확장)
- 기존 테스트 구조 유지하며 새로운 테스트 케이스 추가
- TestContainers 설정 재사용
- 기존 테스트 데이터 활용
```

### 11.2 신규 구현이 필요한 부분
```java
✅ 새로 만들어야 할 것들:
// UseCase (신규)
- CheckFamilyNameDuplicationUseCase: 중복 체크 전용 (이미 구현됨)
- FindMyFamiliesUseCase: 사용자별 소속 Family 조회 (기존 FamilyMemberPort 활용)
- FindPublicFamiliesUseCase: 공개 Family 검색 (기존 findByNameContaining 확장)

// Controller (신규/확장)  
- 기존 SaveFamilyController 경로 변경 (이미 완료)
- CheckFamilyNameController: 중복 체크 API (이미 구현됨)
- FindFamilyController에 /my, /public 엔드포인트 추가

// 검색 로직 확장
- FindFamilyPort에 findPublicFamiliesByNameContaining 메서드 추가
- 기존 검색 로직을 공개 Family 필터링으로 확장

// 무한 스크롤 지원 (신규)
- CursorPageable: 커서 기반 페이징 객체
- CursorPage: 커서 페이징 응답 객체
- CursorUtils: Base64 인코딩/디코딩 유틸
```

### 11.3 단계별 개발 계획
```
🎯 Step 1: 기존 코드 확장 (1-2일)
- SaveFamilyController 경로 변경 (/api/families)
- Family 도메인에 isPublic 필드 추가
- SaveFamilyRequest 검증 로직 강화
- Family 생성 시 OWNER 권한 자동 부여

🎯 Step 2: 중복 체크 기능 (1일)
- FindFamilyPort.findByName() 추가
- CheckFamilyNameDuplicationUseCase 구현
- GET /api/families/check-name API 구현

🎯 Step 3: 목록 조회 기능 (3일)
- FindMyFamiliesUseCase 구현 (기존 FamilyMemberPort 활용, 전체 목록)
- FindPublicFamiliesUseCase 구현 (기존 findByNameContaining 확장)
- 기존 키워드 검색 로직에 공개 여부 필터링 (isPublic=true) 추가
- 커서 기반 페이징 유틸리티 구현 (ID + memberCount 정렬)
- GET /api/families/my, /api/families/public API
- 기존 검색 API와의 호환성 유지

🎯 Step 4: 권한 강화 (1일)
- OWNER 권한 체크 로직 구현
- 수정/삭제 API에 권한 검증 추가
- 접근 제어 테스트 작성
```

### 11.4 중요 고려사항
```
⚠️ 기존 코드 호환성:
- 기존 테스트가 깨지지 않도록 주의
- 데이터베이스 마이그레이션 스크립트 필요
- API 버전 호환성 고려 (경로 변경)

⚠️ 성능 고려:
- 가족명 중복 체크는 DB 인덱스 활용
- 공개 Family 검색은 무한 스크롤로 구현 (커서 기반)
- 내 소속 Family는 전체 목록 (일반적으로 15개 이하)
- N+1 쿼리 방지를 위한 fetch join 고려

⚠️ 보안 고려:
- 모든 Family 관련 API에 인증 필수
- 권한 체크는 AOP 또는 Security 설정 활용
- 소프트 딜리트된 Family는 완전히 숨김 처리
```

---

## 12. Epic/Story 구조 정의 (기존 구현 반영)

### Epic: [FT-014] Family 관리 시스템 확장 (Extend Family Management System)

```
Epic-014: Family 관리 시스템 확장 (기존 구현 기반)
├── Story-015: Family API 표준화 및 검증 강화 📅 1주차 (백엔드)
├── Story-016: 가족명 중복 체크 기능 📅 1주차 (백엔드)  
├── Story-017: Family 목록 및 검색 기능 📅 2주차 (백엔드)
└── Story-018: Family 권한 기반 접근 제어 📅 2주차 (백엔드)
```

### Story 상세 정의

#### Story-015: Family API 표준화 및 검증 강화
```
목표: 기존 Family 생성 API를 REST 표준에 맞게 개선하고 검증 로직 강화

✅ 완료 조건:
- /api/family → /api/families 경로 변경 완료
- SaveFamilyRequest에 20자 제한, 패턴 검증 추가
- Family 도메인 및 JpaEntity에 isPublic 필드 추가  
- Family 생성 시 생성자에게 OWNER 권한 자동 부여
- 기존 테스트 모두 통과 + 새로운 검증 테스트 통과

예상 공수: 1-2일
우선순위: High (기존 기능 개선)
```

#### Story-016: 가족명 중복 체크 기능
```
목표: 실시간 가족명 중복 확인 API 구현

✅ 완료 조건:
- GET /api/families/check-name API 구현
- FindFamilyPort에 findByName() 메서드 추가
- CheckFamilyNameDuplicationUseCase 구현
- 중복 체크 API 테스트 통과
- 전체 시스템에서 가족명 유니크 제약 보장

예상 공수: 1일
우선순위: High (사용자 경험 개선)
```

#### Story-017: Family 목록 및 검색 기능
```
목표: 사용자별 소속 Family 목록 조회 및 공개 Family 검색 기능 (기존 검색 로직 확장)

✅ 완료 조건:
- GET /api/families/my API 구현 (내 소속 Family 전체 목록)
- GET /api/families/public API 구현 (기존 키워드 검색 + 공개 필터링 + 무한 스크롤)
- FindMyFamiliesUseCase 구현 (기존 FamilyMemberPort 활용)
- FindPublicFamiliesUseCase 구현 (기존 findByNameContaining 확장)
- 커서 기반 무한 스크롤 처리 및 정렬 기능 구현
- CursorPageable, CursorPage 유틸리티 구현
- 기존 검색 로직과의 호환성 유지
- 목록/검색 API 테스트 통과

세부 구현 계획:
Phase 1: 내 소속 Family 목록 (1일)
- 기존 FamilyMemberPort의 findByUserId 활용
- 전체 목록 조회 (페이징 없음, 일반적으로 15개 이하)

Phase 2: 공개 Family 검색 + 무한 스크롤 (2일)  
- 기존 findByNameContaining(keyword) 로직 확장
- isPublic=true 필터링 추가
- 커서 기반 페이징 (ID + memberCount 기준 정렬)
- 무한 스크롤 응답 구조 구현

예상 공수: 3일 (기존 구현 재사용으로 단축)
우선순위: Medium (편의 기능)
```

#### Story-018: Family 권한 기반 접근 제어
```
목표: OWNER 권한 기반 Family 수정/삭제 제한 및 접근 제어 강화

✅ 완료 조건:
- OWNER만 Family 수정/삭제 가능하도록 권한 체크 구현
- 비공개 Family는 구성원만 조회 가능하도록 제한
- FamilyPermissionChecker 유틸 클래스 구현
- 권한 기반 테스트 케이스 작성 및 통과
- 보안 취약점 검증 완료

예상 공수: 1-2일
우선순위: High (보안 강화)
```

---

## 13. 다음 단계

### 즉시 진행 가능한 작업
1. **기획서 승인 요청** ← 현재 단계  
2. **Story-015 상세 요구사항 작성** (Family API 표준화)
3. **개발자 AI에게 Story-015 할당**
4. **기존 코드 분석 후 단계별 확장 진행**

### 개발 우선순위 (기존 구현 기반)
```
Phase 1 (기존 개선 - 1주차):
- Story-015: Family API 표준화 및 검증 강화 
- Story-016: 가족명 중복 체크 기능

Phase 2 (기능 확장 - 2주차):
- Story-017: Family 목록 및 검색 기능
- Story-018: Family 권한 기반 접근 제어
```

---

## 변경 이력

| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-09 | Family 생성/수정/삭제 기능 초기 기획서 작성 | 신규 프로젝트 시작 | - | 기획자 AI |
| v2.0.0 | 2025-06-09 | 기존 구현 분석 후 미구현 부분만 정리한 수정판 | SaveFamilyController 등 기존 구현 발견, 중복 작업 방지 및 효율적 확장 전략 수립 | 개발: 기존 코드 재사용으로 50% 이상 공수 절약, 검증: 기존 테스트 패턴 활용, 일정: 4주 → 2주로 단축 | 기획자 AI |
| v2.1.0 | 2025-06-09 | 공개 Family 검색 기능을 페이징에서 무한 스크롤로 변경 | 모바일 환경에서 더 자연스러운 UX 제공 및 사용자 경험 개선 | 개발: Story-017 공수 2일→2-3일로 증가, UX: 모바일 친화적 인터페이스로 개선 | 기획자 AI |

| v2.2.0 | 2025-06-10 | Story-017 기획 수정: 기존 구현 분석 반영 | 기존 FindFamilyController와 findByNameContaining 로직 발견, 키워드 검색과 공개 Family 필터링, 무한 스크롤 통합 | 개발: 기존 검색 로직 재사용으로 공수 2일→3일, 호환성: 기존 API 유지하며 신규 기능 추가 | 기획자 AI |

**🎉 Family 관리 시스템 확장 기획 완료!**

기존 구현된 SaveFamilyController, FindFamilyController, CheckFamilyNameController를 최대한 활용하면서, Story-017에서는 기존 키워드 검색 로직을 확장하여 공개 Family 필터링과 무한 스크롤을 통합하는 전략으로 수정되었습니다.
