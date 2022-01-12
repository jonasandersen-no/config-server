#!/usr/bin/env bash


echo "Pulling from GitHub"
git pull
echo "Building with Maven"
mvn clean package -DskipTests
echo "Deploying with Docker"
docker-compose up -d
