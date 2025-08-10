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

/**
 * Classe responsável por interceptar exceções lançadas pelas controllers
 * e converter em respostas HTTP com status e mensagens adequadas.
 * <p>
 * Utiliza anotações do Spring {@code @ControllerAdvice} e {@code @ExceptionHandler}
 * para tratamento global e centralizado de erros.
 */
@ControllerAdvice
public class ApiExceptionHandler {

    /**
     * Trata exceções genéricas não tratadas em outros handlers.
     * Retorna erro HTTP 500 - Internal Server Error.
     *
     * @param ex      Exceção capturada.
     * @param request Objeto que contém informações da requisição HTTP.
     * @return ResponseEntity com status 500 e corpo com detalhes do erro.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeral(Exception ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno inesperado",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Trata exceções do tipo {@link RegraNegocioException}.
     * Retorna erro HTTP 400 - Bad Request com a mensagem específica da regra violada.
     *
     * @param ex      Exceção capturada.
     * @param request Objeto com informações da requisição HTTP.
     * @return ResponseEntity com status 400 e detalhes do erro.
     */
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ApiError> handleRegraNegocio(RegraNegocioException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções do tipo {@link RecursoNaoEncontradoException}.
     * Retorna erro HTTP 404 - Not Found.
     *
     * @param ex      Exceção capturada.
     * @param request Informações da requisição HTTP.
     * @return ResponseEntity com status 404 e mensagem de recurso não encontrado.
     */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiError> handleNotFound(RecursoNaoEncontradoException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Trata exceções do tipo {@link EnumInvalidoException}.
     * Retorna erro HTTP 400 - Bad Request para valores inválidos em enums.
     *
     * @param ex      Exceção capturada.
     * @param request Informações da requisição HTTP.
     * @return ResponseEntity com status 400 e mensagem detalhada.
     */
    @ExceptionHandler(EnumInvalidoException.class)
    public ResponseEntity<ApiError> handleEnumInvalido(EnumInvalidoException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções {@link InvalidFormatException} relacionadas a enums inválidos
     * durante o mapeamento JSON.
     * <p>
     * Retorna erro 400 com mensagem que lista valores válidos esperados.
     *
     * @param ex      Exceção capturada.
     * @param request Dados da requisição HTTP.
     * @return ResponseEntity com status 400 e detalhes do erro.
     */
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

    /**
     * Trata exceções relacionadas a token JWT inválido ou expirado.
     * Retorna status HTTP 401 - Unauthorized.
     *
     * @param ex      Exceção capturada.
     * @param request Informações da requisição HTTP.
     * @return ResponseEntity com status 401 e mensagem padrão.
     */
    @ExceptionHandler(JwtInvalidTokenException.class)
    public ResponseEntity<ApiError> handleJwtInvalidToken(JwtInvalidTokenException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Token inválido ou expirado.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Trata falhas na geração de tokens JWT.
     * Retorna status HTTP 500 - Internal Server Error.
     *
     * @param ex      Exceção capturada.
     * @param request Dados da requisição HTTP.
     * @return ResponseEntity com status 500 e mensagem padrão.
     */
    @ExceptionHandler(JwtGenerationException.class)
    public ResponseEntity<ApiError> handleJwtGeneration(JwtGenerationException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno ao processar o token de autenticação.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Trata exceções relacionadas a credenciais inválidas durante autenticação.
     * Retorna status HTTP 401 - Unauthorized com mensagem específica.
     *
     * @param ex      Exceção capturada.
     * @param request Dados da requisição HTTP.
     * @return ResponseEntity com status 401 e mensagem detalhada.
     */
    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ApiError> handleCredenciaisInvalidas(CredenciaisInvalidasException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Trata exceções de permissão não encontrada.
     * Retorna status HTTP 404 - Not Found com mensagem específica.
     *
     * @param ex      Exceção capturada.
     * @param request Informações da requisição HTTP.
     * @return ResponseEntity com status 404 e detalhes da exceção.
     */
    @ExceptionHandler(PermissaoNaoEncontradaException.class)
    public ResponseEntity<ApiError> handlePermissaoNaoEncontrada(PermissaoNaoEncontradaException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Trata exceções de validação de argumentos (@Valid).
     * Extrai as mensagens de erro de cada campo inválido e retorna status 400 - Bad Request.
     *
     * @param ex      Exceção capturada contendo erros de validação.
     * @param request Dados da requisição HTTP.
     * @return ResponseEntity com status 400 e lista de mensagens de erro.
     */
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

    /**
     * Trata erros de JSON malformado ou incompatível no corpo da requisição.
     * Detecta casos especiais relacionados a enums e delega a tratamento específico.
     * Caso contrário, retorna mensagem genérica de erro de sintaxe JSON.
     *
     * @param ex      Exceção capturada durante a leitura da mensagem HTTP.
     * @param request Dados da requisição HTTP.
     * @return ResponseEntity com status 400 e detalhes do erro.
     */
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

    /**
     * Método auxiliar que constrói a resposta para erros relacionados a valores inválidos em enums,
     * formatando a mensagem com o campo, valor inválido e os valores aceitos.
     *
     * @param enumClass    Classe do enum que apresentou erro.
     * @param fieldName    Nome do campo JSON que recebeu valor inválido.
     * @param invalidValue Valor inválido recebido.
     * @param request      Objeto com informações da requisição HTTP.
     * @return ResponseEntity contendo {@link ApiError} com status 400.
     */
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