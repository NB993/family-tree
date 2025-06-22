# Family Tree 데이터베이스 설정

## 🐳 Docker를 사용한 로컬 개발 환경

### 빠른 시작

```bash
# 1. 데이터베이스 시작
./scripts/start-db.sh

# 2. 애플리케이션 실행 (기본값: local 프로필)
./gradlew bootRun
```

### 데이터베이스 관리 명령어

```bash
# 데이터베이스 시작
./scripts/start-db.sh

# 데이터베이스 중지 (데이터 보존)
./scripts/stop-db.sh

# 데이터베이스 완전 초기화 (모든 데이터 삭제)
./scripts/reset-db.sh
```

### 수동 Docker 명령어

```bash
# 서비스 시작
docker-compose up -d

# 서비스 중지
docker-compose down

# 모든 데이터 삭제하고 중지
docker-compose down -v

# 로그 확인
docker-compose logs -f mysql
docker-compose logs -f redis
```

## 📊 데이터베이스 연결 정보

### MySQL
- **Host**: localhost
- **Port**: 3306
- **Database**: family_tree
- **Username**: family_tree_user
- **Password**: family_tree_password
- **Root Password**: rootpassword


## 🔧 설정 프로필

### application-local.yml (MySQL)
- 기본 개발 환경
- 데이터 영속성 보장
- Docker 컨테이너 필요

### application-test.yml (H2 인메모리)
- 테스트 전용 환경
- 격리된 테스트 실행
- Docker 불필요

## 🛠️ IDE 설정

### IntelliJ IDEA
1. Run Configuration에서 Active profiles는 기본값(local) 사용
2. 프로필 변경이 필요한 경우만 지정

### VS Code
1. 기본적으로 별도 설정 불필요 (local 프로필 자동 사용)

## 🗄️ 데이터베이스 스키마

JPA가 자동으로 테이블을 생성합니다 (`ddl-auto: create-drop`).

### 주요 테이블
- `users`: 사용자 정보
- `families`: 가족 정보
- `family_members`: 가족 구성원
- `family_member_relationships`: 가족 관계
- `family_join_requests`: 가족 가입 요청
- `announcements`: 공지사항
- `refresh_tokens`: JWT 리프레시 토큰

## 🔍 데이터베이스 접속

### MySQL 클라이언트로 직접 접속
```bash
mysql -h localhost -P 3306 -u family_tree_user -p family_tree
# Password: family_tree_password
```

### GUI 도구 사용
- **MySQL Workbench**
- **DBeaver**
- **DataGrip**

연결 정보는 위의 데이터베이스 연결 정보를 참고하세요.

## 🎯 초기 데이터

### 자동 생성되는 테스트 계정
- `test@example.com` / `password123`
- `kim@example.com` / `password123`
- `park@example.com` / `password123`
- `lee@example.com` / `password123`

### 데이터 영속성
- Docker 볼륨 사용으로 **컨테이너 재시작 후에도 데이터 보존**
- 초기 데이터는 **최초 실행 시에만 생성**됨
- 초기 데이터 재생성: `./scripts/reset-db.sh` 실행

## 🚨 주의사항

1. **개발 환경에서만 사용**: 이 설정은 로컬 개발용입니다.
2. **데이터 백업**: 중요한 데이터는 정기적으로 백업하세요.
3. **포트 충돌**: 3306(MySQL) 포트가 이미 사용 중이면 docker-compose.yml에서 변경하세요.
4. **데이터 초기화**: 깨끗한 상태로 다시 시작하려면 `./scripts/reset-db.sh` 사용

## 🔧 문제 해결

### Docker 관련
```bash
# Docker 상태 확인
docker ps

# 컨테이너 로그 확인
docker logs family-tree-mysql

# 컨테이너 재시작
docker restart family-tree-mysql
```

### MySQL 연결 문제
1. MySQL 컨테이너가 완전히 시작될 때까지 기다리세요 (30초~1분)
2. 방화벽이 3306 포트를 차단하지 않는지 확인하세요
3. 다른 MySQL 인스턴스가 3306 포트를 사용하고 있지 않은지 확인하세요