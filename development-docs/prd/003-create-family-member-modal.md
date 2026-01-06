# PRD-003: FamilyMember 수동 등록 모달

## 문서 정보
- **작성일**: 2025-01-05
- **상태**: ✅ 완료
- **우선순위**: 중간

### 진행 상황
| 단계 | 내용 | 상태 |
|------|------|------|
| 1단계 | 프론트엔드 모달 컴포넌트 구현 | ✅ 완료 (`53b55f5`) |
| 1-1 | 프론트엔드 테스트 구현 | ✅ 완료 (`4a481b6`) |
| 2단계 | 백엔드 API 구현 | ✅ 완료 |
| 2-1 | 프론트엔드 API 연동 수정 | ✅ 완료 |
| 3단계 | relationship 필드 분리 리팩토링 | ✅ 완료 |

---

## 개요
HomePage의 `등록` 버튼 클릭 시 FamilyMember를 수동으로 등록하는 모달 구현

## 결정사항
| 항목 | 결정 |
|------|------|
| 프로필 이미지 | 기본 이미지 사용 (경로 주석 처리, 추후 이미지 제공 예정) |
| 관계 설정 | 모달에 포함 |
| 백엔드 API | ✅ 구현 완료 |

---

## 2단계: 백엔드 API 구현 (완료)

### API 명세
| 항목 | 내용 |
|------|------|
| 엔드포인트 | `POST /api/families/{familyId}/members` |
| 인증 | 필수 (로그인 사용자) |
| 권한 | 해당 Family 구성원만 |

### 요청 본문
```json
{
  "name": "홍길동",
  "birthday": "1990-01-15T00:00:00",
  "relationshipType": "FATHER",
  "customRelationship": null
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| name | String | ✅ | 구성원 이름 (50자 이내) |
| birthday | LocalDateTime | ❌ | 생년월일 (ISO 8601) |
| relationshipType | Enum | ❌ | FamilyMemberRelationshipType |
| customRelationship | String | ❌ | CUSTOM일 때 필수 (50자 이내) |

### 응답
```json
{
  "id": 123
}
```

### HTTP 상태 코드
| 코드 | 설명 |
|------|------|
| 201 | Created - 등록 성공 |
| 400 | Bad Request - 유효성 검증 실패 |
| 404 | Not Found - Family 없음 또는 구성원 아님 |

### 생성된 파일 (백엔드)
```
be/src/main/java/.../core/family/
├── adapter/in/
│   ├── SaveFamilyMemberController.java
│   ├── request/SaveFamilyMemberRequest.java
│   └── response/SaveFamilyMemberResponse.java
├── application/
│   ├── port/in/SaveFamilyMemberCommand.java
│   ├── port/in/SaveFamilyMemberUseCase.java
│   └── service/SaveFamilyMemberService.java

be/src/test/java/.../core/family/
├── adapter/in/
│   ├── SaveFamilyMemberControllerTest.java (인수 테스트)
│   └── SaveFamilyMemberDocsTest.java (API 문서 테스트)
├── application/
│   ├── port/in/SaveFamilyMemberCommandTest.java
│   └── service/SaveFamilyMemberServiceTest.java
```

### 수정된 파일 (프론트엔드)
| 파일 | 변경 내용 |
|------|----------|
| `fe/src/api/services/familyService.ts` | `CreateFamilyMemberForm`에 `relationshipType`, `customRelationship` 추가 |
| `fe/src/api/services/familyService.ts` | `createFamilyMember` 응답 타입을 `{ id: number }`로 수정 |
| `fe/src/components/family/CreateFamilyMemberModal.tsx` | API 호출 시 `relationshipType`, `customRelationship` 전송 |
| `fe/src/components/family/CreateFamilyMemberModal.tsx` | `birthday` 형식을 `YYYY-MM-DDT00:00:00`으로 변환 |

---

## 3단계: relationship 필드 분리 리팩토링 (완료)

### 배경
현재 `FamilyMember.relationship` 필드가 String 타입으로 선언되어 있어 enum(`FamilyMemberRelationshipType`)을 사용한 의미가 퇴색됨. CUSTOM 관계와 기본 관계를 구분하여 저장하는 구조로 리팩토링 필요.

### 현재 구조 (문제점)
```java
// FamilyMember.java
private final String relationship; // "FATHER" 또는 "외할아버지" 저장
```
- enum 타입 안전성 없음
- DB 레벨 제약 불가
- 통계/필터링 어려움

### 목표 구조
```java
// FamilyMember.java
private final FamilyMemberRelationshipType relationshipType; // nullable
private final String customRelationship; // CUSTOM일 때만 값 존재

public String getRelationshipDisplayName() {
    if (relationshipType == null) return null;
    if (relationshipType == CUSTOM) return customRelationship;
    return relationshipType.getDisplayName();
}
```

### DB 스키마 변경
```sql
-- 기존
relationship VARCHAR(255)

-- 변경 후
relationship_type VARCHAR(50)     -- enum name (FATHER, MOTHER, CUSTOM 등)
custom_relationship VARCHAR(50)   -- CUSTOM일 때만 사용자 입력값
```

### 변경 대상 파일
| 레이어 | 파일 | 변경 내용 |
|--------|------|----------|
| Domain | `FamilyMember.java` | `relationship` → `relationshipType` + `customRelationship` |
| Infra | `FamilyMemberJpaEntity.java` | 두 컬럼으로 분리 |
| Infra | `FamilyMemberAdapter.java` | 매핑 로직 수정 |
| Core | `SaveFamilyMemberService.java` | 저장 로직 수정 |
| DB | `V4__split_relationship_column.sql` | 마이그레이션 |
| Test | 관련 테스트 전체 | 필드 변경 반영 |

### 마이그레이션 전략
1. 새 컬럼 추가 (`relationship_type`, `custom_relationship`)
2. 기존 데이터 마이그레이션:
   - enum name인 경우 → `relationship_type`에 저장
   - 그 외 → `relationship_type = 'CUSTOM'`, `custom_relationship`에 저장
3. 기존 `relationship` 컬럼 삭제

---

## 모달 UI 설계

### 입력 필드
| 필드 | 필수 | 컴포넌트 | 설명 |
|------|------|----------|------|
| 이름 | ✅ | Input | 구성원 이름 |
| 관계 | ❌ | Select | FamilyMemberRelationshipType enum 사용 |
| 직접입력 | ❌ | Input | 관계가 'CUSTOM'일 때만 표시 |
| 생년월일 | ❌ | Input (date) | yyyy-MM-dd 형식 |

### 버튼
- **등록**: 폼 제출 (로딩 상태 표시)
- **취소**: 모달 닫기

---

## 참고: 기존 인프라
| 항목 | 경로 |
|------|------|
| Dialog 컴포넌트 | `fe/src/components/ui/dialog.tsx` |
| Select 컴포넌트 | `fe/src/components/ui/select.tsx` |
| 관계 타입 enum | `fe/src/types/family.ts` (FamilyMemberRelationshipType) |
| API 훅 | `fe/src/hooks/queries/useFamilyQueries.ts` (useCreateFamilyMember) |
| API 서비스 | `fe/src/api/services/familyService.ts` (createFamilyMember) |
