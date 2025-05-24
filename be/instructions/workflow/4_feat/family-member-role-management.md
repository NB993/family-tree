# Family 구성원 권한 관리 기능 설계

## 도메인 모델 상세 설계

### 1. FamilyMemberRole (추가)

Family 구성원의 역할을 나타내는 열거형으로, 역할별 권한 수준을 정의합니다.

```java
public enum FamilyMemberRole {
    OWNER,  // 소유자 (최상위 권한)
    ADMIN,  // 관리자
    MEMBER; // 일반 구성원
    
    public boolean isAtLeast(FamilyMemberRole role) {
        return this.ordinal() <= role.ordinal();
    }
}
```

- OWNER: Family 생성자로 모든 권한을 가짐
- ADMIN: 관리자로, 구성원 관리와 공지사항 등의 기능 수행 가능
- MEMBER: 일반 구성원으로 기본 조회 권한만 보유

### 2. FamilyMember (수정)

기존 FamilyMember 도메인 모델에 역할(role) 필드를 추가하고 관련 메서드 구현.

```java
// 추가될 필드
private final FamilyMemberRole role;

// 새로 추가될 생성 메서드 (OWNER 역할로 생성)
public static FamilyMember newOwner(Long familyId, Long userId, String name, 
                                 String profileUrl, LocalDateTime birthday, 
                                 String nationality) {
    // OWNER 역할로 생성
    return new FamilyMember(null, familyId, userId, name, profileUrl, birthday, 
                          nationality, FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER, 
                          null, null, null, null);
}

// 기존 newMember 메서드 수정 (MEMBER 역할 지정)
public static FamilyMember newMember(Long familyId, Long userId, String name, 
                                   String profileUrl, LocalDateTime birthday, 
                                   String nationality) {
    // MEMBER 역할로 생성
    return new FamilyMember(null, familyId, userId, name, profileUrl, birthday, 
                          nationality, FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER, 
                          null, null, null, null);
}

// 역할 변경 메서드
public FamilyMember updateRole(FamilyMemberRole newRole) {
    // OWNER 역할은 변경 불가
    if (this.role == FamilyMemberRole.OWNER) {
        throw new IllegalStateException("Cannot change role of the Family OWNER");
    }
    
    return new FamilyMember(this.id, this.familyId, this.userId, this.name, this.profileUrl, 
                          this.birthday, this.nationality, this.status, newRole, 
                          this.createdBy, this.createdAt, this.modifiedBy, this.modifiedAt);
}

// 상태 변경 메서드 (기존 메서드 수정)
public FamilyMember updateStatus(FamilyMemberStatus newStatus) {
    // OWNER 상태는 변경 불가
    if (this.role == FamilyMemberRole.OWNER) {
        throw new IllegalStateException("Cannot change status of the Family OWNER");
    }
    
    return new FamilyMember(this.id, this.familyId, this.userId, this.name, this.profileUrl, 
                          this.birthday, this.nationality, newStatus, this.role, 
                          this.createdBy, this.createdAt, this.modifiedBy, this.modifiedAt);
}

// 권한 확인 메서드
public boolean hasRoleAtLeast(FamilyMemberRole requiredRole) {
    return this.role.isAtLeast(requiredRole);
}

// 구성원 활성화 상태 확인
public boolean isActive() {
    return this.status == FamilyMemberStatus.ACTIVE;
}
```

### 3. FamilyMemberStatusHistory (추가)

구성원 상태 변경 이력을 관리하는 도메인 모델입니다.

```java
public class FamilyMemberStatusHistory {
    private final Long id;
    private final Long familyId;
    private final Long memberId;
    private final FamilyMemberStatus status;
    private final String reason;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    
    // 생성자 (private)
    
    // 새 이력 생성 메서드
    public static FamilyMemberStatusHistory create(Long familyId, Long memberId, 
                                                 FamilyMemberStatus status, String reason) {
        return new FamilyMemberStatusHistory(null, familyId, memberId, status, reason, null, null);
    }
    
    // ID를 포함한 기존 이력 생성 메서드
    public static FamilyMemberStatusHistory withId(Long id, Long familyId, Long memberId, 
                                                 FamilyMemberStatus status, String reason, 
                                                 Long createdBy, LocalDateTime createdAt) {
        return new FamilyMemberStatusHistory(id, familyId, memberId, status, reason, createdBy, createdAt);
    }
}
```

### 4. Announcement (추가)

Family 내 공지사항을 관리하는 도메인 모델입니다.

