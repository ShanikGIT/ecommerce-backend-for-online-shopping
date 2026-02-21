package com.nikhil.ecommerce_backend.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.BadRequestException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private Map<String, Object> buildErrorBody(HttpStatus status, String errorKey, String messageKey, Object... args) {
        var locale = LocaleContextHolder.getLocale();
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("error", messageSource.getMessage(errorKey, null, locale));
        body.put("message", messageSource.getMessage(messageKey, args, locale));
        return body;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String unsupportedMethod = ex.getMethod();
        String supportedMethods = String.join(", ", ex.getSupportedMethods());
        Object[] args = {unsupportedMethod, supportedMethods};
        Map<String, Object> body = buildErrorBody(
                HttpStatus.METHOD_NOT_ALLOWED,
                "error.method.not.allowed",
                "error.method.not.supported",
                args
        );


        return new ResponseEntity<>(body, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> body = buildErrorBody(
                HttpStatus.FORBIDDEN,
                "error.access.denied",
                ex.getMessage()
        );

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> handleDisabledAccount(DisabledException ex) {

        String messageKey = ex.getMessage();
        Map<String, Object> body = buildErrorBody(
                HttpStatus.UNAUTHORIZED,
                "auth.account.disabled",
                messageKey
        );

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        Map<String, Object> body = buildErrorBody(
                HttpStatus.BAD_REQUEST,
                "error.validation.failed",
                "error.invalid.request.data"
        );
        body.put("details", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();

        Map<String, Object> body = buildErrorBody(
                HttpStatus.BAD_REQUEST,
                "error.validation.failed",
                "error.invalid.request.data"
        );
        body.put("details", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return new ResponseEntity<>(
                buildErrorBody(HttpStatus.UNAUTHORIZED,
                        "error.invalid.login",
                        "auth.invalid.password"),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, Object>> handleLockedAccount(LockedException ex) {
        return new ResponseEntity<>(
                buildErrorBody(HttpStatus.LOCKED,
                        "error.account.locked",
                        "auth.account.locked"),
                HttpStatus.LOCKED
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return new ResponseEntity<>(
                buildErrorBody(HttpStatus.BAD_REQUEST,
                        "error.resource.exists",
                        "email.already.exists"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<Map<String, Object>> handlePasswordMismatch(PasswordMismatchException ex) {
        return new ResponseEntity<>(
                buildErrorBody(HttpStatus.BAD_REQUEST,
                        "password.mismatch",
                        "password.mismatch"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<Map<String, Object>> handleTokenError(TokenException ex) {
        return new ResponseEntity<>(
                buildErrorBody(HttpStatus.UNAUTHORIZED,
                        "error.token.invalid.or.expired",
                        "jwt.token.invalid"),
                HttpStatus.UNAUTHORIZED
        );
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {

        return new ResponseEntity<>(
                buildErrorBody(HttpStatus.NOT_FOUND,
                        "error.resource.not.found",
                        ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        String messageKey = ex.getMessage();

        return new ResponseEntity<>(
                buildErrorBody(HttpStatus.BAD_REQUEST,
                        "error.invalid.request.data",
                        messageKey),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
                buildErrorBody(HttpStatus.INTERNAL_SERVER_ERROR,
                        "internal.server.error",
                        "internal.server.error"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {
        String messageKey = ex.getMessage(); // e.g., "metadata.field.name.exists"
        return new ResponseEntity<>(
                buildErrorBody(HttpStatus.CONFLICT,
                        "error.resource.exists",
                        messageKey),
                HttpStatus.CONFLICT
        );
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UsernameNotFoundException ex) {
        return new ResponseEntity<>(
                buildErrorBody(
                        HttpStatus.NOT_FOUND,
                        "auth.invalid.email",
                        ex.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return new ResponseEntity<>(
                buildErrorBody(
                        HttpStatus.BAD_REQUEST,
                        "error.inactive.resource",
                        ex.getMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(NoResourceFoundException ex) {
        Map<String, Object> body = buildErrorBody(
                HttpStatus.NOT_FOUND,
                "error.resource.not.found",
                ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidEnum(HttpMessageNotReadableException ex) {
        Map<String, Object> body = buildErrorBody(
                HttpStatus.BAD_REQUEST,
                "error.invalid.request.data",
               "address.label.not.found"
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}
