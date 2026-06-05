# PostgreSQL Docker 개발 환경 세팅 가이드

Mac / Windows 환경에서 동일한 PostgreSQL DB 환경을 사용하기 위한 Docker 기반 설정 가이드입니다.

---

## 1. 목표

이 문서는 Spring Boot 프로젝트에서 PostgreSQL을 Docker로 실행하고, 로컬 개발 환경에서 동일하게 연결하기 위한 설정을 정리합니다.

```text
Mac 개발자     → docker compose up -d
Windows 개발자 → docker compose up -d

동일한 PostgreSQL 버전
동일한 포트
동일한 DB명
동일한 계정
동일한 비밀번호
```

---

## 2. Docker Desktop 설치

### Mac

Docker Desktop for Mac을 설치한 뒤 실행합니다.

설치 확인:

```bash
docker --version
docker compose version
```

### Windows

Docker Desktop for Windows를 설치합니다.

설치 중 WSL2 관련 안내가 나오면 안내에 따라 설치합니다.

설치 확인:

```bash
docker --version
docker compose version
```

정상 설치되었다면 Docker 버전과 Docker Compose 버전이 출력됩니다.

---

## 3. 프로젝트 루트에 `docker-compose.yml` 생성

프로젝트 루트 구조 예시:

```text
hrbank/
 ├─ src/
 ├─ build.gradle
 ├─ settings.gradle
 └─ docker-compose.yml
```

`docker-compose.yml` 파일 생성:

```yaml
services:
  postgres:
    image: postgres:16
    container_name: hrbank-postgres
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: hrbank
      POSTGRES_USER: hrbank
      POSTGRES_PASSWORD: hrbank1234
      TZ: Asia/Seoul
    volumes:
      - hrbank-postgres-data:/var/lib/postgresql/data

volumes:
  hrbank-postgres-data:
```

---

## 4. PostgreSQL 컨테이너 실행

프로젝트 루트에서 실행합니다.

```bash
docker compose up -d
```

실행 중인 컨테이너 확인:

```bash
docker ps
```

`hrbank-postgres` 컨테이너가 보이면 정상입니다.

로그 확인:

```bash
docker logs hrbank-postgres
```

---

## 5. PostgreSQL 접속 테스트

컨테이너 내부의 PostgreSQL에 접속합니다.

```bash
docker exec -it hrbank-postgres psql -U hrbank -d hrbank
```

접속되면 아래와 같은 프롬프트가 표시됩니다.

```text
hrbank=#
```

간단한 확인 쿼리:

```sql
select version();
```

종료:

```sql
\q
```

---

## 6. Spring Boot 설정 추가

`src/main/resources/application-local.yml` 파일을 생성합니다.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hrbank
    username: hrbank
    password: hrbank1234

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

---

## 7. Gradle 의존성 추가

`build.gradle`에 PostgreSQL JDBC 드라이버를 추가합니다.

```gradle
dependencies {
    runtimeOnly 'org.postgresql:postgresql'
}
```

JPA를 사용한다면 아래 의존성도 필요합니다.

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'org.postgresql:postgresql'
}
```

---

## 8. local profile로 Spring Boot 실행

### IntelliJ에서 실행

```text
Run Configuration
→ Active profiles
→ local
```

### Mac / Linux 터미널

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Windows PowerShell

```powershell
.\gradlew bootRun --args='--spring.profiles.active=local'
```

---

## 9. 자주 쓰는 Docker 명령어

### 컨테이너 실행

```bash
docker compose up -d
```

### 컨테이너 중지

```bash
docker compose down
```

### 컨테이너 재시작

```bash
docker compose restart
```

### 실행 중인 컨테이너 확인

```bash
docker ps
```

### 전체 컨테이너 확인

```bash
docker ps -a
```

### PostgreSQL 로그 확인

```bash
docker logs hrbank-postgres
```

### PostgreSQL 접속

```bash
docker exec -it hrbank-postgres psql -U hrbank -d hrbank
```

### 데이터까지 완전히 삭제

```bash
docker compose down -v
```

주의: `docker compose down -v`를 실행하면 PostgreSQL 데이터 볼륨까지 삭제됩니다.

---

## 10. 데이터 유지 방식

현재 설정에서는 Docker named volume을 사용합니다.

```yaml
volumes:
  - hrbank-postgres-data:/var/lib/postgresql/data
```

이 방식은 Mac / Windows 경로 차이를 줄일 수 있어 로컬 개발 환경에서 사용하기 좋습니다.

반대로 아래처럼 로컬 폴더를 직접 마운트하는 방식은 OS별 경로 문제나 권한 문제가 생길 수 있습니다.

```yaml
volumes:
  - ./postgres-data:/var/lib/postgresql/data
```

초기 개발 단계에서는 named volume 방식을 추천합니다.

---

## 11. 최종 실행 순서 요약

```text
1. Docker Desktop 설치
2. docker --version 확인
3. docker compose version 확인
4. 프로젝트 루트에 docker-compose.yml 생성
5. docker compose up -d 실행
6. docker ps로 컨테이너 실행 확인
7. docker exec로 PostgreSQL 접속 테스트
8. application-local.yml 작성
9. build.gradle에 PostgreSQL 드라이버 추가
10. local profile로 Spring Boot 실행
```

---

## 12. 최종 권장 구조

```text
로컬 개발 DB
→ Docker Compose PostgreSQL

Spring Boot 실행
→ application-local.yml

자동 통합 테스트
→ 추후 Testcontainers 고려
```

처음에는 Docker Compose로 PostgreSQL을 띄워서 개발하고, 나중에 Repository 테스트나 Querydsl 테스트를 자동화할 때 Testcontainers를 추가하는 흐름이 좋습니다.
