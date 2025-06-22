#!/bin/bash

echo "️ Family Tree 데이터베이스 초기화 중..."

# 기존 컨테이너와 볼륨 완전 제거
docker-compose down -v

echo " 모든 데이터가 삭제되었습니다."
echo ""
echo " 깨끗한 데이터베이스로 다시 시작하려면:"
echo "  ./scripts/start-db.sh"
