# Family 구성원 권한 관리 기능 세부 명세서

## 세부 기능 명세서

### 1. FamilyMemberRole 모델 및 권한 시스템

#### 1.1 FamilyMemberRole 데이터 모델
- `FamilyMemberRole` 열거형(Enum) 정의
  - `OWNER`: Family 생성자(소유자)
  - `ADMIN`: 관리자 권한
  - `MEMBER`: 일반 구성원
- `FamilyMember` 엔티티에 `role` 필드 추가
- 기본값: 생성자는 `OWNER`, 일반 가입자는 `MEMBER`

#### 1.2 권한별 수행 가능 작업
- `OWNER`
  - Family 삭제
  - 모든 구성원 권한 관리 (ADMIN 승격/강등 포함)
  - ADMIN의 모든 권한 포함
- `ADMIN`
  - 가입 신청 승인/거절
  - 구성원 상태 관리 (정지/강퇴/활성화)
  - 공지사항 작성/수정/삭제
  - MEMBER의 모든 권한 포함
- `MEMBER`
  - Family 정보 조회
  - 구성원 목록 조회
  - 공지사항 조회

### 2. 구성원 권한 관리 API

#### 2.1 구성원 권한 조회 API
- 엔드포인트: `GET /api/families/{familyId}/members/roles`
- 요청 파라미터:
  - `familyId`: Family ID
- 응답 형식:
  ```json
  {
    "members": [
      {
        "id": "멤버 ID",
        "userId": "유저 ID",
        "name": "이름",
        "profileUrl": "프로필 이미지 URL",
        "role": "OWNER/ADMIN/MEMBER"
      },
      ...
    ]
  }
  ```
- 접근 권한: OWNER, ADMIN

#### 2.2 구성원 권한 변경 API
- 엔드포인트: `PATCH /api/families/{familyId}/members/{memberId}/role`
- 요청 파라미터:
  - `familyId`: Family ID
  - `memberId`: 변경 대상 구성원 ID
- 요청 본문:
  ```json
  {
    "role": "ADMIN 또는 MEMBER"
  }
  ```
- 응답 형식:
  ```json
  {
    "id": "멤버 ID",
    "role": "변경된 역할",
    "updatedAt": "변경 시간"
  }
  ```
- 접근 권한: OWNER만 가능
- 제약 조건:
  - OWNER 역할은 변경 불가 (Family 생성자는 항상 OWNER)
  - 자기 자신의 권한은 변경 불가
  - OWNER는 다른 OWNER를 지정할 수 없음

### 3. 가입 신청 처리 기능

#### 3.1 가입 신청 목록 조회 API
- 엔드포인트: `GET /api/families/{familyId}/join-requests`
- 요청 파라미터:
  - `familyId`: Family ID
  - `status`: 신청 상태 필터 (PENDING/APPROVED/REJECTED, 기본값: PENDING)
- 응답 형식:
  ```json
  {
    "requests": [
      {
        "id": "신청 ID",
        "userId": "유저 ID",
        "userName": "유저 이름",
        "profileUrl": "프로필 이미지 URL",
        "message": "가입 신청 메시지",
        "status": "PENDING/APPROVED/REJECTED",
        "createdAt": "신청 시간"
      },
      ...
    ]
  }
  ```
- 접근 권한: OWNER, ADMIN

#### 3.2 가입 신청 처리 API
- 엔드포인트: `PATCH /api/families/{familyId}/join-requests/{requestId}`
- 요청 파라미터:
  - `familyId`: Family ID
  - `requestId`: 가입 신청 ID
- 요청 본문:
  ```json
  {
    "status": "APPROVED 또는 REJECTED",
    "message": "승인/거절 사유 (선택사항)"
  }
  ```
- 응답 형식:
  ```json
  {
    "id": "신청 ID",
    "status": "변경된 상태",
    "updatedAt": "처리 시간",
    "processedBy": "처리자 ID"
  }
  ```
- 접근 권한: OWNER, ADMIN
- 비즈니스 로직:
  - 승인 시 자동으로 FamilyMember 생성 (MEMBER 역할 부여)
  - 이미 처리된 신청은 수정 불가

### 4. 구성원 상태 관리 기능

#### 4.1 구성원 상태 변경 API
- 엔드포인트: `PATCH /api/families/{familyId}/members/{memberId}/status`
- 요청 파라미터:
  - `familyId`: Family ID
  - `memberId`: 변경 대상 구성원 ID
- 요청 본문:
  ```json
  {
    "status": "ACTIVE/SUSPENDED/BANNED",
    "reason": "상태 변경 사유 (선택사항)"
  }
  ```
- 응답 형식:
  ```json
  {
    "id": "멤버 ID",
    "status": "변경된 상태",
    "updatedAt": "변경 시간",
    "updatedBy": "변경자 ID"
  }
  ```
- 접근 권한: OWNER, ADMIN
- 제약 조건:
  - OWNER 상태는 변경 불가
  - 자기 자신의 상태는 변경 불가
  - ADMIN은 다른 ADMIN의 상태를 변경할 수 없음 (OWNER만 가능)

#### 4.2 구성원 상태 변경 이력 조회 API
- 엔드포인트: `GET /api/families/{familyId}/members/{memberId}/status-history`
- 요청 파라미터:
  - `familyId`: Family ID
  - `memberId`: 구성원 ID
- 응답 형식:
  ```json
  {
    "statusHistory": [
      {
        "id": "상태 변경 이력 ID",
        "status": "ACTIVE/SUSPENDED/BANNED",
        "reason": "상태 변경 사유",
        "updatedBy": "변경자 ID",
        "updatedAt": "변경 시간"
      },
      ...
    ]
  }
  ```
