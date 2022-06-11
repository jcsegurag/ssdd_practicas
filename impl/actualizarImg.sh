#! /bin/sh

docker-compose -f docker-compose-devel.yml rm -f
docker volume rm $(docker volume ls -q)
docker rmi impl_videofaces-grpc:latest
docker rmi impl_videofaces-frontend:latest
docker rmi impl_backend-rest:latest
docker rmi impl_videofaces-mysql:latest
docker-compose -f docker-compose-devel.yml up
