package org.acme.pricing.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class GlobalConfig {

    @Value("${h2.server.port}")
    private String h2ServerPort;

    /**
     * Start H2 as local server.
     * Once this application starts and connects to the database, it can be accessed with externally:
     * JDBC URL: `jdbc:h2:tcp://localhost:9090/mem:pricing_module`
     *
     * @return the H2 server
     * @throws SQLException in case of server init errors
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server inMemoryH2DatabaseaServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", h2ServerPort);
    }

    @Bean
    public OpenAPI customOpenAPI(@Value("${app.version}") String appVersion) {
        Map<String, Object> exts = new HashMap<>();
        exts.put("x-api-id", "72d75c22-100d-4f6c-98ea-a820f30cf8d4");
        exts.put("x-audience", "component-internal");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("BearerToken",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT").name("BearerToken")))
                .info(new Info().title("Pricing Module API").version(appVersion)
                        .description("Provides product prices for a given date. The prices are based on different rate lists " +
                                "that apply according to the requested date and time")
                        .license(new License().name("Private").url("https://acme.org/lic"))
                        .contact(new Contact().email("product@acme.org").name("Acme Product Team").url("https://acme.org/product"))
                        .extensions(exts)
                ).externalDocs(new ExternalDocumentation()
                        .description("Product Documentation")
                        .url("https://acme.org/product"));

    }

}
