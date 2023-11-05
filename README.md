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

## 쿠폰 Project 문제점
레이스 컨디션 문제 발생. 레이스 컨디션이란 두 개 이상의 쓰레드가 공유 데이터에 access를 하고 동시에 작업을 하려할때 발생하는 문제이다.

![image](https://github.com/haeyonghahn/coupon-system/assets/31242766/fd2a73b6-55e0-4881-9fe9-9c6cd16a4a45)

Thread1이 생성된 쿠폰의 개수를 가져가고 아직 100개가 아니므로 쿠폰을 생성하고 Thread2가 생성된 쿠폰의 개수를 가져갔을 때 생성된 쿠폰의 개수가 100개이므로 쿠폰을 생성하지 않는 것을 예상했을 것이다. 하지만 실제로 Thread1이 생성된 쿠폰의 개수를 가져가고 Thread1이 쿠폰을 생성하기 전에 Thread2가 가져가는 생성된 쿠폰의 개수도 99개이다. 그래서 Thread2도 쿠폰을 생성하게 되고 결과적으로 100개가 넘는 쿠폰이 생성되게 되는 것이다. 이렇게 2개 이상의 Thread가 공유자원에 액세스를 하고 작업을 하려고 할 때 발생되는 문제점을 `레이스 컨디션`이라고 한다. 레이스 컨디션을 해결하는 방법으로는 여러가지가 있겠지만 Redis를 활용하여 해결해보자.

## Redis 작업환경 세팅
```
docker pull redis
docker run --name myredis -d -p 6379:6379 redis
```
