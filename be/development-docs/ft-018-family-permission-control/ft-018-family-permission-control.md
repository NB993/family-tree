# FT-018 Family 권한 기반 접근 제어 개발 문서

## 문서 정보
- **티켓**: FT-018 (Story-018: Family 권한 기반 접근 제어)
- **Epic**: FT-014 Family 관리 시스템 확장
- **우선순위**: High (보안 강화)
- **작성일**: 2025-06-10
- **작성자**: 개발자 AI

---

## 🎯 개발 목표

**OWNER 권한 기반 Family 수정/삭제 제한 및 접근 제어 강화**

### 완료 조건
1. ✅ OWNER만 Family 수정/삭제 가능하도록 권한 체크 구현
2. ✅ 비공개 Family는 구성원만 조회 가능하도록 제한
3. ✅ 권한 검증 로직 확장 및 통합
4. ✅ 권한 기반 테스트 케이스 작성 및 통과
5. ✅ 보안 취약점 검증 완료

---

## 🔍 기존 권한 검증 구조 분석

### ✅ 이미 구현된 권한 검증 기능

#### 1. **FamilyMemberAuthorizationValidator** (도메인 권한 검증)
```java
// 위치: be/src/main/java/io/jhchoe/familytree/core/family/application/validation/FamilyMemberAuthorizationValidator.java
// 특징: 정적 유틸리티 클래스로 도메인 계층의 권한 검증 담당

주요 메서드:
- validateRole(FamilyMember, FamilyMemberRole): 최소 권한 체크
- validateActiveStatus(FamilyMember): 활성 상태 체크
- validateRoleAndStatus(FamilyMember, FamilyMemberRole): 권한 + 활성 상태 통합 체크
- validateAdminModification(FamilyMember, FamilyMember): 관리자 간 수정 권한 체크
```

#### 2. **FamilyValidationService** (애플리케이션 계층 검증)
```java
// 위치: be/src/main/java/io/jhchoe/familytree/core/family/application/validation/FamilyValidationService.java  
// 특징: Spring Service로 Family 접근 권한 체크 담당

주요 메서드:
- validateFamilyExists(Long familyId): Family 존재 여부 체크
- validateFamilyAccess(Long familyId, Long userId): Family 접근 권한 체크 (구성원 여부)
```

#### 3. **도메인 모델의 권한 체크 메서드**
```java
// FamilyMember 도메인 객체
- hasRoleAtLeast(FamilyMemberRole): 역할 권한 체크
- isActive(): 활성 상태 체크

// FamilyMemberRole 열거형
- isAtLeast(FamilyMemberRole): 역할 비교 (OWNER > ADMIN > MEMBER)
```

#### 4. **인증 시스템 구조**
```java
// @AuthFTUser 어노테이션 - 인증된 사용자 주입
@PutMapping("/{id}")
public ResponseEntity<Long> modifyFamily(
    @PathVariable Long id,
    @Valid @RequestBody ModifyFamilyRequest request,
    @AuthFTUser FTUser ftUser  // ← 이미 인증된 사용자 정보 주입
) { ... }

// SecurityConfig - 시스템 레벨 권한 체크
.requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")  // ← USER/ADMIN 역할 체크
```

---

## 🏗️ 권한 검증 계층 구조 설계

### 📋 핵심 설계 결정사항

#### **Q: 컨트롤러에서 @PreAuthorize("hasRole('USER')") 추가 필요한가?**
**A: 불필요. SecurityConfig에서 이미 `/api/**` 경로에 대해 USER/ADMIN 역할 체크 중**

#### **Q: @AuthFTUser만으로 충분한 권한 검증인가?**
**A: 시스템 권한(USER)은 충분. 도메인 권한(OWNER)은 Service에서 추가 검증 필요**

#### **Q: 기존 FamilyMemberAuthorizationValidator 재사용 가능한가?**
**A: 가능. 기존 패턴 유지하며 OWNER 권한 검증 메서드만 추가**

### 🔄 권한 검증 흐름도

```
[클라이언트 요청]
    ↓
[SecurityConfig] - 시스템 권한 체크 (USER/ADMIN 역할)
    ↓ ✅ 통과
[Controller] - @AuthFTUser로 인증된 사용자 정보 주입
    ↓
[Service] - 도메인 권한 검증
    ├── FamilyValidationService.validateFamilyExists() - Family 존재 체크
    ├── FamilyValidationService.validateFamilyAccess() - 구성원 여부 체크  
    └── FamilyMemberAuthorizationValidator.validateOwnerRole() - OWNER 권한 체크
    ↓ ✅ 모든 검증 통과
[비즈니스 로직 실행]
```

