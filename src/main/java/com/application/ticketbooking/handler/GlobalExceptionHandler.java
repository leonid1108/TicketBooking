package com.application.ticketbooking.handler;

import com.application.ticketbooking.exception.BadRequestException;
import com.application.ticketbooking.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает ошибки, связанные с неправильными запросами.
     *
     * @param exception {@link BadRequestException}.
     * @return {@link ResponseEntity} с сообщением об ошибке и HttpStatus.BAD_REQUEST.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequestException(BadRequestException exception) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("exception", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Обрабатывает ошибки, связанные с отсутствием данных.
     *
     * @param exception {@link EntityNotFoundException}.
     * @return {@link ResponseEntity} с сообщением об ошибке и HttpStatus.NOT_FOUND.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException exception) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("exception", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
