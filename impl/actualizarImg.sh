#! /bin/sh

docker-compose -f docker-compose-devel.yml rm -f
docker rmi impl_videofaces-frontend:latest
docker rmi impl_backend-rest:latest
docker-compose -f docker-compose-devel.yml up
