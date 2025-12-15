# creatorhub-server
웹툰을 창작하는 작가와 작품을 즐겨보는 독자 모두를 위한 웹툰 플랫폼의 백엔드 서버로 작품 업로드, 작품 뷰, 정산 시스템 등 핵심 기능을 제공합니다.
<br/>
<br/>

---

## 🛠️ 1. 개발 환경

- JDK 21
- Spring Boot 3.5.7
- MySQL 8.0
- Redis 7.2-alpine
- Docker
- Gradle 8.14.3
- IntelliJ IDEA

---

## 📦 2. 프로젝트 구조

```shell
creatorhub-server/
├─ src/
│  └─ main/
│     └─ resources/
│        ├─ application.yml
│        └─ application-test.yml # 테스트 코드용 
├─ docker-compose.yml
├─ Dockerfile
├─ mysql-data/       # MySQL 데이터 (자동 생성)
└─ mysql-init/       # 초기 테스트용 DB 생성 스크립트
```

---

## 🐳 3. Docker 기반 실행

MySQL DB, Redis, Spring Boot 앱(creatorhub-server)을 Docker Compose를 통해 각각 실행할 수 있습니다. 환경변수는 creatorhub-prod.env 파일을 사용합니다.
<br/>

만약 Spring Boot 앱을 IDE에서 실행한다면 MySQL, Redis만 Docker로 실행하면 됩니다.


### 🔹 MySQL, Redis, Spring Boot 앱 실행
- 모든 민감한 설정 값은 실행 시 환경변수로 주입합니다.  
-  Windows 환경에서는 WSL 사용을 권장합니다.

```bash
docker compose up -d mysql
```
```bash
docker compose up -d redis
```
```bash
MYSQL_ROOT_PASSWORD="password" \
SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/creatorhub?serverTimezone=Asia/Seoul" \
SPRING_DATASOURCE_PASSWORD="password" \
JWT_ACCESS_SECRET="your-access-secret" \
JWT_REFRESH_SECRET="your-refresh-secret" \
docker compose up -d app --build
```