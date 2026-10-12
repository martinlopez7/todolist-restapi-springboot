package com.martin.todo_list.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Todo List API",
                version = "v1",
                description = "API REST de lista de tareas por usuario, protegida con JWT. "
                        + "Obten un token en /auth/register o /auth/login y pulsa Authorize para enviarlo como Bearer."
        ),
        security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
)
@SecurityScheme(
        name = OpenApiConfig.SECURITY_SCHEME_NAME,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "bearerAuth";
}
