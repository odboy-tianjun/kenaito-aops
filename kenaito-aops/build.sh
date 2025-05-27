#!/bin/bash
APP_NAME=kenaito-aops
APP_ENV=dev
JAR_PATH=$(awk -F= '/build.output=/ {print $2}' $APP_NAME.release)
IMAGE_URL=$(awk -F= '/docker.repo=/ {print $2}' $APP_NAME.release)
IMAGE_TAG="$IMAGE_URL:$APP_ENV"_"$(date "+%Y%m%d%H%M%S%3N")"
#mvn clean package -DskipTests
#cp "$(pwd)/$JAR_PATH" .
#tar -czvf $APP_NAME.tar.gz $APP_NAME.jar && rm -f $APP_NAME.jar
docker build -f Dockerfile.$APP_ENV -t "$IMAGE_TAG" . && rm -f $APP_NAME.tar.gz
