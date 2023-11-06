# coupon-system
해당 문서 출처는 [실습으로 배우는 선착순 이벤트 시스템](https://www.inflearn.com/course/%EC%84%A0%EC%B0%A9%EC%88%9C-%EC%9D%B4%EB%B2%A4%ED%8A%B8-%EC%8B%9C%EC%8A%A4%ED%85%9C-%EC%8B%A4%EC%8A%B5) 기반으로 작성되었습니다. 

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
docker exec -it CONTAINER_ID redis-cli
```

## Redis 문제점 해결하기
레이스 컨디션은 두 개 이상의 스레드에서 공유 데이터의 액세스를 할 때 발생하는 문제이므로 싱글 스레드로 작업한다면 레이스 컨디션은 일어나지 않을 것이다. 하지만 쿠폰 발급 로직 전체를 싱글 스레드로 작업을 하게 된다면 성능이 좋지 않을 것이다. 레이스 컨디션을 해결하기 위해서 Java에서 지원하는 Synchronized를 생각해볼 수 있지만 서버가 여러 ㅐ가 된다면 레이스 컨디션이 다시 발생함로 적절하지 않다. 또 다른 방법으로는 MySQL, Redis를 활용한 락을 구현해서 해결할 수도 있을 것이다. 하지만 쿠폰 개수에 대한 정합성인데 락을 활용하여 구현한다면 발급된 쿠폰 개수를 가져오는 것부터 쿠폰을 생성할 때까지 락을 걸어야 한다. 그렇게 된다면 락을 거는 구간이 길어져서 성능에 불이익이 있을 수 있다. 이 프로젝트의 핵심은 쿠폰 개수이므로 쿠폰 개수에 대한 정합성만 관리하면 될 것이라고 생각한다. Redis에는 incr이라는 명령어가 존재하고 이 명령어는 키에 대한 value를 1씩 증가시키는 명령어다. Redis는 싱글스레드 기반으로 동작하여 레이스 컨디션을 해결할 수 있을 뿐만 아니라 INCR 명령어는 성능도 굉장히 빠른 명령어이다. 이 명령어를 사용하여 발급된 쿠폰 개수를 제외한다면 성능도 빠르고 데이터 정합성도 지킬 수 있을 것이다.
