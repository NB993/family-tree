# Family Tree MVP - 초대 링크 기능 개발 현황

## ✅ 완료된 작업
1. [completed] 초대 링크 도메인 모델 수정 (Family 개념 제거)
2. [completed] 초대 링크 UseCase 및 Service 수정
3. [completed] 초대 링크 코어 계층 단위 테스트 작성
4. [completed] 초대 링크 JPA Entity 및 Adapter 구현
5. [completed] 초대 링크 Adapter 테스트 작성
6. [completed] 초대 링크 Controller 및 DTO 구현
7. [completed] 초대 링크 인수 테스트 작성
8. [completed] 초대 링크 만료 체크 로직 추가 (Service에서 이미 구현됨)

## ❌ 폐기된 작업 (잘못된 설계)
- FamilyInviteResponse 관련 모든 코드 (별도 응답 관리 불필요)
- 초대 응답을 별도로 저장하는 로직

## 📋 현재 진행해야 할 작업

### 1. FamilyMember 도메인 수정
- [pending] FamilyMember의 userId를 nullable로 변경
- [pending] kakaoId 필드 추가
- [pending] 비회원도 FamilyMember가 될 수 있도록 수정

### 2. 카카오 OAuth 초대 수락 플로우
- [pending] 카카오 OAuth 콜백 엔드포인트 구현 (GET /api/invites/kakao/callback)
- [pending] 카카오 인증 코드 → Access Token 교환 서비스
- [pending] 카카오 프로필 조회 서비스
- [pending] 초대 수락 UseCase (AcceptInviteWithKakaoUseCase)
- [pending] FamilyMember 자동 생성 (userId=null, kakaoId 설정)

### 3. Security 설정
- [pending] /api/invites/kakao/callback 익명 접근 허용

### 4. 데이터베이스
- [pending] family_member 테이블 스키마 변경 (user_id nullable, kakao_id 추가)
- [pending] 마이그레이션 스크립트 작성

### 5. 테스트
- [pending] 카카오 OAuth 플로우 테스트
- [pending] 비회원 FamilyMember 생성 테스트

## 🔑 핵심 설계 변경사항
- **초대 응답 ≠ 회원가입**: 카카오 프로필 정보만 제공
- **FamilyMember는 회원/비회원 모두 가능**: userId가 null이면 비회원
- **백엔드에서 OAuth 콜백 직접 처리**: 보안 강화를 위해 인증 코드를 서버에서만 처리

## 📝 플로우 요약
1. 사용자 A가 초대 링크 생성
2. 사용자 B가 링크 클릭 → 카카오 OAuth 인증
3. 백엔드가 콜백 받아서 Access Token 교환
4. 카카오 프로필 조회
5. FamilyMember 생성 (userId=null, kakaoId=xxx)
6. 사용자 A의 관리 목록에 자동 추가