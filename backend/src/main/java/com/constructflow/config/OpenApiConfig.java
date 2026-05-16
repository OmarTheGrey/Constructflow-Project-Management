package com.constructflow.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ConstructFlow REST API",
                version = "1.0.0",
                description = "Service-Oriented backend for the ConstructFlow construction project " +
                        "management system. Documented for C-SW311 Deliverable #5.",
                contact = @Contact(name = "ConstructFlow Team", email = "team@constructflow.local"),
                license = @License(name = "MIT")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local development server")
        }
)
public class OpenApiConfig {
}