- 접근 권한: OWNER, ADMIN

### 5. 공지사항 관리 기능

#### 5.1 공지사항 목록 조회 API
- 엔드포인트: `GET /api/families/{familyId}/announcements`
- 요청 파라미터:
  - `familyId`: Family ID
  - `page`: 페이지 번호 (기본값: 0)
  - `size`: 페이지 크기 (기본값: 10)
- 응답 형식:
  ```json
  {
    "announcements": [
      {
        "id": "공지사항 ID",
        "title": "제목",
        "content": "내용",
        "createdBy": "작성자 ID",
        "createdAt": "작성 시간",
        "updatedAt": "수정 시간"
      },
      ...
    ],
    "totalElements": "전체 공지사항 수",
    "totalPages": "전체 페이지 수",
    "currentPage": "현재 페이지"
  }
  ```
- 접근 권한: 모든 Family 구성원

#### 5.2 공지사항 작성 API
- 엔드포인트: `POST /api/families/{familyId}/announcements`
- 요청 파라미터:
  - `familyId`: Family ID
- 요청 본문:
  ```json
  {
    "title": "공지사항 제목",
    "content": "공지사항 내용"
  }
  ```
- 응답 형식:
  ```json
  {
    "id": "생성된 공지사항 ID",
    "title": "제목",
    "content": "내용",
    "createdAt": "작성 시간"
  }
  ```
- 접근 권한: OWNER, ADMIN

#### 5.3 공지사항 수정 API
- 엔드포인트: `PUT /api/families/{familyId}/announcements/{announcementId}`
- 요청 파라미터:
  - `familyId`: Family ID
  - `announcementId`: 공지사항 ID
- 요청 본문:
  ```json
  {
    "title": "수정된 제목",
    "content": "수정된 내용"
  }
  ```
- 응답 형식:
  ```json
  {
    "id": "공지사항 ID",
    "title": "수정된 제목",
    "content": "수정된 내용",
    "updatedAt": "수정 시간"
  }
  ```
- 접근 권한: OWNER, ADMIN

#### 5.4 공지사항 삭제 API
- 엔드포인트: `DELETE /api/families/{familyId}/announcements/{announcementId}`
- 요청 파라미터:
  - `familyId`: Family ID
  - `announcementId`: 공지사항 ID
- 응답 형식: 204 No Content
- 접근 권한: OWNER, ADMIN

## 데이터 모델 개념 설계

### Family 도메인

```
Family
- id: Long
- name: String
- description: String
- profileUrl: String
- createdBy: Long
- createdAt: LocalDateTime
- modifiedBy: Long
- modifiedAt: LocalDateTime
```

### FamilyMember 도메인 (변경)

```
FamilyMember
- id: Long
- familyId: Long
- userId: Long
- name: String
- profileUrl: String
- birthday: LocalDateTime
- nationality: String
- status: FamilyMemberStatus
- role: FamilyMemberRole (추가)
- createdBy: Long
- createdAt: LocalDateTime
- modifiedBy: Long
- modifiedAt: LocalDateTime
```

### FamilyMemberRole 열거형 (신규)

```
FamilyMemberRole
- OWNER
- ADMIN
- MEMBER
```

### FamilyJoinRequest 도메인

```
FamilyJoinRequest
- id: Long
- familyId: Long
- userId: Long
- message: String
- status: FamilyJoinRequestStatus
- processedBy: Long
- processedAt: LocalDateTime
- createdBy: Long
- createdAt: LocalDateTime
- modifiedBy: Long
- modifiedAt: LocalDateTime
```

### FamilyMemberStatusHistory 도메인 (신규)

```
FamilyMemberStatusHistory
- id: Long
- familyId: Long
- memberId: Long
- status: FamilyMemberStatus
- reason: String
- createdBy: Long
- createdAt: LocalDateTime
```

### Announcement 도메인 (신규)

```
Announcement
- id: Long
- familyId: Long
- title: String
- content: String
- createdBy: Long
- createdAt: LocalDateTime
- modifiedBy: Long
- modifiedAt: LocalDateTime
```

## 시스템 간 상호작용 다이어그램

```
1. 권한 변경 프로세스
User -> API Gateway -> FamilyMemberController -> FamilyMemberService -> FamilyMemberRepository -> Database

2. 가입 신청 처리 프로세스
User -> API Gateway -> FamilyJoinRequestController -> FamilyJoinRequestService
      -> [승인 시] FamilyMemberService -> FamilyMemberRepository -> Database
      -> [거절 시] FamilyJoinRequestRepository -> Database

3. 구성원 상태 변경 프로세스
User -> API Gateway -> FamilyMemberController -> FamilyMemberService 
      -> FamilyMemberRepository -> Database
      -> FamilyMemberStatusHistoryRepository -> Database

4. 공지사항 관리 프로세스
User -> API Gateway -> AnnouncementController -> AnnouncementService -> AnnouncementRepository -> Database
```

## 구현 로드맵 및 마일스톤

### 1단계: 기반 구조 구축 (D+0 ~ D+2)
- FamilyMemberRole 모델 구현
- FamilyMember 도메인 확장
- 권한 검증 로직 구현

### 2단계: 핵심 기능 구현 (D+3 ~ D+7)
- 구성원 권한 관리 API 구현
- 가입 신청 처리 API 구현
- 구성원 상태 관리 API 구현

### 3단계: 추가 기능 구현 (D+8 ~ D+12)
- 구성원 상태 변경 이력 관리 구현
- 공지사항 관리 API 구현

### 4단계: 검증 및 마무리 (D+13 ~ D+15)
- 통합 테스트
- API 문서화
- 버그 수정 및 성능 최적화