```java
public class Announcement {
    private final Long id;
    private final Long familyId;
    private final String title;
    private final String content;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final Long modifiedBy;
    private final LocalDateTime modifiedAt;
    
    // 생성자 (private)
    
    // 새 공지사항 생성 메서드
    public static Announcement create(Long familyId, String title, String content) {
        return new Announcement(null, familyId, title, content, null, null, null, null);
    }
    
    // ID를 포함한 기존 공지사항 생성 메서드
    public static Announcement withId(Long id, Long familyId, String title, String content, 
                                    Long createdBy, LocalDateTime createdAt, 
                                    Long modifiedBy, LocalDateTime modifiedAt) {
        return new Announcement(id, familyId, title, content, createdBy, createdAt, modifiedBy, modifiedAt);
    }
    
    // 공지사항 수정 메서드
    public Announcement update(String title, String content) {
        return new Announcement(this.id, this.familyId, title, content, 
                              this.createdBy, this.createdAt, this.modifiedBy, this.modifiedAt);
    }
}
```

## 도메인 모델 관계도

```
[Family] 1 --- * [FamilyMember]
    |               |
    |               | (상태 변경 이력)
    |               |
    |               * [FamilyMemberStatusHistory]
    |
    | (공지사항)
    |
    * [Announcement]
```

- Family와 FamilyMember: 1:N 관계
- FamilyMember와 FamilyMemberStatusHistory: 1:N 관계
- Family와 Announcement: 1:N 관계

## 개발 및 QA 단계
- [x] 1단계: 코어 계층 개발
- [x] 1단계: 코어 계층 QA 검증
- [x] 2단계: 인프라 계층 개발
- [x] 2단계: 인프라 계층 QA 검증
- [ ] 3단계: 프레젠테이션 계층 개발
- [ ] 3단계: 프레젠테이션 계층 QA 검증

### 1. 인바운드 포트 (UseCase)

#### 1.1 UpdateFamilyMemberRoleUseCase

구성원 역할 변경 유스케이스

```java
public interface UpdateFamilyMemberRoleUseCase {
    Long updateRole(UpdateFamilyMemberRoleCommand command);
}

public class UpdateFamilyMemberRoleCommand {
    private final Long familyId;
    private final Long memberId;
    private final Long currentUserId;
    private final FamilyMemberRole newRole;
    
    // 생성자 및 유효성 검증 메서드
}
```

#### 1.2 UpdateFamilyMemberStatusUseCase

구성원 상태 변경 유스케이스

```java
public interface UpdateFamilyMemberStatusUseCase {
    Long updateStatus(UpdateFamilyMemberStatusCommand command);
}

public class UpdateFamilyMemberStatusCommand {
    private final Long familyId;
    private final Long memberId;
    private final Long currentUserId;
    private final FamilyMemberStatus newStatus;
    private final String reason;
    
    // 생성자 및 유효성 검증 메서드
}
```

#### 1.3 FindFamilyMembersRoleUseCase

Family 구성원 역할 조회 유스케이스

```java
public interface FindFamilyMembersRoleUseCase {
    List<FamilyMember> findAllByFamilyId(FindFamilyMembersRoleQuery query);
}

public class FindFamilyMembersRoleQuery {
    private final Long familyId;
    private final Long currentUserId;
    
    // 생성자 및 유효성 검증 메서드
}
```

#### 1.4 ProcessFamilyJoinRequestUseCase

가입 신청 처리 유스케이스

```java
public interface ProcessFamilyJoinRequestUseCase {
    Long process(ProcessFamilyJoinRequestCommand command);
}

public class ProcessFamilyJoinRequestCommand {
    private final Long familyId;
    private final Long requestId;
    private final Long currentUserId;
    private final FamilyJoinRequestStatus newStatus;
    private final String message;
    
    // 생성자 및 유효성 검증 메서드
}
```

#### 1.5 AnnouncementUseCase

공지사항 관리 유스케이스

```java
public interface SaveAnnouncementUseCase {
    Long save(SaveAnnouncementCommand command);
}

public class SaveAnnouncementCommand {
    private final Long familyId;
    private final Long currentUserId;
    private final String title;
    private final String content;
    
    // 생성자 및 유효성 검증 메서드
}
```

### 2. 아웃바운드 포트

#### 2.1 FindFamilyMemberPort (확장)

```java
public interface FindFamilyMemberPort {
    // 기존 메서드들
    
    List<FamilyMember> findAllByFamilyId(Long familyId);
    Optional<FamilyMember> findByFamilyIdAndUserId(Long familyId, Long userId);
}
```

#### 2.2 UpdateFamilyMemberPort (추가)

```java
public interface UpdateFamilyMemberPort {
    Long update(FamilyMember familyMember);
}
```

#### 2.3 SaveFamilyMemberStatusHistoryPort (추가)

