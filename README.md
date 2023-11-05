# coupon-system
## 작업환경 세팅
### docker 설치
```
brew install docker
brew link docker

docker version
```

### docker mysql 실행 명령어
```
docker pull mysql
docker run -d -p 13306:3306 -e MYSQL_ALLOW_EMPTY_PASSWORD=true --name mysql mysql
docker ps
docker exec -it mysql bash
```

### mysql 명령어
```
mysql -u root -p
create database coupon_example;
use coupon_example;
```
