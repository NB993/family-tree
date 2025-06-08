# JWT 인증 설계 의사결정 문서

## 📋 문서 정보
- **작성일**: 2025-06-08
- **작성자**: 개발자 AI + 사용자
- **목적**: JWT 인증 구현 과정에서의 핵심 설계 의사결정과 그 이유를 기록

---

## 🤔 1. JWT 토큰에 Role 정보 포함 이슈

### 1.1 발견된 문제
```java
// 기존 코드 - role 파라미터가 사용되지 않음
private FTUser createJwtFTUser(
    final Long userId,
    final String name,
    final String email,
    final String role  // ← 추출했지만 사용하지 않음!
) {
    return FTUser.ofFormLoginUser(
        userId,
        name != null ? name : "JWT User",
        email,
        "", 
        email
    );
    // role 정보가 Spring Security 권한으로 반영되지 않음!
}
```

### 1.2 문제의 영향
- **권한 기반 접근 제어 실패**: `hasRole("ADMIN")` 등이 작동하지 않음
- **JWT 토큰의 role 정보