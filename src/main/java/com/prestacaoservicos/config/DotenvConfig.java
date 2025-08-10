package com.prestacaoservicos.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração para carregamento das variáveis de ambiente a partir do arquivo `.env`.
 * <p>
 * Utiliza a biblioteca {@code dotenv} para carregar as variáveis definidas no arquivo `.env`
 * e as adiciona nas propriedades do sistema para uso pela aplicação.
 * <p>
 * O método {@code init} é executado após a construção do bean para realizar o carregamento.
 *
 * @since 1.0
 * @version 1.0
 */
@Configuration
public class DotenvConfig {

    /**
     * Inicializa o carregamento das variáveis de ambiente do arquivo `.env`
     * e adiciona cada uma nas propriedades do sistema {@link System#setProperty(String, String)}.
     * <p>
     * Este método é executado automaticamente após a criação do bean pelo Spring devido à anotação {@link PostConstruct}.
     */
    @PostConstruct
    public void init() {
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
    }
}