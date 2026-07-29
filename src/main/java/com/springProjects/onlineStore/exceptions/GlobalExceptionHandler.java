package com.springProjects.onlineStore.exceptions;

import com.springProjects.onlineStore.common.dto.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDTO> handleResourceNotFoundException(ResourceNotFoundException exception) {
        logger.error("Resource not found: {}", exception.getMessage(), exception);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.NOT_FOUND,
                "Resource not found: " + exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDTO> handleInvalidRequestExceptions(IllegalArgumentException exception) {
        logger.error("Invalid request: {}", exception.getMessage(), exception);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.BAD_REQUEST,
                "Invalid request: " + exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ResponseDTO> handleUnsupportedOperationExceptions(UnsupportedOperationException exception) {
        logger.error("Unsupported operation: {}", exception.getMessage(), exception);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.NOT_IMPLEMENTED,
                "Unsupported operation: " + exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(responseDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO> handleValidationExceptions(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        logger.error("Validation error: {}", errorMessage, exception);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.BAD_REQUEST, errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseDTO> handleIllegalStateException(IllegalStateException exception) {
        logger.error("Illegal state reached: {}", exception.getMessage(), exception);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.CONFLICT,
                "Illegal state reached : " + exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleRemainingExceptions(Exception exception) {
        logger.error("Something went wrong: {}", exception.getMessage(), exception);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Please try again later");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
    }
}
