package com.prestacaoservicos.security.config;

import com.prestacaoservicos.security.authentication.UserAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
            "/api/v1/auth/login",
            "/api/v1/users",
            "/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs/swagger-config",
            "/swagger-ui/swagger-config"
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
     * Configura o filtro de segurança da aplicação.
     * <p>
     * Define as regras de autorização para os endpoints, desabilita o CSRF,
     * e configura a política de sessão como stateless (sem estado).
     *
     * @param http A configuração de segurança HTTP.
     * @return O {@link SecurityFilterChain} configurado.
     * @throws Exception Se ocorrer um erro ao configurar o filtro de segurança.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED).permitAll()
                        .requestMatchers(ENDPOINTS_ADMINISTRATOR).hasRole("ADMINISTRATOR")
                        .requestMatchers(ENDPOINTS_CUSTOMER).hasRole("CUSTOMER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(userAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
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