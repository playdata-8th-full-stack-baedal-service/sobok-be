# 🥬 소복 - 유통기한 임박 식재료 실시간 배달 중개 플랫폼

**소복**은 유통기한 임박 식재료를 소비자에게 실시간으로 중개·배달해주는 서비스입니다.  
사용자는 냉장고처럼 자동 관리되는 식재료 목록을 기반으로 주문하고,  
공급자는 실시간 재고를 등록하여 소진할 수 있습니다.

---

## 🚀 프로젝트 개요

- **프로젝트 기간**: 2025.XX ~ 2025.XX
- **기술 스택**: Spring Boot, Spring Cloud, MySQL, Redis, Docker, Kubernetes, ArgoCD, AWS
- **아키텍처**: MSA (Microservices Architecture)

---

## 📦 마이크로서비스 구조

| 서비스 이름                     | 역할                 |
|----------------------------|--------------------|
| `auth-service`             | 로그인/회원가입, JWT 인증   |
| `user-service`             | 사용자 정보, 선호 재료      |
| `cook-service`             | 레시피 기반 식재료 추천      |
| `payment-service`          | 주문, 결제 처리          |
| `delivery-service`         | 배달 상태 추적           |
| `shop-service`             | 공급자 등록, 재고 관리      |
| `post-service`             | 유저 레시피 공유 게시판      |
| `gateway-service`          | API Gateway + 인증 필터 |
| `config-server`            | 공통 설정 관리           |
| (제외됨) `discovery-service` | 내부 개발용             |

---

## ⚙️ CI/CD 파이프라인

- **GitHub Actions** → 서비스 변경 감지 → Docker 이미지 빌드
- **AWS ECR** → 빌드된 이미지 저장
- **ArgoCD + Helm** → Kubernetes 클러스터 자동 배포

> 변경된 서비스만 빌드해서 ECR에 올리는 효율적인 전략 적용됨

---

## 🔐 GitHub Secret 관리 항목

| 이름 | 용도 |
|------|------|
| `AWS_ACCESS_KEY_ID` | ECR 접근용 |
| `AWS_SECRET_ACCESS_KEY` | ECR 접근용 |
| `ECR_REGISTRY` | ECR 주소 (`123456.dkr.ecr.ap-northeast-2.amazonaws.com`) |
| `GIT_USERNAME` | Config Server가 Git repo 접근 시 사용 |
| `GIT_PASSWORD` | GitHub Token (config repo 접근용) |

---

## 🛠️ 실행 방법 (로컬 개발 기준)

```bash
# 공통 모듈 빌드
./gradlew clean build

# 개별 서비스 Docker 빌드
cd auth-service
docker build -t auth-service .

# docker-compose 또는 K8s로 실행
