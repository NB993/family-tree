#!/bin/bash

echo "🛑 Family Tree 데이터베이스 중지 중..."

# Docker Compose로 서비스 중지
docker-compose down

echo "✅ 데이터베이스가 중지되었습니다."
echo ""
echo "💾 데이터는 보존됩니다. 완전히 삭제하려면:"
echo "  docker-compose down -v"