package com.prestacaoservicos.exception;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeral(Exception ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno inesperado",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ApiError> handleRegraNegocio(RegraNegocioException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiError> handleNotFound(RecursoNaoEncontradoException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EnumInvalidoException.class)
    public ResponseEntity<ApiError> handleEnumInvalido(EnumInvalidoException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ApiError> handleInvalidFormatEnum(InvalidFormatException ex, HttpServletRequest request) {
        if (!ex.getPath().isEmpty() && ex.getTargetType().isEnum()) {
            String campo = ex.getPath().get(0).getFieldName();
            Object valorRecebido = ex.getValue();
            Class<?> enumClass = ex.getTargetType();

            List<String> valoresValidos = Arrays.stream(enumClass.getEnumConstants())
                    .map(Object::toString)
                    .toList();

            String mensagem = String.format("Valor inválido para o campo '%s': '%s'. Valores válidos: %s",
                    campo, valorRecebido, valoresValidos);

            ApiError error = new ApiError(
                    HttpStatus.BAD_REQUEST,
                    mensagem,
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Formato inválido no dado recebido",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(JwtInvalidTokenException.class)
    public ResponseEntity<ApiError> handleJwtInvalidToken(JwtInvalidTokenException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Token inválido ou expirado.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(JwtGenerationException.class)
    public ResponseEntity<ApiError> handleJwtGeneration(JwtGenerationException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno ao processar o token de autenticação.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ApiError> handleCredenciaisInvalidas(CredenciaisInvalidasException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(PermissaoNaoEncontradaException.class)
    public ResponseEntity<ApiError> handlePermissaoNaoEncontrada(PermissaoNaoEncontradaException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Erro de validação nos dados enviados",
                request.getRequestURI(),
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Throwable cause = ex.getCause();

        if (cause instanceof ValueInstantiationException vie) {
            JavaType javaType = vie.getType();
            if (javaType != null && javaType.isEnumType()) {
                String fieldName = vie.getPath().isEmpty() ? "desconhecido" : vie.getPath().get(0).getFieldName();
                Class<?> enumClass = javaType.getRawClass();

                Object invalidValue = "desconhecido";
                if (vie.getCause() instanceof EnumInvalidoException eie) {
                    invalidValue = eie.getValorRecebido();
                }

                return buildEnumErrorResponse(enumClass, fieldName, invalidValue, request);
            }
        }

        if (cause instanceof InvalidFormatException ife && ife.getTargetType() != null && ife.getTargetType().isEnum()) {
            String fieldName = ife.getPath().isEmpty() ? "desconhecido" : ife.getPath().get(0).getFieldName();
            return buildEnumErrorResponse(ife.getTargetType(), fieldName, ife.getValue(), request);
        }

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST,
                "JSON malformado ou com tipo de dado inválido.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private ResponseEntity<ApiError> buildEnumErrorResponse(Class<?> enumClass, String fieldName, Object invalidValue, HttpServletRequest request) {
        List<String> validValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Object::toString)
                .toList();

        String message = String.format(
                "Valor inválido para o campo '%s'. Valor recebido: '%s'. Valores aceitos: %s",
                fieldName,
                invalidValue,
                validValues
        );

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}