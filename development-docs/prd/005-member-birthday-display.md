# PRD-005: 멤버 목록 생일/나이 표시 기능

## 문서 정보
- **작성일**: 2025-01-06
- **상태**: 📝 작성중
- **우선순위**: 중간
- **영향 범위**: 백엔드 (be), 프론트엔드 (fe)

---

## 1. 배경 및 문제점

### 1.1 현재 상황
- 멤버 목록에서 **이름**과 **연락처**를 위아래로 표시
- 연락처 정보가 없어서 `-`만 출력됨
- 생일 데이터(`memberBirthday`)는 있지만 표시하지 않음
- 카카오에서 제공하는 `birthday_type`(양력/음력) 정보를 저장하지 않음

### 1.2 현재 UI

```
┌─────────────────────────────────────┐
│ [아바타] 홍길동                  [>] │
│          - (연락처 없음)            │
└─────────────────────────────────────┘
```

### 1.3 문제점

| 문제 | 설명 |
|------|------|
| 불필요한 정보 표시 | 연락처가 없어서 `-`만 표시됨 |
| 생일 미표시 | 유용한 생일 정보가 있지만 활용 안 됨 |
| 양력/음력 미구분 | 카카오에서 `birthday_type` 제공하지만 저장 안 함 |
| 스켈레톤 UI 위치 불일치 | 로딩 시 스켈레톤이 실제 콘텐츠 위치와 안 맞음 |

---

## 2. 목표

1. **연락처 제거**: 불필요한 `-` 표시 제거
2. **생일 표시**: 양력/음력 토글 지원
3. **나이 표시**: 한국나이/만나이 토글 지원
4. **birthday_type 저장**: 카카오에서 제공하는 양력/음력 정보 저장
5. **스켈레톤 UI 정렬**: 실제 콘텐츠 위치와 일치시킴

---

## 3. 변경 후 UI (텍스트 와이어프레임)

### 3.1 기본 상태

```
┌───────────────────────────────────────────────┐
│ [아바타] 홍길동 (35)        1990.12.25    [>] │
└───────────────────────────────────────────────┘
         ↑이름    ↑한국나이    ↑양력생일
```

### 3.2 나이 토글 (클릭 시)

```
┌───────────────────────────────────────────────┐
│ [아바타] 홍길동 (만 34)     1990.12.25    [>] │
└───────────────────────────────────────────────┘
                  ↑만나이
```

### 3.3 생일 토글 (클릭 시)

```
┌───────────────────────────────────────────────┐
│ [아바타] 홍길동 (35)     (음) 1990.11.15  [>] │
└───────────────────────────────────────────────┘
                           ↑음력생일
```

### 3.4 스켈레톤 UI (로딩 시)

```
┌───────────────────────────────────────────────┐
│ [████] ████████ (██)       ██████████     [>] │
└───────────────────────────────────────────────┘
  ↑좌측 정렬 (실제 콘텐츠 위치와 동일)
```

---

## 4. 유스케이스

### UC-1: 멤버 목록에서 생일/나이 확인

- **전제조건**: 사용자가 홈화면 멤버 목록을 보고 있음
- **기본 흐름**:
  1. 멤버 카드에 이름, 나이(한국나이), 생일(양력) 표시
  2. 나이 영역 클릭 시 만나이로 토글
  3. 생일 영역 클릭 시 음력으로 토글 (음력 변환 라이브러리 사용)
  4. 다시 클릭 시 원래 상태로 복귀
- **예외 흐름**:
  - E1: 생일 정보 없음 → 나이/생일 영역 표시 안 함
  - E2: birthday_type이 LUNAR인 경우 → 기본이 음력, 토글 시 양력

### UC-2: 카카오 로그인으로 초대 수락 시 birthday_type 저장

- **전제조건**: 사용자가 초대 링크를 통해 카카오 로그인
- **기본 흐름**:
  1. 카카오 OAuth에서 `birthday`, `birthyear`, `birthday_type` 수신
  2. User 엔티티에 birthday + birthdayType 저장
  3. FamilyMember 생성 시 User의 birthdayType 복사
  4. API 응답에 birthdayType 포함
