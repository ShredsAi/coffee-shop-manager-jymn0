package ai.shreds.adapter.primary;

import ai.shreds.application.exceptions.ApplicationInventoryException;
import ai.shreds.shared.dtos.SharedErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class AdapterInventoryExceptionHandler {

    @ExceptionHandler(AdapterInventoryException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleInventoryException(AdapterInventoryException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(SharedErrorResponseDTO.fromException(ex));
    }

    @ExceptionHandler(ApplicationInventoryException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleApplicationException(ApplicationInventoryException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(SharedErrorResponseDTO.fromException(ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .reduce("", (a, b) -> a + "; " + b);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(SharedErrorResponseDTO.fromException(new Exception("Validation error: " + errorMessage)));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(SharedErrorResponseDTO.fromException(ex));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String error = String.format("Parameter '%s' should be of type %s", 
            ex.getName(), ex.getRequiredType().getSimpleName());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(SharedErrorResponseDTO.fromException(new Exception(error)));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleMissingParams(MissingServletRequestParameterException ex) {
        String error = String.format("Missing parameter: %s of type %s", 
            ex.getParameterName(), ex.getParameterType());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(SharedErrorResponseDTO.fromException(new Exception(error)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SharedErrorResponseDTO> handleGenericException(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(SharedErrorResponseDTO.fromException(ex));
    }
}