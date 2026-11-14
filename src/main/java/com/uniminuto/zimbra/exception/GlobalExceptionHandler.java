package com.uniminuto.zimbra.exception;

import com.uniminuto.zimbra.exception.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * Convierte las excepciones en respuestas JSON claras para el frontend (RNF-009).
 * Nunca devuelve trazas ni detalles internos del servidor.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** SQLSTATE que usan los procedimientos almacenados (SIGNAL) para las reglas de negocio. */
    private static final String SQLSTATE_REGLA_NEGOCIO = "45000";

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return respuesta(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return respuesta(HttpStatus.BAD_REQUEST,
                message.isBlank() ? "Los datos enviados no son válidos." : message);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidation(HandlerMethodValidationException ex) {
        String message = ex.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return respuesta(HttpStatus.BAD_REQUEST,
                message.isBlank() ? "Los parámetros enviados no son válidos." : message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return respuesta(HttpStatus.BAD_REQUEST,
                "El formato del JSON enviado es inválido o no coincide con la estructura esperada.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        return respuesta(HttpStatus.BAD_REQUEST,
                "Falta el parámetro obligatorio '" + ex.getParameterName() + "'.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return respuesta(HttpStatus.BAD_REQUEST,
                "El valor del parámetro '" + ex.getName() + "' no es válido.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return respuesta(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no permitido para esta ruta.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex) {
        return respuesta(HttpStatus.NOT_FOUND, "La ruta solicitada no existe.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return respuesta(HttpStatus.BAD_REQUEST,
                ex.getMessage() != null ? ex.getMessage() : "Argumento inválido.");
    }

    /**
     * Errores que vienen de MySQL. Las reglas de negocio de los procedimientos
     * almacenados (SIGNAL SQLSTATE 45000) se devuelven como 400 con su mensaje.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccess(DataAccessException ex) {
        SQLException sql = buscarSqlException(ex);
        if (sql != null && SQLSTATE_REGLA_NEGOCIO.equals(sql.getSQLState())) {
            return respuesta(HttpStatus.BAD_REQUEST, sql.getMessage());
        }
        if (ex instanceof DataIntegrityViolationException) {
            LOG.warn("Violación de integridad: {}", ex.getMostSpecificCause().getMessage());
            return respuesta(HttpStatus.BAD_REQUEST,
                    "Los datos no cumplen las reglas de la base de datos (valor duplicado o fuera de rango).");
        }
        LOG.error("Error de base de datos", ex);
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error al acceder a la base de datos. Intente de nuevo.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        LOG.error("Error inesperado", ex);
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado en el servidor.");
    }

    private ResponseEntity<ErrorResponse> respuesta(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), status.name(), mensaje));
    }

    private SQLException buscarSqlException(Throwable ex) {
        Throwable actual = ex;
        while (actual != null) {
            if (actual instanceof SQLException sql) {
                return sql;
            }
            actual = actual.getCause();
        }
        return null;
    }
}