- **예외 흐름**:
  - E1: birthday_type 미제공 → null 저장 (기본 양력으로 표시)

---

## 5. 상세 설계

### 5.1 백엔드 변경

#### 5.1.1 BirthdayType Enum 추가

```java
// core/family/domain/BirthdayType.java
public enum BirthdayType {
    SOLAR,  // 양력
    LUNAR   // 음력
}
```

#### 5.1.2 KakaoUserInfo 수정

```java
// common/auth/domain/KakaoUserInfo.java
public BirthdayType getBirthdayType() {
    if (account == null) {
        return null;
    }
    String type = (String) account.get("birthday_type");
    if ("LUNAR".equals(type)) {
        return BirthdayType.LUNAR;
    }
    return BirthdayType.SOLAR;  // 기본값
}
```

#### 5.1.3 User 엔티티 수정

| 필드 | 타입 | 설명 |
|------|------|------|
| birthdayType | BirthdayType | 양력/음력 구분 (nullable) |

#### 5.1.4 FamilyMember 수정

| 필드 | 타입 | 설명 |
|------|------|------|
| birthdayType | BirthdayType | 양력/음력 구분 (nullable) |

#### 5.1.5 API 응답 수정

```json
// GET /api/families/{familyId}/home/members
{
  "memberId": 1,
  "memberName": "홍길동",
  "memberBirthday": "1990-12-25T00:00:00",
  "memberBirthdayType": "SOLAR",  // 추가
  ...
}
```

### 5.2 프론트엔드 변경

#### 5.2.1 나이 계산 유틸리티

```typescript
// utils/age.ts

// 한국 나이 (세는 나이)
export const getKoreanAge = (birthday: Date): number => {
  const today = new Date();
  return today.getFullYear() - birthday.getFullYear() + 1;
};

// 만 나이
export const getWesternAge = (birthday: Date): number => {
  const today = new Date();
  const age = today.getFullYear() - birthday.getFullYear();
  const monthDiff = today.getMonth() - birthday.getMonth();
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthday.getDate())) {
    return age - 1;
  }
  return age;
};
```

#### 5.2.2 음력 변환 (라이브러리 사용)

```typescript
// utils/lunar.ts
import { Solar, Lunar } from 'lunar-javascript';  // 또는 korean-lunar-calendar

// 양력 → 음력
export const solarToLunar = (date: Date): { year: number; month: number; day: number } => {
  const solar = Solar.fromDate(date);
  const lunar = solar.getLunar();
  return {
    year: lunar.getYear(),
    month: lunar.getMonth(),
    day: lunar.getDay(),
  };
};

// 음력 → 양력
export const lunarToSolar = (year: number, month: number, day: number): Date => {
  const lunar = Lunar.fromYmd(year, month, day);
  const solar = lunar.getSolar();
  return solar.toDate();
};
```

#### 5.2.3 HomePage.tsx 멤버 카드 수정

```tsx
// 토글 상태 관리
const [ageDisplayMode, setAgeDisplayMode] = useState<'korean' | 'western'>('korean');
const [birthdayDisplayMode, setBirthdayDisplayMode] = useState<Record<number, 'original' | 'converted'>>({});

// 멤버 카드 렌더링
<div className="flex items-center gap-2 px-3 py-1.5">
  {/* 아바타 */}
  <div className="w-6 h-6 rounded-full bg-primary/10 ...">
    <span>{member.memberName.charAt(0)}</span>
  </div>

  {/* 이름 + 나이 */}
  <div className="flex items-center gap-1 min-w-0">
    <span className="text-xs font-medium truncate">{member.memberName}</span>
    {member.memberBirthday && (
      <span
        className="text-[10px] text-muted-foreground cursor-pointer"
        onClick={(e) => { e.stopPropagation(); toggleAgeMode(); }}
      >
        {ageDisplayMode === 'korean'
          ? `(${getKoreanAge(member.memberBirthday)})`
          : `(만 ${getWesternAge(member.memberBirthday)})`}
      </span>
    )}
  </div>

  {/* 생일 */}
  {member.memberBirthday && (
    <span
      className="text-[10px] text-muted-foreground ml-auto cursor-pointer"
      onClick={(e) => { e.stopPropagation(); toggleBirthdayMode(member.memberId); }}
    >
      {formatBirthday(member, birthdayDisplayMode[member.memberId])}
    </span>
  )}

  <ChevronRight className="w-3 h-3" />
</div>
```

