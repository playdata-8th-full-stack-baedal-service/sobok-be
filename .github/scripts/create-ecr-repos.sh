#!/bin/bash

# AWS ECR 리포지토리 생성 스크립트
# 사용법: ./create-ecr-repos.sh ap-northeast-2

AWS_REGION=${1:-ap-northeast-2}

echo "🚀 Creating ECR repositories in region: $AWS_REGION"

services=(
    "api-service"
    "auth-service" 
    "config-server"
    "cook-service"
    "delivery-service"
    "gateway-service"
    "payment-service"
    "post-service"
    "shop-service"
    "user-service"
)

for service in "${services[@]}"; do
    echo "📦 Creating repository: $service"
    
    aws ecr create-repository \
        --repository-name "$service" \
        --region "$AWS_REGION" \
        --image-scanning-configuration scanOnPush=true \
        2>/dev/null || echo "Repository $service already exists"
        
    # 라이프사이클 정책 설정 (최근 10개 이미지만 보관)
    aws ecr put-lifecycle-policy \
        --repository-name "$service" \
        --region "$AWS_REGION" \
        --lifecycle-policy-text '{
            "rules": [
                {
                    "rulePriority": 1,
                    "description": "Keep last 10 images",
                    "selection": {
                        "tagStatus": "any",
                        "countType": "imageCountMoreThan",
                        "countNumber": 10
                    },
                    "action": {
                        "type": "expire"
                    }
                }
            ]
        }' >/dev/null 2>&1
done

echo "✅ All ECR repositories created successfully!"
echo ""
echo "📋 Next steps:"
echo "1. Copy your ECR registry URL: $(aws sts get-caller-identity --query Account --output text).dkr.ecr.$AWS_REGION.amazonaws.com"
echo "2. Add it to GitHub Secrets as ECR_REGISTRY"
echo "3. Add AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY to GitHub Secrets"
