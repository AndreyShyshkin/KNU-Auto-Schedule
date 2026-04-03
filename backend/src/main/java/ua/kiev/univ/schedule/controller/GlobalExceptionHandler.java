package ua.kiev.univ.schedule.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> response = new HashMap<>();
        String message = "Неможливо видалити об'єкт, оскільки він використовується в інших записах.";
        
        // Спробуємо витягнути деталі про обмеження, якщо це можливо
        String rootCause = ex.getMostSpecificCause().getMessage();
        if (rootCause != null) {
            if (rootCause.contains("violates foreign key constraint")) {
                message += " Існує зв'язок з іншою таблицею (наприклад, цей корпус містить аудиторії або цей викладач призначений на заняття).";
            }
        }
        
        response.put("error", "Data Integrity Violation");
        response.put("message", message);
        response.put("details", rootCause);
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal Server Error");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
