package com.sobok.apiservice.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "JWT 토큰을 입력하세요. (Bearer 접두사 제외)"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${openapi.service.url}") String url) {
        return new OpenAPI()
                .info(new Info()
                        .title("api-service API")
                        .version("1.0.0")
                        .description("MSA 기반 API 관리 서비스 API입니다.\n\n- S3 파일 업로드/삭제\n - toss 결제 \n - 소셜 로그인 등...")
                        .contact(new Contact().name("Sobok").email("dev@sobok.shop"))
                        .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT"))
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .servers(List.of(new Server().url(url)));
    }
}