```java
public interface SaveFamilyMemberStatusHistoryPort {
    Long save(FamilyMemberStatusHistory history);
}
```

#### 2.4 SaveAnnouncementPort (추가)

```java
public interface SaveAnnouncementPort {
    Long save(Announcement announcement);
}
```

#### 2.5 FindAnnouncementPort (추가)

```java
public interface FindAnnouncementPort {
    List<Announcement> findAllByFamilyId(Long familyId, int page, int size);
    Optional<Announcement> findById(Long id);
}
```

## 기술적 이슈 및 해결방안

### 1. 권한 검증 방식

**이슈**: 여러 API에서 권한 검증 로직이 반복되어야 함

**해결방안**:
- `FamilyMemberAuthorizationValidator` 유틸리티 클래스 구현
- 특정 작업에 필요한 최소 권한과 현재 구성원의 역할을 비교하여 권한 검증
- AOP를 활용한 권한 검증 어노테이션 (`@RequireFamilyRole`) 구현 고려

```java
public class FamilyMemberAuthorizationValidator {
    public static void validateRole(FamilyMember member, FamilyMemberRole requiredRole) {
        if (!member.hasRoleAtLeast(requiredRole)) {
            throw new FTException(FamilyExceptionCode.NOT_AUTHORIZED);
        }
    }
    
    public static void validateActiveStatus(FamilyMember member) {
        if (!member.isActive()) {
            throw new FTException(FamilyExceptionCode.MEMBER_NOT_ACTIVE);
        }
    }
}
```

### 2. Family 구성원 상태와 역할의 정합성 유지

**이슈**: 특정 상태와 역할 조합이 유효하지 않을 수 있음 (예: 정지 상태의 관리자)

**해결방안**:
- 도메인 모델에 상태와 역할 변경 시 유효성 검사 로직 추가
- 서비스 계층에서 추가 유효성 검증 수행
- 동일 트랜잭션 내에서 상태와 역할 변경 동시 수행 지원

### 3. 구성원 관리 권한의 세분화

**이슈**: ADMIN 역할을 가진 구성원들 간의 권한 충돌 가능성

**해결방안**:
- ADMIN은 다른 ADMIN의 상태나 역할을 변경할 수 없도록 제한
- OWNER만 ADMIN 역할을 부여하거나 회수할 수 있도록 설계
- 권한 변경 이력 관리를 통한 감사 추적 기능 구현

## API 엔드포인트 설계

### 1. 구성원 역할 조회 API
```
GET /api/families/{familyId}/members/roles
- 권한: MEMBER 이상
- 응답: 해당 가족의 모든 구성원 역할 정보
```

