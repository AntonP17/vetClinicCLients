package by.antohakon.vetclinitclients.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
@Data
public class GlobalExceptionHandler {

    @Data
    @AllArgsConstructor
    private static class ErrorResponse {
        private String errorType;
        private String message;

    }

    @ExceptionHandler({AnimalNotFoundException.class, OwnerNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleAnimalNotFoundException(RuntimeException ex) {
        log.error("такой сущности не существует: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ErrorResponse(
                        ex.getClass().getSimpleName(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler({AnimalDublicateException.class, OwnerDublicateException.class})
    public ResponseEntity<ErrorResponse> handleAnimalDublicateException(RuntimeException ex) {
        log.error("дубликат сущности: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ErrorResponse(
                        ex.getClass().getSimpleName(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex) {

        log.error("{}: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        ex.getClass().getSimpleName(),
                        ex.getMessage()
                ));
    }
}
