#!/bin/sh

export DATASOURCE_URL=$(cat /run/secrets/datasource_url)
export DATASOURCE_USERNAME=$(cat /run/secrets/datasource_user)
export DATASOURCE_PASSWORD=$(cat /run/secrets/datasource_pw)
export KAKAO_REDIRECT_URI=$(cat /run/secrets/kakao_redirect_uri)
export KAKAO_CLIENT_ID=$(cat /run/secrets/kakao_client_id)
export KAKAO_CLIENT_SECRET=$(cat /run/secrets/kakao_client_secret)
export JWT_KEY=$(cat /run/secrets/jwt_key)

# 디버깅 로그 추가
echo "===== Environment Variables ====="
echo "DATASOURCE_URL: $DATASOURCE_URL"
echo "DATASOURCE_USERNAME: $DATASOURCE_USER"
echo "DATASOURCE_PASSWORD: $DATASOURCE_PASSWORD"
echo "KAKAO_REDIRECT_URI: $KAKAO_REDIRECT_URI"
echo "================================"

exec java -jar app.jar