### 2. 구성원 역할 변경 API
```
PUT /api/families/{familyId}/members/{memberId}/role
- 권한: OWNER만 가능
- 요청: {
  "newRole": "ADMIN"
}
- 응답: {
  "success": true,
  "data": {
    "memberId": 123,
    "role": "ADMIN",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

### 3. 구성원 상태 변경 API
```
PUT /api/families/{familyId}/members/{memberId}/status
- 권한: ADMIN 이상
- 요청: {
  "newStatus": "SUSPENDED",
  "reason": "규칙 위반"
}
- 응답: {
  "success": true,
  "data": {
    "memberId": 123,
    "status": "SUSPENDED",
    "reason": "규칙 위반",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

### 4. 공지사항 작성 API
```
POST /api/families/{familyId}/announcements
- 권한: ADMIN 이상
- 요청: {
  "title": "가족 모임 공지",
  "content": "다음 주 일요일에 가족 모임이 있습니다."
}
- 응답: {
  "success": true,
  "data": {
    "announcementId": 456,
    "title": "가족 모임 공지",
    "content": "다음 주 일요일에 가족 모임이 있습니다.",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

### 5. 공지사항 조회 API
```
GET /api/families/{familyId}/announcements?page=0&size=10
- 권한: MEMBER 이상
- 응답: {
  "success": true,
  "data": {
    "announcements": [
      {
        "id": 456,
        "title": "가족 모임 공지",
        "content": "다음 주 일요일에 가족 모임이 있습니다.",
        "createdBy": "홍길동",
        "createdAt": "2024-01-15T10:30:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1
  }
}
```

## 에러 코드 및 예외 처리

### 권한 관련 예외
- `FM001`: 권한이 부족함 (403 Forbidden)
- `FM002`: 비활성화된 구성원 (403 Forbidden)
- `FM003`: OWNER 역할 변경 시도 (400 Bad Request)
- `FM004`: OWNER 상태 변경 시도 (400 Bad Request)

### 데이터 검증 예외
- `FM005`: 존재하지 않는 가족 구성원 (404 Not Found)
- `FM006`: 잘못된 역할 값 (400 Bad Request)
- `FM007`: 잘못된 상태 값 (400 Bad Request)
- `FM008`: 필수 필드 누락 (400 Bad Request)

### 비즈니스 규칙 위반 예외
- `FM009`: 자기 자신의 역할 변경 시도 (400 Bad Request)
- `FM010`: ADMIN이 다른 ADMIN 역할 변경 시도 (400 Bad Request)
- `FM011`: 공지사항 내용 길이 초과 (400 Bad Request)

## 성능 고려사항

### 1. 캐싱 전략
- **구성원 역할 정보**: Redis를 활용한 캐싱
  - Key: `family:{familyId}:member:{memberId}:role`
  - TTL: 1시간
  - 역할 변경 시 캐시 무효화

### 2. 데이터베이스 최적화
- **인덱스 설계**:
  - `family_member(family_id, role)` 복합 인덱스
  - `family_member_status_history(member_id, created_at)` 복합 인덱스
  - `announcement(family_id, created_at)` 복합 인덱스

### 3. 페이징 및 정렬
- 공지사항 조회 시 페이징 필수 적용
- 최신 공지사항부터 정렬 (created_at DESC)
- 구성원 목록 조회 시 역할별 정렬 (OWNER → ADMIN → MEMBER)

## 보안 고려사항

### 1. 권한 검증 레벨
- **API 레벨**: Controller에서 기본 권한 확인
- **비즈니스 레벨**: Service에서 세부 권한 검증
- **데이터 레벨**: Repository에서 family_id 기반 접근 제어

### 2. 민감 정보 보호
- 상태 변경 이력의 reason 필드는 로그에 기록하지 않음
- 권한 변경 이력은 감사 로그로 별도 관리
- API 응답에서 내부 시스템 정보 노출 방지

### 3. 입력값 검증
- 모든 사용자 입력에 대한 XSS 방지 처리
- SQL Injection 방지를 위한 PreparedStatement 사용
- 파일 업로드 시 확장자 및 크기 제한

## 배포 및 통합 지침

### 1. 데이터 마이그레이션 계획
```sql
-- FamilyMember 테이블에 role 컬럼 추가
ALTER TABLE family_member ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'MEMBER';

-- Family 생성자를 OWNER로 업데이트
UPDATE family_member fm 
SET role = 'OWNER' 
WHERE fm.id = (
    SELECT MIN(fm2.id) 
    FROM family_member fm2 
    WHERE fm2.family_id = fm.family_id
);

-- FamilyMemberStatusHistory 테이블 생성
CREATE TABLE family_member_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    family_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    reason VARCHAR(500),
    created_by BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (family_id) REFERENCES family(id),
    FOREIGN KEY (member_id) REFERENCES family_member(id),
    INDEX idx_member_created (member_id, created_at)
);

-- Announcement 테이블 생성
CREATE TABLE announcement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    family_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    created_by BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_by BIGINT,
    modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (family_id) REFERENCES family(id),
    INDEX idx_family_created (family_id, created_at)
);
```

### 2. 단계적 배포 전략
- **1단계**: 도메인 모델, 애플리케이션 서비스 및 DB 스키마 변경
- **2단계**: 권한 관리 API 배포 (기존 기능 영향 없음)
- **3단계**: 상태 관리 API 배포 (점진적 적용)
- **4단계**: 공지사항 API 배포 (신규 기능)

### 3. API 버전 관리
- 기존 API는 유지하며 새로운 기능은 `/v2/` 엔드포인트로 제공
- 권한 검증이 필요한 기존 API에 점진적으로 권한 검증 로직 적용
- 3개월 후 기존 API deprecated 예정

### 4. 모니터링 및 알림
- 권한 변경 이벤트에 대한 실시간 알림 설정
- 비정상적인 권한 변경 패턴 감지
- API 응답 시간 및 에러율 모니터링

## 테스트 전략

### 1. 단위 테스트
- 도메인 모델의 비즈니스 로직 검증
- 권한 검증 로직 테스트
- 예외 상황 처리 검증

### 2. 통합 테스트
- API 엔드포인트별 권한 시나리오 테스트
- 데이터베이스 트랜잭션 검증
- 캐시 동작 검증

### 3. 성능 테스트
- 대량 구성원 조회 성능 측정
- 권한 변경 시 캐시 무효화 성능 확인
- 동시 접근 시나리오 테스트

### 4. 보안 테스트
- 권한 우회 시도 방지 검증
- SQL Injection 방지 확인
- XSS 공격 방지 검증

이 설계 문서를 바탕으로 3단계 프레젠테이션 계층 개발을 진행할 수 있습니다.
