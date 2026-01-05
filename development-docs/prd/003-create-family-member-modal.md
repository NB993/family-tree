# PRD-003: FamilyMember 수동 등록 모달

## 문서 정보
- **작성일**: 2025-01-05
- **상태**: 🔄 진행 중 (프론트엔드만 구현)
- **우선순위**: 중간

### 진행 상황
| 단계 | 내용 | 상태 |
|------|------|------|
| 1단계 | 프론트엔드 모달 컴포넌트 구현 | ✅ 완료 (`53b55f5`) |
| 1-1 | 프론트엔드 테스트 구현 | ✅ 완료 (`4a481b6`) |
| 2단계 | 백엔드 API 구현 | ⏳ 미진행 |

---

## 개요
HomePage의 `등록` 버튼 클릭 시 FamilyMember를 수동으로 등록하는 모달 구현

## 결정사항
| 항목 | 결정 |
|------|------|
| 프로필 이미지 | 기본 이미지 사용 (경로 주석 처리, 추후 이미지 제공 예정) |
| 관계 설정 | 모달에 포함 |
| 백엔드 API | 미구현 (프론트엔드만 선 구현) |

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

## 구현 계획

### 1. CreateFamilyMemberModal 컴포넌트 생성 (신규)
**경로**: `fe/src/components/family/CreateFamilyMemberModal.tsx`

```tsx
interface CreateFamilyMemberModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  familyId: number;
  onSuccess?: () => void;
}
```

**구현 내용**:
- Dialog 컴포넌트 사용 (`@/components/ui/dialog`)
- 폼 상태 관리: `useState`
- 관계 선택: Select + 조건부 Input (CUSTOM)
- 프로필 이미지: 기본 이미지 경로 주석 처리
- API 호출: `useCreateFamilyMember` 훅 사용 (백엔드 없으므로 실패 예상)
  - TODO 주석으로 백엔드 API 필요 표시

### 2. HomePage 수정
**경로**: `fe/src/pages/HomePage.tsx`

**변경 내용**:
- 모달 open state 추가: `const [isCreateModalOpen, setIsCreateModalOpen] = useState(false)`
- `등록` 버튼 onClick 변경: `navigate('/families')` → `setIsCreateModalOpen(true)`
- CreateFamilyMemberModal 컴포넌트 렌더링

---

## 변경 대상 파일
1. `fe/src/components/family/CreateFamilyMemberModal.tsx` (신규)
2. `fe/src/pages/HomePage.tsx` (수정)
3. `fe/src/components/family/__tests__/CreateFamilyMemberModal.test.tsx` (신규, 테스트 11개)
4. `fe/craco.config.js` (수정, jest path alias 설정 추가)

---

## 참고: 기존 인프라
| 항목 | 경로 |
|------|------|
| Dialog 컴포넌트 | `fe/src/components/ui/dialog.tsx` |
| Select 컴포넌트 | `fe/src/components/ui/select.tsx` |
| 관계 타입 enum | `fe/src/types/family.ts` (FamilyMemberRelationshipType) |
| API 훅 | `fe/src/hooks/queries/useFamilyQueries.ts` (useCreateFamilyMember) |
| API 서비스 | `fe/src/api/services/familyService.ts` (createFamilyMember) |

---

## 백엔드 API (추후 개발 필요)
- **엔드포인트**: `POST /api/families/{familyId}/members`
- **요청 본문**: name, birthday, relationship, customRelationship
- **응답**: FamilyMember 객체