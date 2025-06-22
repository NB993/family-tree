#!/bin/bash

echo "Family Tree 데이터베이스 시작 중..."

# Docker Compose로 데이터베이스 시작
docker-compose up -d

echo "MySQL이 준비될 때까지 대기 중..."

# MySQL이 준비될 때까지 대기
until docker exec family-tree-mysql mysqladmin ping -h"localhost" --silent; do
    echo "MySQL이 아직 준비되지 않았습니다. 5초 후 다시 시도합니다..."
    sleep 5
done

echo "✅ MySQL이 준비되었습니다!"
echo ""
echo "데이터베이스 연결 정보:"
echo "  - MySQL: localhost:3306"
echo "  - Database: family_tree"
echo "  - Username: family_tree_user"
echo "  - Password: family_tree_password"
echo ""
echo "🏃 애플리케이션을 실행하려면:"
echo "  ./gradlew bootRun"
echo ""
echo "이제 local 프로필이 기본적으로 MySQL과 Redis를 사용합니다!"
