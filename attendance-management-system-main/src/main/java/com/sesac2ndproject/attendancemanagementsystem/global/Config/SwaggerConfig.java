package com.sesac2ndproject.attendancemanagementsystem.global.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "근태관리 시스템 API 명세서",
                version = "1.0.0",
                description = "새싹 출퇴근 근태관리 시스템"
        )
)

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        String jwt = "JWT";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);

        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat(jwt)
        );

        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityRequirement);
    }

}
