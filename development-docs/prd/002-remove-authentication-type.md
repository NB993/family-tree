# PRD-002: AuthenticationType 필드 제거

## 문서 정보
- **작성일**: 2025-01-05
- **상태**: ✅ 완료
- **완료일**: 2025-01-05
- **커밋**: `8850931 refactor PRD-002 AuthenticationType 필드 제거`

---

## 배경

`authentication_type` 필드는 초기에 ID/PW 로그인과 OAuth2 로그인을 병행하려고 만들었으나, 현재는 OAuth2만 사용하고 있어 불필요한 필드가 되었습니다.

### 현재 상태
- DB에 `authentication_type` 컬럼 존재 (항상 OAUTH2)
- `AuthenticationType` enum: FORM_LOGIN, OAUTH2, JWT, NONE
- 실제 비즈니스 로직에서 사용하는 곳 없음

## 변경 사항

### 1. AuthenticationType enum 삭제
- `be/src/main/java/io/jhchoe/familytree/common/auth/domain/AuthenticationType.java` 삭제

### 2. Domain 계층
| 파일 | 변경 내용 |
|------|----------|
| `User.java` | `authenticationType` 필드 제거, `isLoginable()` 메서드 추가 |
| `FTUser.java` | `authType` 필드 및 `getAuthType()` 메서드 제거 |

### 3. Infrastructure 계층
| 파일 | 변경 내용 |
|------|----------|
| `UserJpaEntity.java` | `authenticationType` 필드 제거 |

### 4. DTO/Service 계층
| 파일 | 변경 내용 |
|------|----------|
| `UserResponse.java` | `authType` 필드 제거 |
| `OAuth2UserServiceImpl.java` | AuthenticationType 설정 로직 제거 |

### 5. Config
| 파일 | 변경 내용 |
|------|----------|
| `LocalDataInitializer.java` | AuthenticationType 파라미터 제거 |

### 6. 테스트
| 파일 | 변경 내용 |
|------|----------|
| `AuthenticationTypeTest.java` | 삭제 |
| `UserTest.java` | authenticationType 관련 테스트 수정 |
| `UserFixture.java` | 파라미터 제거 |
| `UserControllerDocsTest.java` | authType 필드 문서 제거 |

### 7. DB 마이그레이션
```sql
ALTER TABLE users DROP COLUMN authentication_type;
```

## 수동 등록 사용자 구분 방법

**기존:**
```java
user.getAuthenticationType() == AuthenticationType.NONE
```

**변경:**
```java
// oauth2_provider가 NULL이면 수동 등록 사용자
user.isLoginable()  // return oAuth2Provider != null
```

## 작업 순서

1. Domain 수정 (User, FTUser)
2. Infrastructure 수정 (UserJpaEntity)
3. DTO/Service 수정
4. 테스트 수정
5. AuthenticationType.java 삭제
6. DB 컬럼 제거
7. 전체 테스트 실행
