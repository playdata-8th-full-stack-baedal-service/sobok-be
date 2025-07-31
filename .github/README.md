# 🚀 Sobok-be CI/CD 가이드

## 개요
이 프로젝트는 GitHub Actions를 사용하여 **변경된 서비스만 감지**하고 **자동으로 빌드 & ECR 푸시**하는 파이프라인을 구현했습니다.

## 🎯 주요 기능
- ✅ **스마트 변경 감지**: 수정된 서비스만 빌드
- ✅ **병렬 빌드**: Matrix 전략으로 동시 빌드  
- ✅ **ECR 자동 푸시**: AWS ECR에 컨테이너 이미지 등록
- ✅ **캐싱 최적화**: Docker layer 캐싱으로 빌드 시간 단축
- ✅ **멀티 플랫폼**: linux/amd64 지원

## 📦 대상 서비스 (총 10개)
```
api-service      auth-service     config-server
cook-service     delivery-service gateway-service  
payment-service  post-service     shop-service
user-service
```

## ⚙️ 설정 방법

### 1. ECR 리포지토리 생성
```bash
# 스크립트 실행 권한 부여
chmod +x .github/scripts/create-ecr-repos.sh

# ECR 리포지토리 생성 (AWS CLI 필요)
./.github/scripts/create-ecr-repos.sh ap-northeast-2
```

### 2. GitHub Secrets 설정
**GitHub Repository Settings > Secrets and Variables > Actions**에서 다음 설정:

| Secret Name | 설명 | 예시 |
|-------------|------|------|
| `AWS_ACCESS_KEY_ID` | AWS Access Key | `AKIA...` |
| `AWS_SECRET_ACCESS_KEY` | AWS Secret Key | `wJalr...` |
| `ECR_REGISTRY` | ECR 레지스트리 URL | `123456789012.dkr.ecr.ap-northeast-2.amazonaws.com` |

### 3. IAM 권한 설정
AWS IAM 사용자에게 다음 권한 필요:
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ecr:GetAuthorizationToken",
                "ecr:BatchCheckLayerAvailability",
                "ecr:GetDownloadUrlForLayer",
                "ecr:BatchGetImage",
                "ecr:InitiateLayerUpload",
                "ecr:UploadLayerPart",
                "ecr:CompleteLayerUpload",
                "ecr:PutImage"
            ],
            "Resource": "*"
        }
    ]
}
```

## 🔄 워크플로우 트리거

### 자동 트리거
- `main` 브랜치에 push
- `develop` 브랜치에 push  
- `main` 브랜치로 PR 생성

### 변경 감지 로직
```bash
# PR인 경우
git diff --name-only base_sha current_sha

# Push인 경우  
git diff --name-only before_sha current_sha
```

## 📊 빌드 결과 확인

### GitHub Actions 페이지에서 확인 가능:
- ✅ **Build Summary**: 어떤 서비스가 빌드되었는지 표시
- 📦 **Image Tags**: 생성된 ECR 이미지 태그 정보
- ⏱️ **빌드 시간**: 각 서비스별 빌드 소요 시간

### ECR에서 확인:
```bash
# ECR 이미지 목록 확인
aws ecr describe-images --repository-name api-service --region ap-northeast-2
```

## 🏷️ 이미지 태그 정책

| 조건 | 태그 | 예시 |
|------|------|------|
| main 브랜치 | `latest` | `api-service:latest` |
| 브랜치 push | `브랜치명` | `api-service:develop` |
| PR | `pr-123` | `api-service:pr-123` |
| 커밋 SHA | `브랜치-SHA` | `api-service:main-abc1234` |

## 🚨 문제 해결

### 자주 발생하는 오류:
1. **ECR 접근 권한 오류**: IAM 권한 확인
2. **Dockerfile 빌드 실패**: 각 서비스 디렉터리의 Dockerfile 경로 확인  
3. **변경 감지 실패**: git history depth 확인

### 디버깅 팁:
```bash
# 로컬에서 변경 감지 테스트
git diff --name-only HEAD~1 HEAD

# 특정 서비스만 수동 빌드
docker build -t test-image ./api-service
```

## 📈 성능 최적화
- **Docker Layer 캐싱**: GitHub Actions 캐시 사용
- **병렬 빌드**: Matrix 전략으로 동시 처리
- **조건부 실행**: 변경된 서비스만 빌드하여 리소스 절약

---
💡 **Tip**: 대규모 변경 시에는 `develop` 브랜치에서 먼저 테스트 후 `main`으로 머지하는 것을 권장합니다.
