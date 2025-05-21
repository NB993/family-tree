# Family 구성원 권한 관리 기능

## 개발할 기능
- Family 구성원 권한 역할(OWNER, ADMIN, MEMBER) 관리 기능
- 권한 별 구성원 상태(ACTIVE, SUSPENDED, BANNED) 관리 기능
- 상태 변경 이력 관리 기능
- 권한 기반 공지사항 작성 기능

## 기획 및 유효성 검증 정책
- [x] OWNER만 다른 구성원의 역할 변경 가능
- [x] OWNER 역할은 변경 불가능
- [x] 자신의 역할은 변경 불가능
- [x] OWNER와 ADMIN만 다른 구성원의 상태 변경 가능
- [x] ADMIN은 다른 ADMIN의 상태 변경 불가능
- [x] OWNER 상태는 변경 불가능
- [x] 자신의 상태는 변경 불가능
- [x] 구성원 상태 변경 시 이력 저장
- [x] Family 구성원만 Family 내 역할 정보 조회 가능
- [ ] OWNER와 ADMIN만 공지사항 작성 가능

## 개발 단계
- [x] application 계층 개발
- [ ] adapter/out 계층 개발
- [ ] adapter/in 계층 개발

## 개발 내용 요약

### 도메인 모델 및 비즈니스 규칙 개발 

1. **FamilyMemberRole** 열거형 추가
   - 구성원 역할 (OWNER, ADMIN, MEMBER)를 정의
   - 역할 간 권한 계층 구조 구현 (OWNER > ADMIN > MEMBER)

2. **FamilyMember** 클래스 수정
   - role 필드 추가
   - 역할 관리를 위한 메서드 추가 (updateRole, hasRoleAtLeast)
   - 역할 기반 상태 관리 메서드 개선 (updateStatus)

3. **FamilyMemberStatusHistory** 클래스 추가
   - 구성원 상태 변경 이력 저장 기능 구현
   - 상태 변경 시 변경자, 시간, 사유 추적

4. **Announcement** 클래스 추가
   - Family 내 공지사항 관리 기능 추가
   - 제목, 내용, 작성자 정보 관리

### UseCase와 포트 개발

1. **UpdateFamilyMemberRoleUseCase** 추가
   - 구성원 역할 변경 기능 구현
   - OWNER만 역할 변경 가능하도록 비즈니스 규칙 적용

2. **UpdateFamilyMemberStatusUseCase** 추가
   - 구성원 상태 변경 기능 구현
   - 권한 기반 상태 변경 로직 구현 (OWNER, ADMIN 권한)

3. **FindFamilyMembersRoleUseCase** 추가
   - Family 내 구성원 역할 조회 기능 구현

4. 아웃바운드 포트 확장 및 추가
   - **FindFamilyMemberPort**: 구성원 조회 기능 확장
   - **UpdateFamilyMemberPort**: 구성원 정보 업데이트 기능 추가
   - **SaveFamilyMemberStatusHistoryPort**: 상태 변경 이력 저장 기능 추가

### 서비스 구현

1. **FamilyMemberRoleService**
   - UpdateFamilyMemberRoleUseCase 및 FindFamilyMembersRoleUseCase 구현
   - 역할 변경 권한 검증 로직 구현

2. **FamilyMemberStatusService**
   - UpdateFamilyMemberStatusUseCase 구현
   - 상태 변경 권한 검증 로직 구현
   - 상태 변경 이력 저장 기능 구현

### 테스트 작성

1. 커맨드/쿼리 클래스 테스트
   - UpdateFamilyMemberRoleCommandTest
   - UpdateFamilyMemberStatusCommandTest
   - FindFamilyMembersRoleQueryTest

2. 도메인 모델 테스트
   - FamilyMemberRoleTest
   - FamilyMemberTest (기존 테스트 확장)
   - FamilyMemberStatusHistoryTest
   - AnnouncementTest

3. 서비스 클래스 테스트
   - FamilyMemberRoleServiceTest
   - FamilyMemberStatusServiceTest
