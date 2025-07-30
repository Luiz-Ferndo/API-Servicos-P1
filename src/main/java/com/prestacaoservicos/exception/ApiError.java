package com.prestacaoservicos.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {

    private HttpStatus status;
    private int statusCode;
    private String mensagem;
    private String path;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private List<String> erros;

    public ApiError(HttpStatus status, String mensagem, String path, List<String> erros) {
        this.status = status;
        this.statusCode = status.value();
        this.mensagem = mensagem;
        this.path = path;
        this.erros = erros;
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status, String mensagem, String path) {
        this(status, mensagem, path, null);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<String> getErros() {
        return erros;
    }
}