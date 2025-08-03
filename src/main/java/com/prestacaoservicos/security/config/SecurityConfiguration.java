package com.prestacaoservicos.security.config;

import com.prestacaoservicos.security.authentication.UserAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @TODO: Ajustar quais endpoints devem ser públicos e quais devem exigir autenticação.
 * Classe central de configuração para o Spring Security.
 * <p>
 * Habilita a segurança web e define as regras de autenticação e autorização
 * para os endpoints da aplicação, além de configurar os beans essenciais
 * para o funcionamento da segurança.
 *
 * @version 1.0
 * @since 02/08/2025
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final UserAuthenticationFilter userAuthenticationFilter;

    /**
     * Construtor que recebe o filtro de autenticação personalizado.
     *
     * @param userAuthenticationFilter O filtro de autenticação que processa os tokens JWT.
     */
    public SecurityConfiguration(UserAuthenticationFilter userAuthenticationFilter) {
        this.userAuthenticationFilter = userAuthenticationFilter;
    }

    /**
     * Endpoints públicos que não requerem autenticação.
     */
    public static final String [] ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED = {
            "/api/v1/users/login",
            "/api/v1/users",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    /**
     * Endpoints que exigem que o usuário esteja autenticado.
     */
    public static final String [] ENDPOINTS_WITH_AUTHENTICATION_REQUIRED = {
            "/api/v1/appointments",
            "/api/v1/appointments/**",
            "/api/v1/users/**"
    };

    /**
     * Endpoints acessíveis apenas por usuários com a role 'CUSTOMER'.
     */
    public static final String [] ENDPOINTS_CUSTOMER = {
            "/users/test/customer"
    };

    /**
     * Endpoints acessíveis apenas por usuários com a role 'ADMINISTRATOR'.
     */
    public static final String [] ENDPOINTS_ADMINISTRATOR = {
            "/api/v1/users/test/administrator",
    };

    /**
     * Define a cadeia de filtros de segurança que processa as requisições HTTP.
     * <p>
     * Configurações aplicadas:
     * <ul>
     * <li>Desabilita o CSRF (adequado para APIs stateless).</li>
     * <li>Define a política de sessão como STATELESS (não cria sessões HTTP).</li>
     * <li>Define as permissões de acesso para cada conjunto de endpoints.</li>
     * <li>Bloqueia qualquer requisição não mapeada explicitamente.</li>
     * <li>Adiciona o filtro de autenticação JWT antes do filtro padrão do Spring.</li>
     * </ul>
     *
     * @param httpSecurity Objeto para construção das regras de segurança.
     * @return O bean {@link SecurityFilterChain} configurado.
     * @throws Exception Se ocorrer um erro durante a configuração.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeHttpRequests()
                .requestMatchers(ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED).permitAll()
                .requestMatchers(ENDPOINTS_WITH_AUTHENTICATION_REQUIRED).authenticated()
                .requestMatchers(ENDPOINTS_ADMINISTRATOR).hasRole("ADMINISTRATOR")
                .requestMatchers(ENDPOINTS_CUSTOMER).hasRole("CUSTOMER")
                .anyRequest().denyAll()
                .and().addFilterBefore(userAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Expõe o {@link AuthenticationManager} do Spring Security como um bean gerenciável.
     * <p>
     * Este bean é utilizado em outras partes da aplicação, como no {@code UserService},
     * para processar as tentativas de autenticação.
     *
     * @param authenticationConfiguration A configuração de autenticação do Spring.
     * @return O bean {@link AuthenticationManager}.
     * @throws Exception Se não for possível obter o AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Provê um bean do tipo {@link PasswordEncoder} para a aplicação.
     * <p>
     * Utiliza o BCrypt, um algoritmo de hashing forte e adaptativo, para
     * codificar as senhas dos usuários antes de persisti-las.
     *
     * @return Uma instância de {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}