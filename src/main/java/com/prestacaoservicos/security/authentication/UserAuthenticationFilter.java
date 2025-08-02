package com.prestacaoservicos.security.authentication;

import com.prestacaoservicos.entity.User;
import com.prestacaoservicos.exception.JwtInvalidTokenException;
import com.prestacaoservicos.repository.UserRepository;
import com.prestacaoservicos.security.config.SecurityConfiguration;
import com.prestacaoservicos.security.userdetails.UserDetailsImpl;
import com.prestacaoservicos.service.JwtTokenService;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Filtro de autenticação customizado que intercepta todas as requisições para
 * verificar a presença e validade de um token JWT no cabeçalho de autorização.
 * <p>
 * Se um token válido for encontrado, este filtro autentica o usuário no contexto
 * de segurança do Spring (SecurityContextHolder) para a requisição atual.
 *
 * @version 1.0
 * @since 02/08/2025
 */
@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    public UserAuthenticationFilter(JwtTokenService jwtTokenService, UserRepository userRepository) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
    }

    /**
     * Verifica se o endpoint atual requer autenticação e, se necessário,
     * processa o token JWT presente no cabeçalho da requisição.
     *
     * @param request  A requisição HTTP.
     * @param response A resposta HTTP.
     * @param filterChain A cadeia de filtros a serem aplicados.
     * @throws ServletException Se ocorrer um erro durante o processamento do filtro.
     * @throws IOException Se ocorrer um erro de I/O durante o processamento do filtro.
     * @throws JwtInvalidTokenException Se o token JWT for inválido ou expirado.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = recoveryToken(request);

        if (token != null) {
            try {
                String subject = jwtTokenService.getSubjectFromToken(token);

                userRepository.findByEmail(subject)
                        .map(UserDetailsImpl::new)
                        .map(userDetails -> new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()))
                        .ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication));
            } catch (Exception e) {
                throw new JwtInvalidTokenException("Token JWT inválido ou expirado.");
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token JWT do cabeçalho 'Authorization' da requisição.
     *
     * @param request A requisição HTTP.
     * @return O token JWT como uma String, sem o prefixo "Bearer ", ou null se não houver token.
     */
    private String recoveryToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }

    /**
     * Verifica se o endpoint da requisição atual é protegido (não público).
     *
     * @param request A requisição HTTP.
     * @return {@code true} se o endpoint não estiver na lista de endpoints públicos,
     * {@code false} caso contrário.
     */
    private boolean checkIfEndpointIsNotPublic(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return !Arrays.asList(SecurityConfiguration.ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED).contains(requestURI);
    }
}