package br.com.alura.AluraFake.util.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;
import java.util.List;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError>  handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<ErrorMsg> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> ErrorMsg.builder()
                        .message(field.getField() + ": " + field.getDefaultMessage())
                        .build())
                .toList();
        ResponseError body = ResponseError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .errors(errors)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseError> resourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ResponseError body = ResponseError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .errors(List.of(ErrorMsg.builder().message(ex.getMessage()).build()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ResourceIllegalStateException.class)
    public ResponseEntity<ResponseError> resourceIllegalStateException(ResourceIllegalStateException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ResponseError body = ResponseError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .errors(List.of(ErrorMsg.builder().message(ex.getMessage()).build()))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ResourceIllegalArgumentException.class)
    public ResponseEntity<ResponseError> resourceIllegalArgumentException(ResourceIllegalArgumentException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ResponseError body = ResponseError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .errors(List.of(ErrorMsg.builder().message(ex.getMessage()).build()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}