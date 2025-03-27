package ai.shreds.adapter.primary;

import ai.shreds.application.exceptions.ApplicationInventoryException;
import ai.shreds.shared.dtos.SharedErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdapterExceptionHandler {

    @ExceptionHandler(AdapterInventoryException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleAdapterException(AdapterInventoryException e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(SharedErrorResponseDTO.fromException(e));
    }

    @ExceptionHandler(ApplicationInventoryException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleApplicationException(ApplicationInventoryException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(SharedErrorResponseDTO.fromException(e));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleValidationException(MethodArgumentNotValidException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(SharedErrorResponseDTO.fromException(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SharedErrorResponseDTO> handleGenericException(Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(SharedErrorResponseDTO.fromException(e));
    }
}