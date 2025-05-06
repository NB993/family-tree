# Family Tree 비즈니스 로직

## 핵심 도메인 모델

### Family (가족)
- Family는 여러 FamilyMember(가족 구성원)로 구성됩니다.
- Family는 고유한 이름, 설명, 프로필 이미지를 가질 수 있습니다.
- 한 명의 사용자(User)는 여러 Family에 속할 수 있습니다.
- Family 생성 시 생성자는 자동으로 해당 Family의 첫 번째 멤버가 됩니다.

### FamilyMember (가족 구성원)
- FamilyMember는 한 가족 내의 구성원을 나타냅니다.
- 각 구성원은 이름, 생년월일, 사망 여부, 국적 등의 정보를 가집니다.
- FamilyMember는 ACTIVE, SUSPENDED, BANNED 상태를 가질 수 있습니다.
- 사용자(User)는 여러 Family에서 각각 다른 FamilyMember로 존재할 수 있습니다.

### 가족 관계 (Family Relationships)
- 가족 구성원 간에는 다양한 관계가 존재합니다:
  - 부모-자녀 관계 (BIOLOGICAL, ADOPTED)
  - 배우자 관계 (결혼 날짜, 이혼 날짜 포함)
- 이러한 관계는 Family 내에서만 유효합니다.

## 핵심 비즈니스 규칙

### Family 관리
- 사용자는 새로운 Family를 생성할 수 있습니다.
- Family 생성 시 반드시 이름을 지정해야 합니다.
- Family 이름은 100자를 초과할 수 없습니다.
- Family 설명은 선택 사항이며 200자를 초과할 수 없습니다.
- Family 프로필 URL은 선택 사항이지만, 제공될 경우 유효한 URL 형식이어야 합니다.
- Family 정보 수정은 해당 Family의 멤버만 가능합니다.

### FamilyMember 관리
- FamilyMember는 Family에 속해야 합니다 (외부 FamilyMember는 존재할 수 없음).
- 사용자는 여러 Family에서 서로 다른 FamilyMember로 등록될 수 있습니다.
- FamilyMember는 생성 시 ACTIVE 상태로 시작합니다.
- FamilyMember는 Family 내에서 관리자에 의해 SUSPENDED 또는 BANNED 상태로 변경될 수 있습니다.
- FamilyMember의 프로필은 Family 내에서 공유됩니다.

### 가족 관계 규칙
- 부모-자녀 관계에서 한 사람은 최대 두 명의 생물학적 부모를 가질 수 있습니다.
- 입양 관계는 실제 부모-자녀 관계와 별도로 등록될 수 있습니다.
- 배우자 관계는 시작일(결혼일)과 선택적으로 종료일(이혼일)을 가질 수 있습니다.
- 동일인물이 같은 시간대에 중복된 배우자 관계를 가질 수 없습니다(이혼 후 재혼은 가능).

## 주요 비즈니스 프로세스

### Family 생성 프로세스
1. 사용자가 Family 이름과 옵션 필드(설명, 프로필 URL)를 입력합니다.
2. 시스템은 입력값을 검증하고 새로운 Family를 생성합니다.
3. 사용자는 자동으로 해당 Family의 첫 번째 멤버로 등록됩니다.
4. Family 생성 사실이 시스템에 이벤트로 발행됩니다.

### FamilyMember 추가 프로세스
1. Family 멤버가 새로운 구성원 정보를 입력합니다.
2. 시스템은 입력값을 검증하고 새로운 FamilyMember를 생성합니다.
3. 필요한 경우, 다른 FamilyMember와의 관계를 설정합니다.
4. FamilyMember 추가 사실이 시스템에 이벤트로 발행됩니다.

### 가족 관계 설정 프로세스
1. 사용자가 두 FamilyMember 간의 관계 유형(부모-자녀, 배우자)을 선택합니다.
2. 관계에 필요한 추가 정보(관계 유형, 날짜 등)를 입력합니다.
3. 시스템은 관계 설정의 유효성을 검증합니다 (예: 중복된 배우자 관계 체크).
4. 검증이 통과하면 새로운 관계가 설정됩니다.
5. 관계 설정 사실이 시스템에 이벤트로 발행됩니다.

## 인증 및 권한

### 사용자 인증
- 사용자는 폼 로그인 또는 OAuth2(Google, Kakao) 방식으로 인증할 수 있습니다.
- 사용자의 인증 상태는 세션을 통해 유지됩니다.

### 권한 관리
- Family 접근 권한은 멤버십에 기반합니다.
- 사용자는 자신이 속한 Family에만 접근할 수 있습니다.
- FamilyMember 상태에 따라 Family 내 특정 기능에 대한 접근이 제한될 수 있습니다:
  - ACTIVE: 모든 기능 사용 가능
  - SUSPENDED: 읽기만 가능
  - BANNED: 접근 불가

## 데이터 모델 관계
- User ←→ FamilyMember: 일대다 관계 (한 User가 여러 FamilyMember로 존재 가능)
- Family ←→ FamilyMember: 일대다 관계 (한 Family는 여러 FamilyMember 포함)
- FamilyMember ←→ FamilyMember: 다대다 관계 (관계 테이블을 통해 관리)
  - ParentChildRelationship: 부모-자녀 관계
  - MarriageRelationship: 배우자 관계