### 📊 계층별 책임 분리

| 계층 | 역할 | 검증 내용 | 도구 |
|------|------|-----------|------|
| **SecurityConfig** | 시스템 권한 | USER/ADMIN 역할 체크 | Spring Security |
| **Controller** | 인증된 사용자 주입 | 인증 완료된 사용자 정보 전달 | @AuthFTUser |
| **Service** | 도메인 권한 | OWNER/MEMBER 권한, 구성원 여부 | 기존 Validator 활용 |

---

## 🛠️ 구현 전략

### ✅ 기존 기능 최대 활용 (재사용 우선)

#### 1. **그대로 활용할 수 있는 기능들**
```java
// 기존 권한 검증 로직 재사용
- FamilyMemberAuthorizationValidator (정적 유틸리티)
- FamilyValidationService (Spring Service)  
- FamilyMember.hasRoleAtLeast() (도메인 메서드)
- FamilyMemberRole.isAtLeast() (열거형 메서드)

// 기존 인증 구조 그대로 유지
- @AuthFTUser 어노테이션
- SecurityConfig의 시스템 권한 체크
```

#### 2. **확장이 필요한 부분만 추가**
```java
// FamilyMemberAuthorizationValidator에 추가할 메서드
public static void validateOwnerRole(FamilyMember member) {
    validateRole(member, FamilyMemberRole.OWNER);
}

public static void validateFamilyModificationPermission(FamilyMember member) {
    validateRoleAndStatus(member, FamilyMemberRole.OWNER);
}

public static void validateFamilyAccessPermission(Family family, FamilyMember member) {
    // 비공개 Family 접근 제어 로직
    if (!family.getIsPublic() && member == null) {
        throw new FTException(FamilyExceptionCode.ACCESS_DENIED);
    }
}
```

### 🚀 단계별 구현 계획

#### **1단계: 코어 계층 개발**
**목표**: 기존 FamilyMemberAuthorizationValidator 확장

```java
구현 내용:
1. validateOwnerRole() 메서드 추가
   - OWNER 권한 전용 검증 로직
   - 기존 validateRole() 메서드 활용

2. validateFamilyModificationPermission() 메서드 추가  
   - Family 수정/삭제 권한 통합 검증
   - 활성 상태 + OWNER 권한 체크

3. validateFamilyAccessPermission() 메서드 추가
   - 비공개 Family 접근 제어
   - 구성원 여부 체크

완료 조건:
- 기존 정적 유틸리티 패턴 유지
- 단위 테스트 작성 및 통과
- 기존 테스트 호환성 유지
```

#### **2단계: 인프라 계층 개발**
**목표**: 기존 Port 확장 (필요시)

```java
구현 내용:
1. 기존 FindFamilyMemberPort 활용
   - findByFamilyIdAndUserId() 메서드 재사용
   - 새로운 포트 생성 없이 기존 구조 활용

2. 기존 FamilyValidationService 활용  
   - validateFamilyExists() 재사용
   - validateFamilyAccess() 재사용

완료 조건:
- 기존 인프라 구조 유지
- 새로운 데이터 액세스 로직 없음 (기존 재사용)
```

#### **3단계: 프레젠테이션 계층 개발**
**목표**: Service 계층에서 권한 검증 통합

```java
구현 내용:
1. ModifyFamilyService 권한 검증 추가
   - OWNER 권한 체크 로직 통합
   - 기존 비즈니스 로직은 변경 없음

2. DeleteFamilyService 권한 검증 추가 (필요시)
   - OWNER 권한 체크 로직 통합

3. FindFamilyService 접근 제어 추가
   - 비공개 Family 접근 제어 로직

4. 통합 테스트 작성
   - 권한별 접근 테스트
   - 비공개 Family 접근 제어 테스트

완료 조건:
- 컨트롤러는 기존 방식 유지 (@AuthFTUser)
- Service에서 도메인 권한 검증
- API 레벨 통합 테스트 통과
```

---

## 🔒 보안 구현 세부사항

