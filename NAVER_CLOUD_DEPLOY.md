# Naver Cloud Platform 배포 가이드

## 1. Naver Cloud 준비

### 1.1 서버 (VM) 생성
1. [Naver Cloud Console](https://console.ncloud.com) 접속
2. **Server** → **Server** → `서버 생성`
3. 기본 사양: **vCPU 2EA, Memory 4GB** (`vCPU 2EA, Mem 4GB, Disk 50GB`)
4. OS: **Linux (CentOS 7.8 - 64bit)** 또는 **Ubuntu 22.04**
5. ACG: `22(SSH)`, `80(HTTP)`, `443(HTTPS)`, `8080(Spring Boot)`, `5432(PostgreSQL)` 오픈

### 1.2 Public IP 할당
- 생성한 서버에 **Public IP** 할당 (고정 IP)

### 1.3 Cloud DB for PostgreSQL 생성
1. **Cloud DB for PostgreSQL** → `DB Server 생성`
2. DB: `seoul_toilet`
3. Username: `postgres`
4. 접속 정보를 application.yml에 설정

---

## 2. 백엔드 (Spring Boot) 배포

### 2.1 서버 접속
```bash
ssh -i your-key.pem root@<SERVER_PUBLIC_IP>
```

### 2.2 필수 패키지 설치
```bash
# Java 17
yum install -y java-17-openjdk-devel

# PostgreSQL 클라이언트 (DB 마이그레이션용)
yum install -y postgresql

# 확인
java -version
```

### 2.3 애플리케이션 빌드 & 업로드
로컬에서:
```bash
cd backend
mvn clean package -DskipTests
```
생성된 `target/seoul-toilet-backend-1.0.0.jar`를 서버로 SCP 전송:
```bash
scp -i your-key.pem target/seoul-toilet-backend-1.0.0.jar root@<SERVER_IP>:/app/
```

### 2.4 환경 변수 설정
```bash
mkdir -p /app
cat > /app/application-prod.yml << 'EOF'
spring:
  datasource:
    url: jdbc:postgresql://<DB_HOST>:5432/seoul_toilet
    username: postgres
    password: <DB_PASSWORD>
EOF
```

### 2.5 서비스 등록 (systemd)
```bash
cat > /etc/systemd/system/seoul-toilet.service << 'EOF'
[Unit]
Description=Seoul Toilet Backend
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/app
ExecStart=/usr/bin/java -jar /app/seoul-toilet-backend-1.0.0.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable seoul-toilet
systemctl start seoul-toilet
systemctl status seoul-toilet
```

### 2.6 Nginx 리버스 프록시 설정
```bash
yum install -y nginx

cat > /etc/nginx/conf.d/seoul-toilet.conf << 'EOF'
server {
    listen 80;
    server_name _;

    # Frontend (정적 파일)
    root /usr/share/nginx/html;
    index index.html;

    # API 프록시
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # SPA 라우팅
    location / {
        try_files $uri /index.html;
    }
}
EOF

systemctl enable nginx
systemctl start nginx
```

---

## 3. 프론트엔드 (Vue.js) 배포

### 3.1 로컬에서 빌드
```bash
cd frontend

# .env 파일 생성
echo "VITE_KAKAO_MAP_API_KEY=your_kakao_api_key" > .env

# 빌드
npm install
npm run build
```

### 3.2 빌드 파일 업로드
```bash
scp -i your-key.pem -r dist/* root@<SERVER_IP>:/usr/share/nginx/html/
```

---

## 4. 전체 구조

```
[사용자] → Naver Cloud Public IP:80
              ↓
          Nginx (80)
           /    \
    /api/*    /* (정적 파일)
      ↓
  Spring Boot :8080
      ↓
  Cloud DB for PostgreSQL
```

---

## 5. Object Storage (선택사항)

리뷰 사진 업로드를 위해 Naver Cloud **Object Storage** 사용 가능:
1. Console → **Object Storage** → 버킷 생성 (public 읽기 권한)
2. Spring Boot에 `AWS SDK` 의존성 추가 후 presigned URL 발급
3. Vue.js에서 presigned URL로 직접 업로드

---

## 6. 참고 링크
- [Naver Cloud Server 가이드](https://guide.ncloud-docs.com/docs/compute-compute-1-1)
- [Cloud DB for PostgreSQL 가이드](https://guide.ncloud-docs.com/docs/database-database-3-1)
- [Object Storage 가이드](https://guide.ncloud-docs.com/docs/objectstorage-objectstorage-1-1)
