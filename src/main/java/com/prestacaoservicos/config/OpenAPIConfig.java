package com.prestacaoservicos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração para a documentação OpenAPI (Swagger) da aplicação.
 * <p>
 * Define o bean {@link OpenAPI} personalizado com as informações básicas da API,
 * como título, versão e descrição, que serão exibidas na interface Swagger UI.
 * <p>
 * Esta configuração possibilita a geração automática da documentação da API REST.
 *
 * @since 1.0
 * @version 1.0
 */
@Configuration
public class OpenAPIConfig {

    /**
     * Cria e configura um bean {@link OpenAPI} com informações personalizadas da API.
     *
     * @return objeto {@link OpenAPI} com as informações básicas definidas (título, versão e descrição).
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Agendamento de Serviços")
                        .version("1.0")
                        .description("Documentação Swagger"));
    }
}