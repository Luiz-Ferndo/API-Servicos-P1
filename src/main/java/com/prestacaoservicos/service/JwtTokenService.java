package com.prestacaoservicos.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.prestacaoservicos.exception.JwtGenerationException;
import com.prestacaoservicos.exception.JwtInvalidTokenException;
import com.prestacaoservicos.security.userdetails.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Serviço responsável por gerenciar operações relacionadas a JSON Web Tokens (JWT),
 * como geração e validação. As configurações de chave secreta, tempo de expiração
 * e emissor são injetadas a partir das propriedades da aplicação.
 *
 * @version 1.0
 * @since 02/08/2025
 */
@Service
public class JwtTokenService {
    /**
     * Chave secreta utilizada para assinar e verificar os tokens.
     * O valor é injetado da propriedade 'JWT_SECRET_KEY'.
     */
    @Value("${jwt_secret_key}")
    private String SECRET_KEY;

    /**
     * Tempo de expiração do token em horas.
     * O valor é injetado da propriedade 'JWT_EXPIRATION_TIME'.
     */
    @Value("${jwt_expiration_time}")
    private Long EXPIRATION_TIME;

    /**
     * Identificador do emissor (issuer) do token.
     * O valor é injetado da propriedade 'JWT_ISSUER'.
     */
    @Value("${jwt_issuer}")
    private String ISSUER;

    /**
     * Gera um novo token JWT para o usuário especificado.
     * O token conterá o nome de usuário como "subject" e terá um tempo de expiração
     * definido pela propriedade {@code JWT_EXPIRATION_TIME}.
     *
     * @param user O objeto UserDetailsImpl contendo os dados do usuário.
     * @return Uma String contendo o token JWT assinado.
     * @throws JWTCreationException Se ocorrer um erro durante a criação do token.
     */
    public String generateToken(UserDetailsImpl user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withIssuedAt(creationDate())
                    .withExpiresAt(expirationDate())
                    .withSubject(user.getUsername())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new JwtGenerationException("Erro ao gerar o token JWT.", exception);
        }
    }

    /**
     * Valida um token JWT e extrai o "subject" (assunto) dele.
     * Este método verifica a assinatura, o emissor e a data de expiração do token.
     *
     * @param token O token JWT a ser verificado.
     * @return O "subject" (geralmente o nome de usuário) extraído do token.
     * @throws JWTVerificationException Se o token for inválido, expirado ou a assinatura não corresponder.
     */
    public String getSubjectFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            throw new JwtInvalidTokenException("Token inválido ou expirado.", exception);
        }
    }

    /**
     * Retorna o tempo de expiração do token em segundos.
     * @return tempo de expiração configurado (em segundos).
     */
    public long getExpirationInSeconds() {
        return EXPIRATION_TIME * 3600;
    }

    /**
     * Gera o timestamp de criação do token.
     * Utiliza o fuso horário de "America/Recife".
     *
     * @return Um {@link Instant} representando o momento da criação.
     */
    private Instant creationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Recife")).toInstant();
    }

    /**
     * Calcula o timestamp de expiração do token.
     * Adiciona o número de horas definido em {@code EXPIRATION_TIME} ao momento atual.
     *
     * @return Um {@link Instant} representando o momento da expiração.
     */
    private Instant expirationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Recife")).plusHours(EXPIRATION_TIME).toInstant();
    }
}