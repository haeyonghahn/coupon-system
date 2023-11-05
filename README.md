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

## 요구사항 정의
```
선착순 100명에게 할인쿠폰을 제공하는 이벤트를 진행하고자 한다.

이 이벤트는 아래와 같은 조건을 만족하여야 한다.
- 선착순 100명에게만 지급되어야한다.
- 101개 이상이 지급되면 안된다.
- 순간적으로 몰리는 트래픽을 버틸 수 있어야합니다.
```
