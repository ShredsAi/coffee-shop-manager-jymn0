package ai.shreds.adapter.primary;

import ai.shreds.application.exceptions.ApplicationPaymentException;
import ai.shreds.domain.exceptions.DomainPaymentException;
import ai.shreds.shared.dtos.SharedErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class AdapterPaymentExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AdapterPaymentExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SharedErrorResponseDTO> handleException(Exception e, WebRequest request) {
        logger.error("Unexpected error occurred", e);
        SharedErrorResponseDTO errorDTO = SharedErrorResponseDTO.systemError(
            e.getMessage(),
            request.getDescription(false)
        );
        return errorDTO.toResponse();
    }

    @ExceptionHandler(ApplicationPaymentException.class)
    public ResponseEntity<SharedErrorResponseDTO> handlePaymentException(ApplicationPaymentException ex, WebRequest request) {
        logger.error("Payment processing error", ex);
        SharedErrorResponseDTO errorDTO = SharedErrorResponseDTO.paymentProcessingError(
            ex.getMessage(),
            request.getDescription(false)
        );
        return errorDTO.toResponse();
    }

    @ExceptionHandler(DomainPaymentException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleDomainPaymentException(DomainPaymentException ex, WebRequest request) {
        logger.error("Domain payment error", ex);
        SharedErrorResponseDTO errorDTO = SharedErrorResponseDTO.paymentProcessingError(
            ex.getMessage(),
            request.getDescription(false)
        );
        return errorDTO.toResponse();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<SharedErrorResponseDTO> handleValidationException(Exception ex, WebRequest request) {
        logger.warn("Validation error", ex);
        String errorMessage = ex instanceof MethodArgumentNotValidException ?
            ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .reduce("", (a, b) -> a + "; " + b) :
            ex.getMessage();

        SharedErrorResponseDTO errorDTO = SharedErrorResponseDTO.validationError(
            errorMessage,
            request.getDescription(false)
        );
        return errorDTO.toResponse();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.warn("Invalid argument error", ex);
        SharedErrorResponseDTO errorDTO = SharedErrorResponseDTO.validationError(
            ex.getMessage(),
            request.getDescription(false)
        );
        return errorDTO.toResponse();
    }
}
