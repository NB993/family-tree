# FT-014 Epic: Family 관리 시스템 확장

## Epic 개요
- **Epic 번호**: FT-014
- **Epic 명**: Family 관리 시스템 확장 (Extend Family Management System)
- **시작일**: 2025-06-09
- **예상 완료일**: 2025-06-23 (2주)
- **담당**: 개발자 AI

## 배경 및 목적

### 기존 구현 현황
✅ **이미 구현된 컴포넌트들**
- SaveFamilyController: Family 생성 API (POST /api/family)
- Family 엔티티: 기본 정보 관리 (name, description, profileUrl)
- 소프트 딜리트: deleted 필드로 논리적 삭제 지원
- 권한 관리: FamilyMember와 FamilyMemberRole로 권한 체계 구축
- 기본 CRUD: 생성, 조회, 수정 기능의 기본 틀 완성

### 미구현/개선 필요사항
❌ **확장이 필요한 부분들**
1. 엔드포인트 변경: /api/family → /api/families (REST 표준)
2. 가족명 검증 강화: 20자 제한, 특수문자 제한, 전체 시스템 중복 체크
3. 공개/비공개 설정: isPublic 필드 추가 및 관련 로직
4. 가족명 중복 체크 API: 실시간 중복 확인 기능
5. 공개 Family 검색 API: 키워드 기반 검색 및 무한 스크롤
6. 사용자별 소속 Family 목록 API: 내 소속 Family 조회
7. 권한 기반 접근 제어: OWNER만 수정/삭제 가능한 로직

## Story 구성

### Story-015: Family API 표준화 및 검증 강화
- **목표**: 기존 Family 생성 API를 REST 표준에 맞게 개선하고 검증 로직 강화
- **기간**: 1-2일
- **우선순위**: High

**완료 조건:**
- [x] /api/family → /api/families 경로 변경 완료
- [x] SaveFamilyRequest에 20자 제한, 패턴 검증 추가
- [x] Family 도메인 및 JpaEntity에 isPublic 필드 추가
- [x] Family 생성 시 생성자에게 OWNER 권한 자동 부여
- [x] 기존 테스트 모두 통과 + 새로운 검증 테스트 통과

### Story-016: 가족명 중복 체크 기능
- **목표**: 실시간 가족명 중복 확인 API 구현
- **기간**: 1일
- **우선순위**: High

**완료 조건:**
- [x] GET /api/families/check-name API 구현
- [x] FindFamilyPort에 findByName() 메서드 추가
- [x] CheckFamilyNameDuplicationUseCase 구현
- [x] 중복 체크 API 테스트 통과
- [x] 전체 시스템에서 가족명 유니크 제약 보장

### Story-017: Family 목록 및 검색 기능
- **목표**: 사용자별 소속 Family 목록 조회 및 공개 Family 검색 기능 (무한 스크롤)
- **기간**: 2-3일
- **우선순위**: Medium

**완료 조건:**
- [x] GET /api/families/my API 구현 (내 소속 Family 전체 목록)
- [x] GET /api/families/public API 구현 (키워드 검색 + 무한 스크롤)
- [x] FindMyFamiliesUseCase, FindPublicFamiliesUseCase 구현
- [x] 커서 기반 무한 스크롤 처리 및 정렬 기능 구현
- [x] CursorPageable, CursorPage 유틸리티 구현
- [x] 목록/검색 API 테스트 통과

### Story-018: Family 권한 기반 접근 제어
- **목표**: OWNER 권한 기반 Family 수정/삭제 제한 및 접근 제어 강화
- **기간**: 1-2일
- **우선순위**: High

**완료 조건:**
- [x] OWNER만 Family 수정/삭제 가능하도록 권한 체크 구현
- [x] 비공개 Family는 구성원만 조회 가능하도록 제한
- [x] FamilyPermissionChecker 유틸 클래스 구현
- [x] 권한 기반 테스트 케이스 작성 및 통과
- [x] 보안 취약점 검증 완료

## 기술적 고려사항

### 데이터베이스 스키마 변경
```sql
-- Family 테이블에 공개/비공개 설정 추가
ALTER TABLE family 
ADD COLUMN is_public BOOLEAN NOT NULL DEFAULT FALSE;

-- 가족명 유니크 제약 추가
ALTER TABLE family 
ADD CONSTRAINT uk_family_name UNIQUE (name);

-- 검색 성능 향상을 위한 인덱스
CREATE INDEX idx_family_public_name ON family (is_public, name);
CREATE INDEX idx_family_created_at ON family (created_at DESC);
```

### 성능 목표
- 가족명 중복 체크: < 200ms (실시간 검증)
- Family 목록 조회: < 500ms (사용자별 소속 Family)
- 공개 Family 검색: < 800ms (키워드 검색 + 무한 스크롤)
- Family 생성: < 1000ms (권한 부여 포함)

### 보안 고려사항
- 모든 Family 관련 API에 인증 필수
- 권한 체크는 AOP 또는 Security 설정 활용
- 비공개 Family는 구성원만 조회 가능
- OWNER만 Family 수정/삭제 가능
- 소프트 딜리트된 Family는 완전히 숨김 처리

## 기존 코드 재사용 전략

### 1순위: Port/Adapter 패턴 활용
```java
// 기존 Port 인터페이스 확장
- SaveFamilyPort: 그대로 활용
- FindFamilyPort: findByName() 메서드 추가
- ModifyFamilyPort: 그대로 활용
- SaveFamilyMemberPort: 권한 부여 로직에 활용
- FindFamilyMemberPort: 권한 체크에 활용
```

### 2순위: Domain 모델 확장
```java
// Family 도메인 모델
- isPublic 필드 추가
- newFamily() 메서드 시그니처 확장
- 기존 로직은 그대로 유지
```

### 3순위: UseCase 로직 확장
```java
// 기존 UseCase 확장
- SaveFamilyService: OWNER 권한 부여 로직 추가
- FindFamilyService: 권한 체크 로직 추가
- ModifyFamilyService: OWNER 권한 검증 추가
```

## 예상 위험사항 및 대응책

### 위험사항
1. **기존 테스트 실패**: API 경로 변경으로 인한 테스트 깨짐
2. **데이터베이스 마이그레이션**: isPublic 필드 추가 시 기존 데이터 처리
3. **성능 이슈**: 가족명 중복 체크 및 검색 기능의 응답 속도

### 대응책
1. **점진적 마이그레이션**: 기존 API 유지하며 새 API 병행 운영
2. **기본값 설정**: isPublic 기본값을 FALSE로 설정하여 안전하게 마이그레이션
3. **인덱스 최적화**: 검색 쿼리에 대한 적절한 인덱스 생성

## 성공 지표

### 정량적 지표
- 모든 기존 테스트 통과율: 100%
- 새로운 테스트 커버리지: 90% 이상
- API 응답 시간: 목표치 달성률 100%
- 코드 재사용률: 70% 이상

### 정성적 지표
- REST API 표준 준수 완료
- 보안 강화 및 권한 체계 완성
- 사용자 경험 개선 (실시간 중복 체크, 무한 스크롤)

## 완료 후 기대효과
- 기존 Family 생성 기능의 안정성 및 사용성 대폭 향상
- REST API 표준 준수로 일관된 API 인터페이스 제공
- 실시간 검증 및 검색 기능으로 사용자 경험 개선
- 권한 기반 접근 제어로 보안 강화
- 확장 가능한 Family 관리 시스템 기반 구축