### 1. **Family 수정 권한 제어**
```java
// ModifyFamilyService.modify() 메서드에서
public void modify(Long familyId, ModifyFamilyRequest request, Long userId) {
    // 1. Family 존재 여부 확인
    familyValidationService.validateFamilyExists(familyId);
    
    // 2. 구성원 여부 확인  
    familyValidationService.validateFamilyAccess(familyId, userId);
    
    // 3. 구성원 정보 조회
    FamilyMember member = findFamilyMemberPort.findByFamilyIdAndUserId(familyId, userId)
        .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));
    
    // 4. OWNER 권한 검증 (새로 추가)
    FamilyMemberAuthorizationValidator.validateFamilyModificationPermission(member);
    
    // 5. 기존 수정 로직 실행
    // ...
}
```

### 2. **Family 삭제 권한 제어**
```java
// DeleteFamilyService.delete() 메서드에서 (유사한 패턴)
public void delete(Long familyId, Long userId) {
    // 동일한 권한 검증 패턴 적용
    // OWNER 권한 체크 후 삭제 로직 실행
}
```

### 3. **비공개 Family 접근 제어**
```java
// FindFamilyService.findById() 메서드에서
public Family findById(FindFamilyQuery query, Long currentUserId) {
    // 1. Family 조회
    Family family = findFamilyPort.find(query.getId())
        .orElseThrow(() -> new FTException(FamilyExceptionCode.FAMILY_NOT_FOUND));
    
    // 2. 비공개 Family 접근 제어 (새로 추가)
    if (!family.getIsPublic()) {
        // 구성원 여부 확인
        FamilyMember member = findFamilyMemberPort
            .findByFamilyIdAndUserId(query.getId(), currentUserId)
            .orElse(null);
        
        FamilyMemberAuthorizationValidator.validateFamilyAccessPermission(family, member);
    }
    
    return family;
}
```

---

## 🧪 테스트 전략

### 1. **단위 테스트 (1단계)**
```java
// FamilyMemberAuthorizationValidatorTest 확장
class FamilyMemberAuthorizationValidatorTest {
    
    @Test
    @DisplayName("OWNER 권한 검증 - 성공")
    void validateOwnerRole_성공() {
        // given: OWNER 권한 구성원
        // when: validateOwnerRole 호출
        // then: 예외 발생하지 않음
    }
    
    @Test  
    @DisplayName("OWNER 권한 검증 - 실패 (ADMIN 권한)")
    void validateOwnerRole_실패_ADMIN권한() {
        // given: ADMIN 권한 구성원
        // when: validateOwnerRole 호출
        // then: NOT_AUTHORIZED 예외 발생
    }
    
    @Test
    @DisplayName("Family 수정 권한 검증 - 성공")
    void validateFamilyModificationPermission_성공() {
        // given: OWNER 권한 + 활성 상태 구성원
        // when: validateFamilyModificationPermission 호출
        // then: 예외 발생하지 않음
    }
}
```

### 2. **통합 테스트 (3단계)**
```java
// ModifyFamilyControllerTest 확장
class ModifyFamilyControllerTest extends AcceptanceTestBase {
    
    @Test
    @DisplayName("Family 수정 - OWNER 권한 성공")
    @WithMockOAuth2User(id = "1", email = "owner@test.com", role = "USER")
    void modifyFamily_OWNER권한_성공() {
        // given: OWNER 권한 사용자 + Family 데이터 생성
        // when: RestAssured PUT /api/families/{id} 요청
        // then: 200 OK 응답
    }
    
    @Test
    @DisplayName("Family 수정 - ADMIN 권한 실패")
    @WithMockOAuth2User(id = "2", email = "admin@test.com", role = "USER")  
    void modifyFamily_ADMIN권한_실패() {
        // given: ADMIN 권한 사용자 + Family 데이터
        // when: RestAssured PUT /api/families/{id} 요청
        // then: 403 Forbidden 응답
    }
    
    @Test
    @DisplayName("비공개 Family 조회 - 구성원 성공")
    @WithMockOAuth2User(id = "1", email = "member@test.com", role = "USER")
    void findFamily_비공개_구성원_성공() {
        // given: 비공개 Family + 구성원 사용자
        // when: RestAssured GET /api/families/{id} 요청
        // then: 200 OK 응답
    }
    
    @Test
    @DisplayName("비공개 Family 조회 - 비구성원 실패")
    @WithMockOAuth2User(id = "999", email = "outsider@test.com", role = "USER")
    void findFamily_비공개_비구성원_실패() {
        // given: 비공개 Family + 비구성원 사용자
        // when: RestAssured GET /api/families/{id} 요청  
        // then: 403 Forbidden 응답
    }
}

### 3. **테스트 커버리지 목표**
- **단위 테스트**: 90% 이상 (권한 검증 로직)
- **통합 테스트**: 주요 시나리오 100% (OWNER/ADMIN/MEMBER/비구성원)
- **예외 테스트**: 모든 권한 관련 예외 케이스

---

## ⚠️ 중요 고려사항

### 1. **기존 코드 호환성 유지**
```java
원칙:
- 기존 API의 요청/응답 구조 변경 없음
- 기존 비즈니스 로직 변경 최소화  
- 기존 테스트가 깨지지 않도록 주의
- 권한 체크만 추가, 핵심 로직은 유지

