# creatorhub-server
웹툰을 창작하는 작가와 작품을 즐겨보는 독자 모두를 위한 웹툰 플랫폼의 백엔드 서버로 작품 업로드, 작품 뷰, 정산 시스템 등 핵심 기능을 제공합니다.
<br/>
<br/>

---

## 🛠️ 1. 개발 환경

- JDK 21
- Spring Boot 3.5.7
- MySQL 8.0
- Docker, Docker Compose
- Gradle 8.14.3
- IntelliJ IDEA

---

## 📦 2. 프로젝트 구조

```shell
project/
├─ src/
├─ docker-compose.yml
├─ Dockerfile
├─ application.yml
├─ mysql-data/ # MySQL 데이터 (자동 생성)
└─ mysql-init/ # 초기 테스트용 DB 생성 스크립트
```

---

## 🐳 3. Docker 기반 실행

Docker의 MySQL DB와 Spring Boot 앱(creatorhub-server)을 다음 두 가지 방식으로 실행할 수 있습니다.

### 🔹 방법 1) Docker MySQL + IDE(인텔리제이) 앱 실행

MySQL만 Docker로 실행하고, Spring Boot 앱은 IDE에서 실행하는 방식입니다.

```bash
docker compose up -d mysql
```

### 🔹 방법 2) Docker MySQL + Docker Spring Boot 앱 실행 (전체 Docker 실행)

MySQL과 Spring Boot 앱을 모두 Docker로 실행하는 방식입니다.
```bash
docker compose up -d mysql
docker compose up -d --build
```
