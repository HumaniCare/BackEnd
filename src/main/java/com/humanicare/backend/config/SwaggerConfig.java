package com.humanicare.backend.config;

import com.humanicare.backend.jwt.service.JwtService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        servers = {
                @Server(url = "http://localhost:8080", description = "Local server")
        }
)
@Configuration
public class SwaggerConfig {

    private final JwtService jwtService;

    @Autowired
    public SwaggerConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        String testToken = jwtService.generateTestToken(); // Swagger 용 테스트 토큰 생성
        String test2Token = jwtService.generateTest2Token();

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name("Authorization")
                .description("Test token: " + testToken + " 구분\n" + test2Token); // 디폴트 토큰

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement)
                .info(new Info()
                        .title("HumaniCare API Documentation")
                        .version("1.0")
                        .description("HumaniCare API documentation for the application"));
    }
}