확인사항:
- 기존 Family 조회/수정 API 동작 보장
- 기존 테스트 케이스 모두 통과
- API 응답 형식 일관성 유지
```

### 2. **계층별 책임 분리**
```java
컨트롤러 계층:
- 인증된 사용자 정보만 Service로 전달
- 권한 검증 로직 포함하지 않음
- @AuthFTUser 어노테이션만 사용

Service 계층:  
- 모든 도메인 권한 검증 수행
- 비즈니스 로직 실행 전 권한 체크
- 적절한 예외 발생으로 접근 거부

도메인 계층:
- 기존 권한 체크 메서드 활용
- 새로운 권한 검증 유틸리티 추가
```

### 3. **미래 확장성 고려**
```java
확장 가능성:
- GUEST 역할 추가 시에도 안전한 구조
- 새로운 권한 체크 로직 추가 용이
- 다른 도메인에서도 재사용 가능한 패턴

설계 원칙:
- 정적 유틸리티 패턴 유지 (의존성 최소화)
- 기존 검증 서비스 재사용 우선
- 새로운 클래스 생성보다는 기존 확장
```

### 4. **성능 고려사항**
```java
최적화 전략:
- 기존 Family 접근 권한 체크 재사용
- 불필요한 데이터베이스 조회 방지
- 권한 체크 순서 최적화 (빠른 실패)

조회 순서:
1. Family 존재 여부 (빠른 실패)
2. 구성원 여부 확인  
3. 권한 레벨 체크
4. 비즈니스 로직 실행
```

---

## 📋 예상 변경 파일 목록

### 1단계: 코어 계층
```
수정:
- FamilyMemberAuthorizationValidator.java (메서드 추가)

생성:
- FamilyMemberAuthorizationValidatorTest.java (테스트 확장)
```

### 3단계: 프레젠테이션 계층  
```
수정:
- ModifyFamilyService.java (권한 검증 추가)
- FindFamilyService.java (접근 제어 추가)
- DeleteFamilyService.java (권한 검증 추가, 필요시)

확장:
- ModifyFamilyControllerTest.java (권한 테스트 추가)
- FindFamilyControllerTest.java (접근 제어 테스트 추가)
```

---

## 🎯 완료 후 기대 효과

### 보안 강화
- OWNER만 Family 수정/삭제 가능하여 데이터 무결성 보장
- 비공개 Family 접근 제어로 개인정보 보호 강화
- 계층적 권한 체계로 명확한 접근 제어

### 코드 품질 향상  
- 기존 패턴 재사용으로 일관성 있는 구조
- 도메인 권한과 시스템 권한의 명확한 분리
- 재사용 가능한 권한 검증 유틸리티

### 유지보수성 개선
- 권한 관련 로직의 중앙 집중화
- 새로운 권한 요구사항 추가 용이
- 테스트 코드로 권한 동작 보장

---

## 📚 참고 문서

1. **기존 구현 분석**
   - `FamilyMemberAuthorizationValidator.java`
   - `FamilyValidationService.java`
   - `SecurityConfig.java`

2. **개발 가이드라인**
   - `be/instructions/architecture-overview.md`
   - `be/instructions/coding-standards.md`
   - `be/instructions/testing-guidelines.md`

3. **관련 Story**
   - FT-014: Family 관리 시스템 확장 (Epic)
   - FT-015: Family 생성 시 OWNER 권한 자동 부여
   - FT-016: 가족명 중복 체크 기능
   - FT-017: Family 목록 및 검색 기능

---

**개발 문서 작성 완료. 기획자 AI 검토 및 승인 후 1단계 코어 계층 개발을 시작하겠습니다.** ✅
