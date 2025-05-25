#!/bin/sh

export DATASOURCE_URL=$(cat /run/secrets/datasource_url)
export DATASOURCE_USERNAME=$(cat /run/secrets/datasource_user)
export DATASOURCE_PASSWORD=$(cat /run/secrets/datasource_pw)
export KAKAO_REDIRECT_URI=$(cat /run/secrets/kakao_redirect_uri)
export KAKAO_CLIENT_ID=$(cat /run/secrets/kakao_client_id)
export KAKAO_CLIENT_SECRET=$(cat /run/secrets/kakao_client_secret)
export JWT_KEY=$(cat /run/secrets/jwt_key)

exec java -Duser.timezone=Asia/Seoul -jar app.jar
