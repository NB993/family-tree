# 역할별 AI 프로세스

## 역할별 작업 순서
- 제품 기획자 AI → 기술 PM AI → QA AI -> 아키텍트/개발자 AI 
- 제품 기획자 AI가 기획을 시작하기 전에 기능을 짧게 요약하여 케밥 케이스로 파일명을 만든다
- `be/instructions/workflow` 하위의 각 역할별 폴더에 기획자 AI가 만든 파일명으로 md 파일을 만들어 작업을 진행한다.
- 역할별 폴더명
  - 제품 기획자 AI: 1_plan
  - 기술 PM AI: 2_pm
  - QA AI: 3_qa
  - 아키텍트/개발자 AI: 4_feat

## 역할별 필수 문서 읽기 절차

### 제품 기획자 AI
작업 시작 전 다음 절차를 따라야 한다:
- 기존 기획 문서들 검토 (`1_plan/` 폴더 내 관련 문서)
- 기능명을 케밥 케이스로 생성
- `1_plan/[기능명].md` 작성

### 기술 PM AI  
작업 시작 전 다음 절차를 따라야 한다:
- `1_plan/[기능명].md` 문서 읽기
- 기존 기술 문서들 검토 (`2_pm/` 폴더 내 관련 문서)
- `2_pm/[기능명].md` 작성

### QA AI
작업 시작 전 다음 절차를 따라야 한다:
- `1_plan/[기능명].md` 문서 읽기
- `2_pm/[기능명].md` 문서 읽기  
- 기존 QA 문서들 검토 (`3_qa/` 폴더 내 관련 문서)
- `3_qa/[기능명].md` 작성

### 아키텍트/개발자 AI
작업 시작 전 **반드시** 다음 절차를 따라야 한다:

1. **기능 관련 문서 읽기**
   - `4_feat/[기능명].md` - 해당 기능의 설계 문서
   - 필요시 `1_plan/`, `2_pm/`, `3_qa/` 문서도 참조

2. **개발 가이드라인 문서 읽기**
   - `be/instructions/index.md` (필수)
   - `be/instructions/testing-guidelines.md` (필수)
   - `be/instructions/naming-conventions.md` (필수)
   - `be/instructions/architecture-overview.md` (필수)
   - `be/instructions/development-process.md` (필수)
   - `be/instructions/coding-standards.md` (필수)
   - `be/instructions/entity-mapping.md` (필요시)

3. **절차 확인**
   - find_files로 관련 문서 목록 확인
   - get_file_text로 각 문서 순서대로 읽기
   - 읽을 때마다 "✅ 읽음: [파일명]" 표시
   - 핵심 내용 요약 후 개발 계획 수립
   - 사용자 승인 후 개발 시작

**이 절차를 건너뛰고 바로 개발하는 것을 금지한다.**

## 역할별 책임
- 제품 기획자 AI
  - 책임: 사용자 관점에서 필요한 기능 정의, 비즈니스 가치 평가
  - 산출물
    - 기능 개요 및 목적
    - 사용자 스토리 및 시나리오
    - 와이어프레임/프로토타입
    - 주요 기능 우선순위

- 기술 PM AI
  - 책임: 비즈니스 요구사항을 기술적 요구사항으로 번역
  - 산출물
    - 세부 기능 명세서
    - 데이터 모델 개념 설계
    - 시스템 간 상호작용 다이어그램
    - 구현 로드맵 및 마일스톤

- QA AI
  - 책임: 품질 검증 및 테스트 전략 수립
  - 산출물
    - 테스트 케이스 및 시나리오
    - 예상 버그 및 엣지 케이스
    - 성능 테스트 지침
    - 사용자 수용 테스트 계획

- 아키텍트/개발자 AI
  - 책임: 기술적 요구사항을 구체적인 기술 설계로 변환/기술 설계를 구현 가능한 코드로 변환
  - 산출물
    - md 파일
      - 도메인 모델 상세 설계
        - 도메인 모델의 행위 또는 동작(=메서드)에 대한 정의
        - 도메인 모델 관계도 작성
      - 기술적 이슈 및 해결방안 제시
        - md 파일에 작성
      - 배포 및 통합 지침 제시
        - md 파일에 작성
      - 코드 구현 이후 구현 내용 요약
    - 코드 구현
      - md 파일에는 구현 코드를 작성할 필요 없으며, IDE를 이용하여 AI가 직접 코드를 작성한다
      - 코드 의도를 알리고 싶을 땐 코드 내부에 주석을 활용
      - 백엔드 개발 규칙은 `be/instructions/index.md` 참고

## 개발 단계별 진행 원칙 (중요)

### 단계별 개발 강제 규칙
아키텍트/개발자 AI는 **반드시** 아래 단계를 순서대로 진행해야 한다:

1. **1단계: 코어 계층 개발**
   - UseCase 인터페이스 정의
   - Command/Query 객체 생성
   - Service 비즈니스 로직 구현
   - 단위 테스트 작성 및 검증

2. **2단계: 인프라 계층 개발**  
   - Port 인터페이스 확장
   - Adapter 구현체 작성
   - JPA Repository 확장
   - 인프라 테스트 작성 및 검증

3. **3단계: 프레젠테이션 계층 개발**
   - Controller 구현
   - DTO 변환 로직
   - API 테스트 작성 및 검증

### 버티컬 슬라이스 금지 (엄격 준수)
- **금지사항**: 한 번에 모든 계층(코어+인프라+프레젠테이션)을 동시 구현하는 것
- **이유**: 
  - 토큰 사용량 초과로 작업 중단 방지
  - 단계별 검증 가능
  - 대화 맥락 유지 및 관리 용이성
- **예외 없음**: AI가 자연스럽게 선호하는 방식이지만 관리상 단계별 진행 필수
- **강제 규칙**: 각 단계 완료 후 반드시 사용자 승인을 받고 다음 단계 진행

### 각 단계별 완료 조건
- 해당 단계의 모든 테스트가 통과해야 함
- 기존 테스트에 영향을 주지 않아야 함
- 사용자 승인 후 다음 단계 진행