---

## 6. 데이터베이스 변경

### 6.1 ft_user 테이블

```sql
ALTER TABLE ft_user
ADD COLUMN birthday_type VARCHAR(10) DEFAULT NULL;

COMMENT ON COLUMN ft_user.birthday_type IS '생일 유형 (SOLAR: 양력, LUNAR: 음력)';
```

### 6.2 family_member 테이블

```sql
ALTER TABLE family_member
ADD COLUMN birthday_type VARCHAR(10) DEFAULT NULL;

COMMENT ON COLUMN family_member.birthday_type IS '생일 유형 (SOLAR: 양력, LUNAR: 음력)';
```

---

## 7. 예외 케이스

| 상황 | 처리 |
|------|------|
| 생일 정보 없음 | 나이/생일 영역 표시 안 함 |
| birthday_type이 null | 양력(SOLAR)으로 간주 |
| birthday_type이 LUNAR | 기본 표시가 음력, 토글 시 양력 변환 |
| 음력 윤달인 경우 | 변환 라이브러리가 처리 |

---

## 8. 영향 범위

### 8.1 백엔드 수정 파일

| 파일 | 변경 내용 |
|------|----------|
| `core/family/domain/BirthdayType.java` | **신규** - Enum 생성 |
| `common/auth/domain/KakaoUserInfo.java` | getBirthdayType() 추가 |
| `common/auth/service/OAuth2UserServiceImpl.java` | birthdayType 추출 및 저장 |
| `common/auth/UserJpaEntity.java` | birthdayType 필드 추가 |
| `core/user/domain/User.java` | birthdayType 필드 추가 |
| `core/family/domain/FamilyMember.java` | birthdayType 필드 추가 |
| `core/family/adapter/out/persistence/FamilyMemberJpaEntity.java` | birthdayType 필드 추가 |
| `core/family/adapter/in/response/*Response.java` | birthdayType 필드 추가 |
| DB 마이그레이션 | `V{N}__add_birthday_type.sql` |

### 8.2 프론트엔드 수정 파일

| 파일 | 변경 내용 |
|------|----------|
| `utils/age.ts` | **신규** - 나이 계산 유틸리티 |
| `utils/lunar.ts` | **신규** - 음력 변환 유틸리티 |
| `api/services/familyService.ts` | memberBirthdayType 타입 추가 |
| `pages/HomePage.tsx` | 멤버 카드 UI 변경 |
| `package.json` | 음력 변환 라이브러리 추가 |

---

## 9. 구현 순서

### Phase 1: 백엔드 (birthday_type 저장)
1. BirthdayType Enum 생성
2. KakaoUserInfo에 getBirthdayType() 추가
3. User, UserJpaEntity에 birthdayType 필드 추가
4. FamilyMember, FamilyMemberJpaEntity에 birthdayType 필드 추가
5. DB 마이그레이션 스크립트 작성
6. API 응답에 birthdayType 포함
7. 테스트 작성

### Phase 2: 프론트엔드 (UI 변경)
1. 음력 변환 라이브러리 설치 (`lunar-javascript` 또는 `korean-lunar-calendar`)
2. 나이 계산 유틸리티 작성
3. 음력 변환 유틸리티 작성
4. HomePage 멤버 카드 UI 변경
5. 토글 기능 구현
6. 스켈레톤 UI 정렬 수정
7. 테스트 및 검증

---

## 10. 라이브러리 선정

### 음력 변환 라이브러리 비교

| 라이브러리 | 크기 | 특징 |
|-----------|------|------|
| `lunar-javascript` | ~50KB | 중국/한국 음력 지원, 풍부한 기능 |
| `korean-lunar-calendar` | ~10KB | 한국 음력 전용, 경량 |

**권장**: `korean-lunar-calendar` (한국 음력만 필요하므로 경량 선택)
