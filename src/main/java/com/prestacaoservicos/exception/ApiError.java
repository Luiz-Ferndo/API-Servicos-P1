package com.prestacaoservicos.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe que representa a estrutura padrão de erro retornada pela API.
 * <p>
 * Contém informações detalhadas sobre o erro ocorrido, incluindo status HTTP,
 * mensagem, caminho da requisição, timestamp e possíveis erros específicos.
 */
public class ApiError {

    /** Status HTTP da resposta (ex: BAD_REQUEST, NOT_FOUND). */
    private HttpStatus status;

    /** Código numérico do status HTTP (ex: 400, 404). */
    private int statusCode;

    /** Mensagem geral descrevendo o erro ocorrido. */
    private String mensagem;

    /** Caminho da requisição HTTP onde o erro ocorreu. */
    private String path;

    /**
     * Data e hora em que o erro ocorreu.
     * Formato JSON: yyyy-MM-dd HH:mm:ss
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /** Lista de mensagens de erros específicos, como validações de campos. */
    private List<String> erros;

    /**
     * Construtor completo da classe ApiError.
     *
     * @param status    Status HTTP da resposta.
     * @param mensagem  Mensagem geral do erro.
     * @param path      Caminho da requisição que gerou o erro.
     * @param erros     Lista de mensagens específicas de erro (pode ser nula).
     */
    public ApiError(HttpStatus status, String mensagem, String path, List<String> erros) {
        this.status = status;
        this.statusCode = status.value();
        this.mensagem = mensagem;
        this.path = path;
        this.erros = erros;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Construtor simplificado para erros sem lista de erros específicos.
     *
     * @param status   Status HTTP da resposta.
     * @param mensagem Mensagem geral do erro.
     * @param path     Caminho da requisição que gerou o erro.
     */
    public ApiError(HttpStatus status, String mensagem, String path) {
        this(status, mensagem, path, null);
    }

    /**
     * Obtém o status HTTP do erro.
     *
     * @return Status HTTP.
     */
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * Obtém o código numérico do status HTTP.
     *
     * @return Código HTTP.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Obtém a mensagem geral do erro.
     *
     * @return Mensagem de erro.
     */
    public String getMensagem() {
        return mensagem;
    }

    /**
     * Obtém o caminho da requisição que gerou o erro.
     *
     * @return Caminho da requisição.
     */
    public String getPath() {
        return path;
    }

    /**
     * Obtém o timestamp (data e hora) em que o erro ocorreu.
     *
     * @return Timestamp do erro.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Obtém a lista de mensagens de erros específicos, se houver.
     *
     * @return Lista de mensagens de erro ou {@code null} se não houver.
     */
    public List<String> getErros() {
        return erros;
    }
}